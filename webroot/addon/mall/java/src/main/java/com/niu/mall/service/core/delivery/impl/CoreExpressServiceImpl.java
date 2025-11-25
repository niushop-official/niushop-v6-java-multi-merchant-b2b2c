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
import java.util.Map;
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

    private BigDecimal feeCalculate(Map<Integer, List<OrderCreateDataVo.Sku>> dataList,OrderCreateDataVo vo,BigDecimal deliveryMoney) {
        MemberAddress address = (MemberAddress) vo.getDelivery().getTakeAddress();
        for(Integer deliveryTemplateId : dataList.keySet()){
            Integer supplyId = dataList.get(deliveryTemplateId).get(0).getSupplyId();
            DeliveryShippingTemplateItem template = deliveryShippingTemplateItemMapper.selectOne(new QueryWrapper<DeliveryShippingTemplateItem>()
                    .eq("template_id",deliveryTemplateId).eq("city_id", address.getCityId()));
            DeliveryShippingTemplateItem nationwide = null;
            SiteInfoVo siteInfoVo = null;
            if(ObjectUtil.isNotEmpty(supplyId) && supplyId > 0){
                //todo 配送计算需要重新优化
                //查询可用的供货商发货模板
                QueryWrapper<DeliveryShippingTemplate> query = new QueryWrapper<>();
                query.eq("site_id", supplyId);
                query.like("support_trade", "supply");
                query.select("template_id");
                List<Integer> templateIds = deliveryShippingTemplateMapper.selectList(query).stream().map(DeliveryShippingTemplate::getTemplateId).filter(ObjectUtil::isNotEmpty).distinct().toList();
                if(!templateIds.isEmpty()){
                    nationwide = deliveryShippingTemplateItemMapper.selectOne(new QueryWrapper<DeliveryShippingTemplateItem>().eq("template_id", deliveryTemplateId)
                            .eq("city_id", 0).eq("site_id", supplyId).in("template_id", templateIds).last("limit 1"));
                }
                siteInfoVo = coreSiteService.getSiteCache(supplyId);
            }else{
                nationwide = deliveryShippingTemplateItemMapper.selectOne(new QueryWrapper<DeliveryShippingTemplateItem>().eq("template_id", deliveryTemplateId)
                        .eq("city_id", 0).eq("site_id", vo.getSiteId()).last("limit 1"));
                siteInfoVo = coreSiteService.getSiteCache(vo.getSiteId());
            }

            String siteName = siteInfoVo == null ? "" : siteInfoVo.getSiteName();
            if (ObjectUtil.isEmpty(template)) {
                template = nationwide;
                if(ObjectUtil.isEmpty(nationwide)){
                    vo.getError().add("\""+siteName+"\""+"未配置可用的运费模板");
                    return deliveryMoney;
                }
            }else {
                if (template.getSnum().compareTo(0) == 0) template.setSnum(nationwide.getSnum());
                if (template.getSprice().compareTo(BigDecimal.ZERO) == 0) template.setSprice(nationwide.getSprice());
                if (template.getXprice().compareTo(BigDecimal.ZERO) == 0) template.setXprice(nationwide.getXprice());
                if (template.getXnum().compareTo(0) == 0) template.setXnum(nationwide.getXnum());
            }

            //按运费模板计算总量和总金额
            BigDecimal total = BigDecimal.ZERO;
            BigDecimal totalGoodsMoney = BigDecimal.ZERO;
            for (OrderCreateDataVo.Sku item : dataList.get(deliveryTemplateId)){
                Map<String, BigDecimal> map = totalCalculate(template, item, total, totalGoodsMoney);
                if(ObjectUtil.isNotEmpty(map.get("total"))){
                    total = map.get("total");
                }
                if(ObjectUtil.isNotEmpty(map.get("totalGoodsMoney"))){
                    totalGoodsMoney = map.get("totalGoodsMoney");
                }
            }

            for (OrderCreateDataVo.Sku item : dataList.get(deliveryTemplateId)){
                // 判断收货地址是否不配送
                if (ObjectUtil.isNotEmpty(template.getNoDelivery()) && template.getNoDelivery() > 0 && StringUtils.isNotEmpty(template.getNoDeliveryAreaIds())){
                    vo.getGoodsData().get(item.getSkuId()).setNotSupportDelivery(1);
                    vo.getError().add("所选的收货地址不支持配送");
                    return deliveryMoney;
                }
                // 判断收货地址是否包邮
                if (ObjectUtil.isNotEmpty(template.getIsFreeShipping()) && template.getIsFreeShipping() > 0 && StringUtils.isNotEmpty(template.getFreeShippingAreaIds()) &&
                        (total.compareTo(new BigDecimal(template.getFreeShippingNum())) >= 0 || totalGoodsMoney.compareTo(template.getFreeShippingPrice()) >= 0)) {
                    vo.getGoodsData().get(item.getSkuId()).setIsFreeShipping(1);
                    return deliveryMoney;
                }
            }

            // 计算费用
            BigDecimal itemDeliveryMoney = template.getSprice();
            if (total.compareTo(new BigDecimal(template.getSnum())) > 0 && template.getXnum() > 0) {
                BigDecimal xprice = total.subtract(new BigDecimal(template.getSnum())).divide(new BigDecimal(template.getXnum()), 2, BigDecimal.ROUND_HALF_UP).multiply(template.getXprice());
                itemDeliveryMoney = itemDeliveryMoney.add(xprice);
            }
            deliveryMoney = deliveryMoney.add(itemDeliveryMoney);

            for(OrderCreateDataVo.Sku item : dataList.get(deliveryTemplateId)){
                BigDecimal deliveryMoneyThis = itemDeliveryMoney.divide(new BigDecimal(dataList.get(deliveryTemplateId).size()),2, RoundingMode.HALF_UP);
                vo.getGoodsData().get(item.getSkuId()).setDeliveryMoney(deliveryMoneyThis);
            }
        }
        return deliveryMoney;
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
