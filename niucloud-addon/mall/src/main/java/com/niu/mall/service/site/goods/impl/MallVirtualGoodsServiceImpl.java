package com.niu.mall.service.site.goods.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.diy_form.DiyForm;
import com.niu.core.entity.goods.GoodsBrand;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.diy_form.DiyFormMapper;
import com.niu.core.mapper.goods.GoodsBrandMapper;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.entity.goods.MallGoods;
import com.niu.core.entity.goods.MallGoodsSku;
import com.niu.mall.entity.goods.MallGoodsSpec;
import com.niu.core.enums.goods.GoodsAuditStatusEnum;
import com.niu.mall.enums.goods.GoodsFieldEnum;
import com.niu.mall.event.goods.AfterGoodsEditEvent;
import com.niu.mall.event.goods.CheckGoodsSkuPriceEvent;
import com.niu.mall.event.goods.SupplyGoodsSyncEventDefiner;
import com.niu.core.mapper.goods.MallGoodsMapper;
import com.niu.core.mapper.goods.MallGoodsSkuMapper;
import com.niu.mall.mapper.goods.MallGoodsSpecMapper;
import com.niu.mall.service.core.goods.ICoreGoodsConfigService;
import com.niu.mall.service.site.goods.IMallGoodsService;
import com.niu.mall.service.site.goods.IMallGoodsSkuService;
import com.niu.mall.service.site.goods.IMallGoodsSpecService;
import com.niu.mall.service.site.goods.IMallVirtualGoodsService;
import com.niu.mall.service.site.goods.helper.GoodsAddExtendParam;
import com.niu.mall.service.site.goods.helper.GoodsAddHelper;
import com.niu.mall.service.site.goods.helper.GoodsAddSourceParam;
import com.niu.mall.service.site.goods.helper.GoodsInitHelper;
import com.niu.mall.service.site.goods.param.MallVirtualGoodsParam;
import com.niu.mall.service.site.goods.vo.MallVirtualGoodsInitVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MallVirtualGoodsServiceImpl implements IMallVirtualGoodsService {

    @Resource
    MallGoodsMapper mallGoodsMapper;

    @Resource
    MallGoodsSkuMapper mallGoodsSkuMapper;

    @Resource
    MallGoodsSpecMapper mallGoodsSpecMapper;

    @Resource
    GoodsBrandMapper goodsBrandMapper;

    @Resource
    IMallGoodsSkuService shopGoodsSkuService;

    @Resource
    IMallGoodsSpecService shopGoodsSpecService;

    @Resource
    IMallGoodsService shopGoodsService;

    @Resource
    AddonMapper addonMapper;

    @Resource
    DiyFormMapper diyFormMapper;

    @Resource
    ICoreAddonService coreAddonService;

    @Resource
    ICoreGoodsConfigService coreGoodsConfigService;

    @Resource
    GoodsInitHelper goodsInitHelper;

    /**
     * 商品添加
     *
     * @param addParam 参数
     */
    @Override
    @Transactional
    public Integer add(MallVirtualGoodsParam addParam) {
        Integer siteId = RequestUtils.siteId();

        // 添加goods表
        MallGoods model = new MallGoods();
        BeanUtils.copyProperties(addParam, model);
        model.setSiteId(siteId);
        model.setGoodsCover(addParam.getGoodsImage().split(",")[0]);
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setGoodsCategory(JSONUtil.parseArray(intArrToStrArr(addParam.getGoodsCategory())).toString());
        model.setLabelIds(JSONUtil.parseArray(intArrToStrArr(addParam.getLabelIds())).toString());
        model.setServiceIds(JSONUtil.parseArray(intArrToStrArr(addParam.getServiceIds())).toString());
        model.setShopSort(addParam.getShopSort());
        model.setStatus(addParam.getStatus());
        boolean verifyOpen = coreGoodsConfigService.getConfig().getIsVerifyOpen().equals(1);
        model.setAuditStatus(verifyOpen ?
                GoodsAuditStatusEnum.WAIT_AUDIT.getStatus() : GoodsAuditStatusEnum.AUDIT_ADOPT.getStatus());
        model.setAttrIds(ObjectUtil.isNotEmpty(addParam.getAttrIds()) ? addParam.getAttrIds().toString() : "");
        model.setShopAttrIds(ObjectUtil.isNotEmpty(addParam.getShopAttrIds()) ? addParam.getShopAttrIds().toString() : "");
        model.setAttrFormat(ObjectUtil.isNotEmpty(addParam.getAttrFormat()) ? addParam.getAttrFormat().toString() : "");
        model.setFormId(addParam.getFormId());
        model.setVirtualVerifyMobile(ObjectUtil.isNotEmpty(addParam.getVirtualVerifyMobile()) ? addParam.getVirtualVerifyMobile() : 0);
        applyVirtualDeliveryConfig(model, addParam);
        GoodsAddExtendParam extendParam = GoodsAddExtendParam.from(addParam, verifyOpen);
        GoodsAddHelper.applyAddExtendFields(model, extendParam);
        GoodsAddSourceParam goodsSource = extendParam.toGoodsSourceParam();
        if (model.getUnit().isEmpty()) {
            model.setUnit("件");
        }
        mallGoodsMapper.insert(model);

        // 规格数据
        List<MallGoodsSku> skuData = new LinkedList<>();
        if (addParam.getSpecType().equals("single")) {
            MallGoodsSku sku = new MallGoodsSku();
            BeanUtils.copyProperties(addParam, sku);
            sku.setSiteId(siteId);
            sku.setGoodsId(model.getGoodsId());
            sku.setSkuImage(model.getGoodsCover());
            sku.setSalePrice(addParam.getPrice());
            sku.setIsDefault(1);
            sku.setIsSell(1);
            sku.setSkuSpecFormat("");
            // 指定会员价
            sku.setMemberPrice(GoodsAddHelper.encodeMemberPrice(addParam.getMemberDiscount(), addParam.getMemberPrice()));
            GoodsAddHelper.applySkuSourceFields(sku, goodsSource, null);
            skuData.add(sku);
        } else {
            if (addParam.getGoodsSkuData().keySet().size() == 0) throw new CommonException("请添加商品规格");

            Integer defaultCount = 0;
            for (String key : addParam.getGoodsSkuData().keySet()) {
                MallVirtualGoodsParam.SkuData skuParam = addParam.getGoodsSkuData().get(key);

                List<String> skuSpecFormat = new LinkedList<>();
                for (int i = 0; i < skuParam.getSkuSpec().size(); i++) {
                    JSONObject spec = skuParam.getSkuSpec().getJSONObject(i);
                    skuSpecFormat.add(spec.getStr("spec_value_name", ""));
                }

                MallGoodsSku sku = new MallGoodsSku();
                BeanUtils.copyProperties(skuParam, sku);
                sku.setSiteId(siteId);
                sku.setSkuName(skuParam.getSpecName());
                sku.setSkuImage(skuParam.getSkuImage().isEmpty() ? model.getGoodsCover() : skuParam.getSkuImage());
                sku.setSalePrice(skuParam.getPrice());
                sku.setGoodsId(model.getGoodsId());
                sku.setSkuSpecFormat(String.join(",", skuSpecFormat));
                if (skuParam.getIsDefault().equals(1)) {
                    defaultCount++;
                    if (defaultCount > 1) throw new CommonException("商品不能有多个默认规格");
                }
                sku.setMemberPrice(GoodsAddHelper.encodeMemberPrice(addParam.getMemberDiscount(), skuParam.getMemberPrice()));
                GoodsAddHelper.applySkuSourceFields(sku, goodsSource, GoodsAddSourceParam.of(
                        addParam.getSourceType(),
                        skuParam.getSourceGoodsId(),
                        skuParam.getSourceSkuId(),
                        skuParam.getSourceFlatform(),
                        skuParam.getSourceParty()
                ));
                skuData.add(sku);
            }
            if (defaultCount.equals(0)) throw new CommonException("商品缺少默认规格");

            // 添加商品规格值
            List<MallGoodsSpec> specData = new LinkedList<>();
            for (int i = 0; i < addParam.getGoodsSpecFormat().size(); i++) {
                JSONObject goodsSpec = addParam.getGoodsSpecFormat().getJSONObject(i);

                MallGoodsSpec spec = new MallGoodsSpec();
                spec.setGoodsId(model.getGoodsId());
                spec.setSiteId(siteId);
                spec.setSpecName(goodsSpec.getStr("spec_name", ""));

                List<String> specValues = new LinkedList<>();
                for (int v = 0; v < goodsSpec.getJSONArray("values").size(); v++) {
                    JSONObject value = goodsSpec.getJSONArray("values").getJSONObject(v);
                    specValues.add(value.getStr("spec_value_name", ""));
                }

                spec.setSpecValues(String.join(",", specValues));
                specData.add(spec);
            }
            shopGoodsSpecService.insertAll(specData);
        }

        shopGoodsSkuService.insertAll(skuData);

        // 商品添加之后事件
        AfterGoodsEditEvent event = new AfterGoodsEditEvent();
        event.setSiteId(siteId);
        event.addAppSign("mall");
        event.setName("AfterGoodsEditEvent");
        event.setGoodsId(model.getGoodsId());
        event.setGoodsData(model);
        event.setSkuData(skuData);
        EventAndSubscribeOfPublisher.publishAll(event);
        return model.getGoodsId();
    }

    /**
     * 对齐 PHP VirtualGoodsService：按发货方式规范化虚拟商品收发货配置
     */
    private void applyVirtualDeliveryConfig(MallGoods model, MallVirtualGoodsParam param) {
        String deliveryType = ObjectUtil.defaultIfEmpty(param.getVirtualDeliveryType(), "auto");
        model.setVirtualDeliveryType(deliveryType);
        if ("auto".equals(deliveryType) || "manual".equals(deliveryType)) {
            model.setVirtualDeliveryContent(ObjectUtil.defaultIfNull(param.getVirtualDeliveryContent(), ""));
            model.setVirtualReceiveType(ObjectUtil.defaultIfEmpty(param.getVirtualReceiveType(), "artificial"));
            model.setVirtualVerifyType(0);
            model.setVirtualIndate(0);
            model.setVirtualRefund(0);
            model.setVirtualRefundDay(0);
        } else if ("verify".equals(deliveryType)) {
            model.setVirtualDeliveryContent("");
            model.setVirtualReceiveType("artificial");
            Integer virtualVerifyType = ObjectUtil.defaultIfNull(param.getVirtualVerifyType(), 0);
            Integer virtualIndate = ObjectUtil.defaultIfNull(param.getVirtualIndate(), 0);
            if (virtualVerifyType.equals(1) && virtualIndate < 1) {
                throw new CommonException("核销有效期不能小于1天");
            }
            model.setVirtualVerifyType(virtualVerifyType);
            model.setVirtualIndate(virtualIndate);
            model.setVirtualRefund(ObjectUtil.defaultIfNull(param.getVirtualRefund(), 0));
            model.setVirtualRefundDay(ObjectUtil.defaultIfNull(param.getVirtualRefundDay(), 0));
        }
    }

    /**
     * int 数组转 str 数组
     *
     * @param arr
     * @return
     */
    private String[] intArrToStrArr(Integer[] arr) {
        String[] strArr = new String[arr.length];

        for (int i = 0; i < arr.length; i++) {
            strArr[i] = String.valueOf(arr[i]);
        }
        return strArr;
    }

    /**
     * int 数组转 str 数组
     *
     * @param arr
     * @return
     */
    private Integer[] strArrToIntArr(String[] arr) {
        Integer[] intArr = new Integer[arr.length];

        for (int i = 0; i < arr.length; i++) {
            intArr[i] = Integer.valueOf(arr[i]);
        }
        return intArr;
    }

    /**
     * 商品编辑
     *
     * @param editParam 参数
     */
    @Override
    @Transactional
    public void edit(Integer id, MallVirtualGoodsParam editParam) {
        Integer siteId = RequestUtils.siteId();
        // 校验商品规格价格
        editCheckGoodsSkuPrice(id, editParam);
        MallGoods model = mallGoodsMapper.selectOne(new QueryWrapper<MallGoods>().eq("goods_id", id).eq("site_id", siteId).eq("delete_time", 0));
        Assert.notNull(model, "商品不存在");

        Long activeGoodsCount = shopGoodsService.getActiveGoodsCount(id);
        // 如果有正在进行的活动不允许下架
        if (editParam.getStatus().equals(0) && activeGoodsCount > 0)
            throw new CommonException("商品正在参与营销活动，禁止修改");

        BeanUtils.copyProperties(editParam, model);
        model.setGoodsCover(editParam.getGoodsImage().split(",")[0]);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        model.setGoodsCategory(JSONUtil.parseArray(intArrToStrArr(editParam.getGoodsCategory())).toString());
        model.setLabelIds(JSONUtil.parseArray(intArrToStrArr(editParam.getLabelIds())).toString());
        model.setServiceIds(JSONUtil.parseArray(intArrToStrArr(editParam.getServiceIds())).toString());
        model.setAttrIds(ObjectUtil.isNotEmpty(editParam.getAttrIds()) ? editParam.getAttrIds().toString() : "");
        model.setShopAttrIds(ObjectUtil.isNotEmpty(editParam.getShopAttrIds()) ? editParam.getShopAttrIds().toString() : "");
        model.setAttrFormat(ObjectUtil.isNotEmpty(editParam.getAttrFormat()) ? editParam.getAttrFormat().toString() : "");
        model.setFormId(editParam.getFormId());
        model.setSupplySync(ObjectUtil.isNotEmpty(editParam.getSupplySync()) ? editParam.getSupplySync() : 0);
        model.setVirtualVerifyMobile(ObjectUtil.isNotEmpty(editParam.getVirtualVerifyMobile()) ? editParam.getVirtualVerifyMobile() : 0);
        applyVirtualDeliveryConfig(model, editParam);
        model.setStatus(editParam.getStatus());
        model.setAuditStatus(coreGoodsConfigService.getConfig().getIsVerifyOpen().equals(1) ?
                GoodsAuditStatusEnum.WAIT_AUDIT.getStatus() : GoodsAuditStatusEnum.AUDIT_ADOPT.getStatus());
        if (model.getUnit().isEmpty()) {
            model.setUnit("件");
        }
        mallGoodsMapper.updateById(model);

        // 规格数据
        List<MallGoodsSku> skuData = new LinkedList<>();
        if (editParam.getSpecType().equals("single")) {
            List<MallGoodsSku> skuList = mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().eq("goods_id", id));
            if (skuList.size() > 1) {
                // 删除原来的多规格
                mallGoodsSkuMapper.delete(new QueryWrapper<MallGoodsSku>().eq("goods_id", id));

                MallGoodsSku sku = new MallGoodsSku();
                BeanUtils.copyProperties(editParam, sku);
                sku.setSiteId(siteId);
                sku.setGoodsId(model.getGoodsId());
                sku.setSkuImage(model.getGoodsCover());
                sku.setSalePrice(editParam.getPrice());
                sku.setSkuSpecFormat("");
                sku.setIsDefault(1);
                sku.setIsSell(1);
                sku.setMemberPrice(GoodsAddHelper.encodeMemberPrice(editParam.getMemberDiscount(), editParam.getMemberPrice()));
                skuData.add(sku);
            } else {
                MallGoodsSku sku = skuList.get(0);
                sku.setSkuImage(model.getGoodsCover());
                sku.setSkuNo(editParam.getSkuNo());
                sku.setSkuName("");
                sku.setSkuSpecFormat("");
                sku.setCostPrice(editParam.getCostPrice());
                sku.setMarketPrice(editParam.getMarketPrice());
                sku.setIsDefault(1);
                sku.setIsSell(1);
                if (activeGoodsCount == 0) {
                    sku.setPrice(editParam.getPrice());
                    sku.setSalePrice(editParam.getPrice());
                    sku.setStock(editParam.getStock());
                }
                sku.setMemberPrice(GoodsAddHelper.encodeMemberPrice(editParam.getMemberDiscount(), editParam.getMemberPrice()));
                mallGoodsSkuMapper.updateById(sku);
            }
            // 删除原规格值
            mallGoodsSpecMapper.delete(new QueryWrapper<MallGoodsSpec>().eq("goods_id", id));
        } else {
            if (editParam.getGoodsSkuData().keySet().size() == 0) throw new CommonException("请添加商品规格");

            MallVirtualGoodsParam.SkuData firstSkuData = editParam.getGoodsSkuData().get(editParam.getGoodsSkuData().keySet().iterator().next());
            if (activeGoodsCount > 0 && firstSkuData.getSkuId() == null) {
                throw new CommonException("商品正在参与营销活动，禁止修改");
            }

            // 检测规格项是否发生变化
            if (firstSkuData.getSkuId() != null) {
                List<Integer> skuIdArr = new LinkedList<>();

                Integer defaultCount = 0;
                for (String key : editParam.getGoodsSkuData().keySet()) {
                    MallVirtualGoodsParam.SkuData skuParam = editParam.getGoodsSkuData().get(key);

                    List<String> skuSpecFormat = new LinkedList<>();
                    for (int i = 0; i < skuParam.getSkuSpec().size(); i++) {
                        JSONObject spec = skuParam.getSkuSpec().getJSONObject(i);
                        skuSpecFormat.add(spec.getStr("spec_value_name", ""));
                    }

                    MallGoodsSku sku = new MallGoodsSku();
                    if (skuParam.getSkuId() != null) {
                        sku.setSkuId(skuParam.getSkuId());
                        skuIdArr.add(skuParam.getSkuId());
                    }
                    sku.setSiteId(siteId);
                    sku.setGoodsId(id);
                    sku.setSkuNo(skuParam.getSkuNo());
                    sku.setSkuName(skuParam.getSpecName());
                    sku.setSkuImage(skuParam.getSkuImage().isEmpty() ? model.getGoodsCover() : skuParam.getSkuImage());
                    sku.setSkuSpecFormat(String.join(",", skuSpecFormat));
                    sku.setCostPrice(skuParam.getCostPrice());
                    sku.setMarketPrice(skuParam.getMarketPrice());
                    sku.setIsDefault(skuParam.getIsDefault());
                    sku.setIsSell(skuParam.getIsSell());
                    if (activeGoodsCount == 0) {
                        sku.setPrice(skuParam.getPrice());
                        sku.setSalePrice(skuParam.getPrice());
                        sku.setStock(skuParam.getStock());
                    }
                    sku.setMemberPrice(GoodsAddHelper.encodeMemberPrice(editParam.getMemberDiscount(), skuParam.getMemberPrice()));

                    if (skuParam.getIsDefault().equals(1)) {
                        defaultCount++;
                        if (defaultCount > 1) throw new CommonException("商品不能有多个默认规格");
                    }
                    skuData.add(sku);
                }
                if (defaultCount.equals(0)) throw new CommonException("商品缺少默认规格");

                // 查询移除的规格
                Integer[] removeSkuIdArr = mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().eq("goods_id", id)
                        .notIn("sku_id", skuIdArr).select("sku_id")
                ).stream().map(i -> i.getSkuId()).toArray(Integer[]::new);

                if (removeSkuIdArr.length > 0) {
                    if (shopGoodsService.getSkuTransactionCount(removeSkuIdArr) > 0)
                        throw new CommonException("存在未完成的订单，不允许删除规格");
                    mallGoodsSkuMapper.delete(new QueryWrapper<MallGoodsSku>().in("sku_id", Arrays.asList(removeSkuIdArr)));
                }

                // 添加商品规格值
                List<MallGoodsSpec> specData = new LinkedList<>();
                List<Integer> specIdArr = new LinkedList<>();
                for (int i = 0; i < editParam.getGoodsSpecFormat().size(); i++) {
                    JSONObject goodsSpec = editParam.getGoodsSpecFormat().getJSONObject(i);

                    MallGoodsSpec spec = new MallGoodsSpec();
                    if (goodsSpec.getInt("spec_id") != null) {
                        spec.setSpecId(goodsSpec.getInt("spec_id"));
                        specIdArr.add(goodsSpec.getInt("spec_id"));
                    }
                    spec.setGoodsId(model.getGoodsId());
                    spec.setSiteId(siteId);
                    spec.setSpecName(goodsSpec.getStr("spec_name", ""));

                    List<String> specValues = new LinkedList<>();
                    for (int v = 0; v < goodsSpec.getJSONArray("values").size(); v++) {
                        JSONObject value = goodsSpec.getJSONArray("values").getJSONObject(v);
                        specValues.add(value.getStr("spec_value_name", ""));
                    }

                    spec.setSpecValues(String.join(",", specValues));
                    specData.add(spec);
                }
                // 删除移除的规格值
                if (specIdArr.size() > 0)
                    mallGoodsSpecMapper.delete(new QueryWrapper<MallGoodsSpec>().eq("goods_id", id).notIn("spec_id", specIdArr));

                shopGoodsSpecService.insertAll(specData);
            } else {
                if (shopGoodsService.getGoodsTransactionCount(id) > 0)
                    throw new CommonException("存在未完成的订单，无法编辑商品");

                // 删除原来的规格
                mallGoodsSkuMapper.delete(new QueryWrapper<MallGoodsSku>().eq("goods_id", id));
                // 删除原规格值
                mallGoodsSpecMapper.delete(new QueryWrapper<MallGoodsSpec>().eq("goods_id", id));

                Integer defaultCount = 0;
                for (String key : editParam.getGoodsSkuData().keySet()) {
                    MallVirtualGoodsParam.SkuData skuParam = editParam.getGoodsSkuData().get(key);

                    List<String> skuSpecFormat = new LinkedList<>();
                    for (int i = 0; i < skuParam.getSkuSpec().size(); i++) {
                        JSONObject spec = skuParam.getSkuSpec().getJSONObject(i);
                        skuSpecFormat.add(spec.getStr("spec_value_name", ""));
                    }

                    MallGoodsSku sku = new MallGoodsSku();
                    BeanUtils.copyProperties(skuParam, sku);
                    sku.setSiteId(siteId);
                    sku.setSkuImage(skuParam.getSkuImage().isEmpty() ? model.getGoodsCover() : skuParam.getSkuImage());
                    sku.setSalePrice(skuParam.getPrice());
                    sku.setGoodsId(model.getGoodsId());
                    sku.setSkuSpecFormat(String.join(",", skuSpecFormat));
                    if (skuParam.getIsDefault().equals(1)) {
                        defaultCount++;
                        if (defaultCount > 1) throw new CommonException("商品不能有多个默认规格");
                    }
                    sku.setMemberPrice(GoodsAddHelper.encodeMemberPrice(editParam.getMemberDiscount(), skuParam.getMemberPrice()));
                    skuData.add(sku);
                }
                if (defaultCount.equals(0)) throw new CommonException("商品缺少默认规格");

                // 添加商品规格值
                List<MallGoodsSpec> specData = new LinkedList<>();
                for (int i = 0; i < editParam.getGoodsSpecFormat().size(); i++) {
                    JSONObject goodsSpec = editParam.getGoodsSpecFormat().getJSONObject(i);

                    MallGoodsSpec spec = new MallGoodsSpec();
                    spec.setGoodsId(model.getGoodsId());
                    spec.setSiteId(siteId);
                    spec.setSpecName(goodsSpec.getStr("spec_name", ""));

                    List<String> specValues = new LinkedList<>();
                    for (int v = 0; v < goodsSpec.getJSONArray("values").size(); v++) {
                        JSONObject value = goodsSpec.getJSONArray("values").getJSONObject(v);
                        specValues.add(value.getStr("spec_value_name", ""));
                    }

                    spec.setSpecValues(String.join(",", specValues));
                    specData.add(spec);
                }
                shopGoodsSpecService.insertAll(specData);
            }
        }

        if (skuData.size() > 0) shopGoodsSkuService.insertAll(skuData);

        // 商品添加之后事件
        AfterGoodsEditEvent event = new AfterGoodsEditEvent();
        event.setSiteId(siteId);
        event.addAppSign("mall");
        event.setName("AfterGoodsEditEvent");
        event.setGoodsId(model.getGoodsId());
        event.setGoodsData(model);
        event.setSkuData(skuData);
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    /**
     * 检查插件是否安装
     *
     * @param key
     */
    private Boolean checkAddon(String key) {
        Map<String, InstallAddonListVo> addonListVoMap = coreAddonService.getInstallAddonList();
        return addonListVoMap.get(key) != null;
    }

    /**
     * 编辑商品校验商品规格价格和状态
     *
     * @param goodsId   商品ID
     * @param editParam 编辑参数
     */
    public void editCheckGoodsSkuPrice(Integer goodsId, MallVirtualGoodsParam editParam) {
        int supplyGoodsId = 0;

        if (checkAddon("supply")) {
            // 查询供应商品ID
            MallGoods model = mallGoodsMapper.selectOne(new QueryWrapper<MallGoods>().eq("goods_id", goodsId).eq("site_id", RequestUtils.siteId()));
            if (model != null) {
                supplyGoodsId = model.getSupplyGoodsId();
            }
            // 查询商品SKU数据
            List<MallGoodsSku> skuData = mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().eq("goods_id", goodsId));
            // 移除不存在的SKU并设置价格
            Map<Integer, BigDecimal> filteredSkuData = new HashMap<>();
            if (supplyGoodsId != 0 && !skuData.isEmpty()) {
                if ("single".equals(editParam.getSpecType())) {
                    // 单规格处理
                    skuData.forEach(sku -> {
                        filteredSkuData.put(sku.getSupplySkuId(), editParam.getPrice());
                    });
                } else if ("multi".equals(editParam.getSpecType())) {
                    // 多规格处理
                    if (!editParam.getGoodsSkuData().keySet().isEmpty()) {
                        Map<Integer, BigDecimal> priceMap = new HashMap<>();
                        for (String key : editParam.getGoodsSkuData().keySet()) {
                            MallVirtualGoodsParam.SkuData skuParam = editParam.getGoodsSkuData().get(key);
                            if (ObjectUtil.isNotEmpty(skuParam.getSkuId())) {
                                priceMap.put(skuParam.getSkuId(), skuParam.getPrice());
                            }
                        }
                        if (!priceMap.isEmpty()) {
                            for (MallGoodsSku sku : skuData) {
                                if (priceMap.get(sku.getSkuId()) != null) {
                                    filteredSkuData.put(sku.getSupplySkuId(), priceMap.get(sku.getSkuId()));
                                }
                            }
                        }
                    }
                }
                // 触发事件
                CheckGoodsSkuPriceEvent event = new CheckGoodsSkuPriceEvent();
                event.setGoodsId(supplyGoodsId);
                event.setStatus(editParam.getStatus());
                event.setSkuData(filteredSkuData);
                EventAndSubscribeOfPublisher.publishAll(event);
            }
        }
    }

    /**
     * 获取商品添加/编辑数据（对齐 PHP VirtualGoodsService::getInit）
     *
     * @param goodsId 商品id
     * @return init 数据
     */
    @Override
    public MallVirtualGoodsInitVo getInit(Integer goodsId) {
        MallVirtualGoodsInitVo vo = new MallVirtualGoodsInitVo();
        if (goodsId <= 0) {
            return vo;
        }
        // 虚拟商品 init 需按 site_id 过滤
        MallGoods model = mallGoodsMapper.selectOne(
                new QueryWrapper<MallGoods>()
                        .eq("goods_id", goodsId)
                        .eq("site_id", RequestUtils.siteId())
                        .eq("delete_time", 0));
        if (model == null) {
            return null;
        }

        MallVirtualGoodsInitVo.GoodsInfo goodsInfo = new MallVirtualGoodsInitVo.GoodsInfo();
        BeanUtils.copyProperties(model, goodsInfo);
        goodsInfo.setGoodsCategory(goodsInitHelper.filterGoodsCategory(
                strArrToIntArr(Convert.toStrArray(JSONUtil.parseArray(model.getGoodsCategory())))));
        goodsInfo.setGoodsMallCategory(goodsInitHelper.normalizeGoodsMallCategory(model.getGoodsMallCategory()));
        goodsInfo.setLabelIds(parseVirtualIntIdArrayOrEmpty(model.getLabelIds()));
        goodsInfo.setServiceIds(parseVirtualIntIdArrayOrEmpty(model.getServiceIds()));
        goodsInfo.setStatus(String.valueOf(model.getStatus()));
        goodsInfo.setTimerOnStatus(String.valueOf(ObjectUtil.defaultIfNull(model.getTimerOnStatus(), 0)));
        goodsInfo.setTimerOffStatus(String.valueOf(ObjectUtil.defaultIfNull(model.getTimerOffStatus(), 0)));
        goodsInfo.setIsEnableRefund(String.valueOf(ObjectUtil.defaultIfNull(model.getIsEnableRefund(), 0)));
        goodsInfo.setPointDeductType(String.valueOf(ObjectUtil.defaultIfNull(model.getPointDeductType(), "")));
        goodsInfo.setTimerOnTime(goodsInitHelper.toLongTime(model.getTimerOnTime()));
        goodsInfo.setTimerOffTime(goodsInitHelper.toLongTime(model.getTimerOffTime()));
        goodsInfo.setSupportPayType(goodsInitHelper.parseSupportPayType(model.getSupportPayType()));
        goodsInfo.setAttrIds(goodsInitHelper.parseIntIdArray(model.getAttrIds()));
        goodsInfo.setShopAttrIds(goodsInitHelper.parseIntIdArray(model.getShopAttrIds()));
        goodsInfo.setShopAttrNameArr(goodsInitHelper.loadShopAttrNameArr(goodsInfo.getShopAttrIds()));
        goodsInfo.setRecommendGoodsIds(goodsInitHelper.parseRecommendGoodsIds(model.getRecommendGoodsIds()));
        goodsInfo.setFormId(goodsInitHelper.normalizeFormId(model.getFormId()));
        goodsInfo.setPageTitle(goodsInitHelper.loadPageTitle(goodsInfo.getFormId()));
        goodsInfo.setDiyDetailId(goodsInitHelper.normalizeDiyDetailId(model.getDiyDetailId()));
        goodsInfo.setSupplierId(model.getSupplierId() == null || model.getSupplierId() <= 0 ? "" : model.getSupplierId());
        if (model.getSupplyId() != null && model.getSupplyId() > 0) {
            goodsInfo.setSupplyId(model.getSupplyId());
        } else {
            goodsInfo.setSupplyId("");
        }

        goodsInitHelper.applyBrand(model.getBrandId(), new GoodsInitHelper.BrandTarget() {
            @Override
            public void setBrandId(Object brandId) {
                goodsInfo.setBrandId(brandId);
            }

            @Override
            public void setBrandOption(GoodsBrand brandOption) {
                goodsInfo.setBrandOption(brandOption);
            }
        });

        List<MallGoodsSku> skuEntities = goodsInitHelper.loadSkuList(model.getGoodsId());
        List<MallVirtualGoodsInitVo.SkuInfo> skuList = CollectUtils.convert(skuEntities, MallVirtualGoodsInitVo.SkuInfo.class);
        goodsInfo.setSkuList(skuList);
        // 虚拟商品：sku 数量 > 1 则为多规格（与 PHP 一致）
        goodsInfo.setSpecType("single");
        if (skuEntities.size() > 1) {
            goodsInfo.setSpecType("multi");
            goodsInfo.setSpecList(CollectUtils.convert(goodsInitHelper.loadSpecList(model.getGoodsId()), MallVirtualGoodsInitVo.SpecInfo.class));
        }

        goodsInfo.setOrderGoodsCount(0L);
        goodsInfo.setActiveGoodsCount(shopGoodsService.getActiveGoodsCount(goodsId));
        goodsInfo.setAttrFormat(goodsInitHelper.parseAttrFormat(model.getAttrFormat()));

        // 供货商同步与禁用编辑字段
        GoodsInitHelper.SupplyInitResult supplyInit = goodsInitHelper.buildSupplyInit(model);
        goodsInitHelper.applySupplyToGoodsInfo(supplyInit, new GoodsInitHelper.GoodsInitMutable() {
            @Override
            public void setSupplySyncShow(Integer supplySyncShow) {
                goodsInfo.setSupplySyncShow(supplySyncShow);
            }

            @Override
            public void setGoodsName(String goodsName) {
                goodsInfo.setGoodsName(goodsName);
            }

            @Override
            public void setSubTitle(String subTitle) {
                goodsInfo.setSubTitle(subTitle);
            }

            @Override
            public void setGoodsCover(String goodsCover) {
                goodsInfo.setGoodsCover(goodsCover);
            }

            @Override
            public void setGoodsImage(String goodsImage) {
                goodsInfo.setGoodsImage(goodsImage);
            }

            @Override
            public void setGoodsVideo(String goodsVideo) {
                goodsInfo.setGoodsVideo(goodsVideo);
            }

            @Override
            public void setGoodsDesc(String goodsDesc) {
                goodsInfo.setGoodsDesc(goodsDesc);
            }
        });
        vo.setDisableEditField(supplyInit.getDisableEditField());
        vo.setDisableEditArea(supplyInit.getDisableEditArea());

        vo.setGoodsInfo(goodsInfo);
        return vo;
    }

    /** 标签/服务等 JSON 数组转 int 数组，空则返回 [] */
    private Integer[] parseVirtualIntIdArrayOrEmpty(String json) {
        Integer[] arr = goodsInitHelper.parseIntIdArray(json);
        return arr.length == 0 ? new Integer[]{} : arr;
    }
}
