package com.niu.mall.service.core.delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.delivery.DeliveryShippingTemplate;
import com.niu.core.entity.member.MemberAddress;
import com.niu.core.entity.delivery.DeliveryShippingTemplateItem;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.delivery.DeliveryTypeEnum;
import com.niu.core.enums.delivery.ShippingTemplateEnum;
import com.niu.core.mapper.delivery.DeliveryShippingTemplateMapper;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.core.site.ICoreSiteService;
import com.niu.mall.enums.order.OrderDeliveryTypeEnum;
import com.niu.core.mapper.delivery.DeliveryShippingTemplateItemMapper;
import com.niu.mall.service.core.delivery.ICoreExpressService;
import com.niu.mall.service.core.order.vo.OrderCreateDataVo;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CoreExpressServiceImpl implements ICoreExpressService {

    @Resource
    DeliveryShippingTemplateItemMapper deliveryShippingTemplateItemMapper;
    @Resource
    DeliveryShippingTemplateMapper deliveryShippingTemplateMapper;
    @Resource
    ICoreSiteService coreSiteService;
    /**
     * 计算运费
     *
     * @param vo
     */
    @Override
    public void calculate(OrderCreateDataVo vo) {
        if (ObjectUtil.isEmpty(vo.getDelivery().getTakeAddress())) {
            vo.getError().add("需要先选择收货地址");
            return;
        }
        List<OrderCreateDataVo.Sku> templateGoodsData = new ArrayList<>();
        BigDecimal deliveryMoney = new BigDecimal(0);
        for (OrderCreateDataVo.Sku item : vo.getGoodsData().values()) {
            OrderCreateDataVo.Goods goods = item.getGoods();
            if (goods.getGoodsType().equals("real")) {
                if (goods.getDeliveryType().contains(DeliveryTypeEnum.EXPRESS.getType())){
                    if(goods.getIsFreeShipping().equals(0)){
                        if (goods.getFeeType().equals("template")) {
                            templateGoodsData.add(item);
                        }else{
                            item.setDeliveryMoney(goods.getDeliveryMoney().multiply(new BigDecimal(item.getNum())));
                        }
                        deliveryMoney = deliveryMoney.add(ObjectUtil.isNotEmpty(item.getDeliveryMoney()) ? item.getDeliveryMoney() : BigDecimal.ZERO);
                    }
                }else{
                    if (!vo.getOrderFrom().contains(ChannelEnum.PC.getCode())){
                        item.setNotSupportDelivery(1);
                        vo.getError().add("该商品不支持物流配送");
                    }
                }
            }
        }
        if (!templateGoodsData.isEmpty()){
            //按照运费末班进行分组 相同运费模版 商品处理
            Map<Integer, List<OrderCreateDataVo.Sku>> templateGoodsDataMap = templateGoodsData.stream().collect(Collectors.groupingBy(item -> item.getGoods().getDeliveryTemplateId()));
            deliveryMoney =  feeCalculate(templateGoodsDataMap, vo, deliveryMoney);
        }
        vo.getBasic().setDeliveryMoney(deliveryMoney);
    }

    public void calculate() {

    }

    private BigDecimal feeCalculate(Map<Integer, List<OrderCreateDataVo.Sku>> dataList, OrderCreateDataVo vo, BigDecimal deliveryMoney) {
        MemberAddress address = (MemberAddress) vo.getDelivery().getTakeAddress();
        for (Integer deliveryTemplateId : dataList.keySet()) {
            Integer supplyId = dataList.get(deliveryTemplateId).get(0).getSupplyId();
            Integer siteId = ObjectUtil.isNotEmpty(supplyId) && supplyId > 0 ? supplyId : vo.getSiteId();

            // 取出该运费模板下的全部区域项（含全国默认 city_id=0），再根据收货地址区域精确匹配
            List<DeliveryShippingTemplateItem> allItems = deliveryShippingTemplateItemMapper.selectList(
                    new QueryWrapper<DeliveryShippingTemplateItem>().eq("template_id", deliveryTemplateId).eq("site_id", siteId));
            // 不配送/包邮的总开关在主模板（ns_delivery_shipping_template），区域明细在子表 item
            DeliveryShippingTemplate mainTemplate = deliveryShippingTemplateMapper.selectOne(
                    new QueryWrapper<DeliveryShippingTemplate>().eq("template_id", deliveryTemplateId).eq("site_id", siteId));

            SiteInfoVo siteInfoVo = coreSiteService.getSiteCache(siteId);
            String siteName = siteInfoVo == null ? "" : siteInfoVo.getSiteName();
            if (allItems.isEmpty()) {
                vo.getError().add("\"" + siteName + "\"" + "未配置可用的运费模板");
                return deliveryMoney;
            }

            // 不配送地区校验：主模板 no_delivery=1 且 收货地址的省/市/区落入子表 no_delivery_area_ids
            boolean noDeliverySwitch = ObjectUtil.isNotEmpty(mainTemplate) && ObjectUtil.isNotEmpty(mainTemplate.getNoDelivery()) && mainTemplate.getNoDelivery() > 0;
            if (noDeliverySwitch) {
                boolean inNoDeliveryArea = false;
                boolean hasNoDeliveryArea = false;
                for (DeliveryShippingTemplateItem item : allItems) {
                    if (StringUtils.isNotEmpty(item.getNoDeliveryAreaIds())) {
                        hasNoDeliveryArea = true;
                        if (isAddressInAreaIds(address, item.getNoDeliveryAreaIds())) {
                            inNoDeliveryArea = true;
                            break;
                        }
                    }
                }
                // 开关开启但未配置具体区域时，视为整模板不配送
                if (inNoDeliveryArea || !hasNoDeliveryArea) {
                    for (OrderCreateDataVo.Sku sku : dataList.get(deliveryTemplateId)) {
                        vo.getGoodsData().get(sku.getSkuId()).setNotSupportDelivery(1);
                    }
                    vo.getError().add("所选的收货地址不支持配送");
                    return deliveryMoney;
                }
            }

            // 选取适用于该收货地址的计费项：优先按 fee_area_ids 命中，其次按 city_id 相等，否则全国默认
            DeliveryShippingTemplateItem template = null;
            DeliveryShippingTemplateItem nationwide = null;
            for (DeliveryShippingTemplateItem item : allItems) {
                if (ObjectUtil.isEmpty(item.getCityId())) {
                    continue;
                }
                if (item.getCityId() == 0) {
                    if (nationwide == null) {
                        nationwide = item;
                    }
                    continue;
                }
                if (template == null && (isAddressInAreaIds(address, item.getFeeAreaIds()) || item.getCityId().equals(address.getCityId()))) {
                    template = item;
                }
            }
            if (template == null) {
                template = nationwide;
            }
            if (template == null) {
                vo.getError().add("\"" + siteName + "\"" + "未配置可用的运费模板");
                return deliveryMoney;
            }
            if (nationwide != null) {
                if (template.getSnum().compareTo(0) == 0) template.setSnum(nationwide.getSnum());
                if (template.getSprice().compareTo(BigDecimal.ZERO) == 0) template.setSprice(nationwide.getSprice());
                if (template.getXprice().compareTo(BigDecimal.ZERO) == 0) template.setXprice(nationwide.getXprice());
                if (template.getXnum().compareTo(0) == 0) template.setXnum(nationwide.getXnum());
            }

            // 按运费模板计算总量和总金额
            BigDecimal total = BigDecimal.ZERO;
            BigDecimal totalGoodsMoney = BigDecimal.ZERO;
            for (OrderCreateDataVo.Sku item : dataList.get(deliveryTemplateId)) {
                Map<String, BigDecimal> map = totalCalculate(template, item, total, totalGoodsMoney);
                if (ObjectUtil.isNotEmpty(map.get("total"))) {
                    total = map.get("total");
                }
                if (ObjectUtil.isNotEmpty(map.get("totalGoodsMoney"))) {
                    totalGoodsMoney = map.get("totalGoodsMoney");
                }
            }

            // 包邮校验：总开关在主模板 is_free_shipping=1，且 收货地址命中子表 free_shipping_area_ids 并满足门槛
            boolean freeShippingSwitch = ObjectUtil.isNotEmpty(mainTemplate) && ObjectUtil.isNotEmpty(mainTemplate.getIsFreeShipping()) && mainTemplate.getIsFreeShipping() > 0;
            if (freeShippingSwitch) {
                for (DeliveryShippingTemplateItem item : allItems) {
                    if (StringUtils.isNotEmpty(item.getFreeShippingAreaIds())
                            && isAddressInAreaIds(address, item.getFreeShippingAreaIds())
                            && (total.compareTo(new BigDecimal(item.getFreeShippingNum())) >= 0 || totalGoodsMoney.compareTo(item.getFreeShippingPrice()) >= 0)) {
                        for (OrderCreateDataVo.Sku sku : dataList.get(deliveryTemplateId)) {
                            vo.getGoodsData().get(sku.getSkuId()).setIsFreeShipping(1);
                        }
                        return deliveryMoney;
                    }
                }
            }

            // 计算费用
            BigDecimal itemDeliveryMoney = template.getSprice();
            if (total.compareTo(new BigDecimal(template.getSnum())) > 0 && template.getXnum() > 0) {
                BigDecimal xprice = total.subtract(new BigDecimal(template.getSnum())).divide(new BigDecimal(template.getXnum()), 2, BigDecimal.ROUND_HALF_UP).multiply(template.getXprice());
                itemDeliveryMoney = itemDeliveryMoney.add(xprice);
            }
            deliveryMoney = deliveryMoney.add(itemDeliveryMoney);

            for (OrderCreateDataVo.Sku item : dataList.get(deliveryTemplateId)) {
                BigDecimal deliveryMoneyThis = itemDeliveryMoney.divide(new BigDecimal(dataList.get(deliveryTemplateId).size()), 2, RoundingMode.HALF_UP);
                vo.getGoodsData().get(item.getSkuId()).setDeliveryMoney(deliveryMoneyThis);
            }
        }
        return deliveryMoney;
    }

    /**
     * 判断收货地址的省/市/区是否落入配置的区域 id 集（逗号分隔）
     */
    private boolean isAddressInAreaIds(MemberAddress address, String areaIds) {
        if (StringUtils.isEmpty(areaIds)) {
            return false;
        }
        Set<Integer> addrAreas = new HashSet<>();
        if (ObjectUtil.isNotEmpty(address.getProvinceId())) addrAreas.add(address.getProvinceId());
        if (ObjectUtil.isNotEmpty(address.getCityId())) addrAreas.add(address.getCityId());
        if (ObjectUtil.isNotEmpty(address.getDistrictId())) addrAreas.add(address.getDistrictId());
        if (addrAreas.isEmpty()) {
            return false;
        }
        for (String id : areaIds.split(",")) {
            if (StringUtils.isEmpty(id)) {
                continue;
            }
            try {
                if (addrAreas.contains(Integer.parseInt(id.trim()))) {
                    return true;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return false;
    }

    public Map<String,BigDecimal> totalCalculate(DeliveryShippingTemplateItem template,OrderCreateDataVo.Sku goods,BigDecimal total,BigDecimal totalGoodsMoney){
        totalGoodsMoney = totalGoodsMoney.add(goods.getGoodsMoney());
        // 按件
        if (template.getFeeType().equals(ShippingTemplateEnum.NUM.getType())) {
            total = total.add(new BigDecimal(goods.getNum()));
        }
        // 按重量
        if (template.getFeeType().equals(ShippingTemplateEnum.WEIGHT.getType())) {
            total = total.add(goods.getVolume().multiply(new BigDecimal(goods.getNum())));
        }
        // 按体积
        if (template.getFeeType().equals(ShippingTemplateEnum.VOLUME.getType())) {
            total = total.add(goods.getWeight().multiply(new BigDecimal(goods.getNum())));
        }

        return Map.of("total",total,"totalGoodsMoney",totalGoodsMoney);
    }
}
