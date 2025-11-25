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
import com.niu.core.event.common.CommonEvent;
import com.niu.core.event.common.CommonEventDefiner;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.diy_form.DiyFormMapper;
import com.niu.core.mapper.goods.GoodsBrandMapper;
import com.niu.core.service.admin.addon.IAddonService;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.entity.goods.MallGoodsSku;
import com.niu.mall.entity.goods.MallGoodsSpec;
import com.niu.mall.enums.goods.GoodsFieldEnum;
import com.niu.mall.enums.goods.GoodsStatusEnum;
import com.niu.mall.event.goods.AfterGoodsEditEvent;
import com.niu.mall.event.goods.CheckGoodsSkuPriceEvent;
import com.niu.mall.event.goods.SupplyGoodsSyncEventDefiner;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import com.niu.mall.mapper.goods.MallGoodsSkuMapper;
import com.niu.mall.mapper.goods.MallGoodsSpecMapper;
import com.niu.mall.service.core.goods.ICoreGoodsConfigService;
import com.niu.mall.service.site.goods.IMallGoodsService;
import com.niu.mall.service.site.goods.IMallGoodsSkuService;
import com.niu.mall.service.site.goods.IMallGoodsSpecService;
import com.niu.mall.service.site.goods.IMallVirtualGoodsService;
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

    /**
     * 商品添加
     * @param addParam 参数
     */
    @Override
    @Transactional
    public void add(MallVirtualGoodsParam addParam) {
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
        model.setStatus(coreGoodsConfigService.getConfig().getIsVerifyOpen().equals(1) ? GoodsStatusEnum.WAIT_AUDIT.getStatus() : addParam.getStatus());
        model.setAttrIds(ObjectUtil.isNotEmpty(addParam.getAttrIds()) ? addParam.getAttrIds().toString() : "");
        model.setShopAttrIds(ObjectUtil.isNotEmpty(addParam.getShopAttrIds()) ? addParam.getShopAttrIds().toString() : "");
        model.setAttrFormat(ObjectUtil.isNotEmpty(addParam.getAttrFormat()) ? addParam.getAttrFormat().toString() : "");
        model.setFormId(addParam.getFormId());
        model.setVirtualVerifyMobile(ObjectUtil.isNotEmpty(addParam.getVirtualVerifyMobile()) ? addParam.getVirtualVerifyMobile() : 0);
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
            skuData.add(sku);
        } else {
            if (addParam.getGoodsSkuData().keySet().size() == 0) throw new CommonException("请添加商品规格");

            Integer defaultCount = 0;
            for (String key: addParam.getGoodsSkuData().keySet()) {
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
    }

    /**
     * int 数组转 str 数组
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
        editCheckGoodsSkuPrice(id,editParam);
        MallGoods model = mallGoodsMapper.selectOne(new QueryWrapper<MallGoods>().eq("goods_id", id).eq("site_id", siteId).eq("delete_time", 0));
        Assert.notNull(model, "商品不存在");

        Long activeGoodsCount = shopGoodsService.getActiveGoodsCount(id);
        // 如果有正在进行的活动不允许下架
        if (editParam.getStatus().equals(0) && activeGoodsCount > 0) throw new CommonException("商品正在参与营销活动，禁止修改");

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
        model.setStatus(coreGoodsConfigService.getConfig().getIsVerifyOpen().equals(1) ? GoodsStatusEnum.WAIT_AUDIT.getStatus() : editParam.getStatus());
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
                for (String key: editParam.getGoodsSkuData().keySet()) {
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
                    if (shopGoodsService.getSkuTransactionCount(removeSkuIdArr) > 0) throw new CommonException("存在未完成的订单，不允许删除规格");
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
                if (specIdArr.size() > 0) mallGoodsSpecMapper.delete(new QueryWrapper<MallGoodsSpec>().eq("goods_id", id).notIn("spec_id", specIdArr));

                shopGoodsSpecService.insertAll(specData);
            } else {
                if (shopGoodsService.getGoodsTransactionCount(id) > 0) throw new CommonException("存在未完成的订单，无法编辑商品");

                // 删除原来的规格
                mallGoodsSkuMapper.delete(new QueryWrapper<MallGoodsSku>().eq("goods_id", id));
                // 删除原规格值
                mallGoodsSpecMapper.delete(new QueryWrapper<MallGoodsSpec>().eq("goods_id", id));

                Integer defaultCount = 0;
                for (String key: editParam.getGoodsSkuData().keySet()) {
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
     * @param key
     */
    private Boolean checkAddon(String key) {
        Map<String, InstallAddonListVo> addonListVoMap= coreAddonService.getInstallAddonList();
        return addonListVoMap.get(key) != null;
    }

    /**
     * 编辑商品校验商品规格价格和状态
     *
     * @param goodsId   商品ID
     * @param editParam 编辑参数
     */
    public void editCheckGoodsSkuPrice(Integer goodsId,MallVirtualGoodsParam editParam) {
        int supplyGoodsId = 0;

        if (checkAddon("supply")) {
            // 查询供应商品ID
            MallGoods model = mallGoodsMapper.selectOne(new QueryWrapper<MallGoods>().eq("goods_id", goodsId).eq("site_id", RequestUtils.siteId()));
            if(model != null){
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
                    if (!editParam.getGoodsSkuData().keySet().isEmpty()){
                        Map<Integer, BigDecimal> priceMap = new HashMap<>();
                        for (String key : editParam.getGoodsSkuData().keySet()) {
                            MallVirtualGoodsParam.SkuData skuParam = editParam.getGoodsSkuData().get(key);
                            if (ObjectUtil.isNotEmpty(skuParam.getSkuId())){
                                priceMap.put(skuParam.getSkuId(), skuParam.getPrice());
                            }
                        }
                        if(!priceMap.isEmpty()){
                            for (MallGoodsSku sku : skuData) {
                                if (priceMap.get(sku.getSkuId()) != null){
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
     * 获取商品添加/编辑数据
     *
     * @param goodsId
     * @return
     */
    @Override
    public MallVirtualGoodsInitVo getInit(Integer goodsId) {
        MallVirtualGoodsInitVo vo = new MallVirtualGoodsInitVo();
        if (goodsId > 0) {
            MallGoods model = mallGoodsMapper.selectOne(
                    new QueryWrapper<MallGoods>()
                            .eq("goods_id", goodsId)
                            .eq("site_id", RequestUtils.siteId())
                            .eq("delete_time", 0));
            if (model == null) return null;

            MallVirtualGoodsInitVo.GoodsInfo goodsInfo = new MallVirtualGoodsInitVo.GoodsInfo();
            BeanUtils.copyProperties(model, goodsInfo);
            goodsInfo.setFormId(model.getFormId() == 0 ? "" : model.getFormId());
            goodsInfo.setGoodsCategory(strArrToIntArr(Convert.toStrArray(JSONUtil.parseArray(model.getGoodsCategory()))));
            goodsInfo.setLabelIds(strArrToIntArr(Convert.toStrArray(JSONUtil.parseArray(model.getLabelIds()))));
            goodsInfo.setServiceIds(strArrToIntArr(Convert.toStrArray(JSONUtil.parseArray(model.getServiceIds()))));
            goodsInfo.setStatus(String.valueOf(model.getStatus()));
            if (goodsInfo.getDiyDetailId() != null && goodsInfo.getDiyDetailId() == 0) goodsInfo.setDiyDetailId( null);
            List<MallVirtualGoodsInitVo.SkuInfo> skuList = CollectUtils.convert(mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().eq("goods_id", model.getGoodsId())), MallVirtualGoodsInitVo.SkuInfo.class);
            goodsInfo.setSkuList(skuList);

            List<MallVirtualGoodsInitVo.SkuInfo> tempSkuList = skuList.stream().filter(i -> i.getSkuSpecFormat() != null && !i.getSkuSpecFormat().isEmpty()).collect(Collectors.toList());
            if (tempSkuList.size() > 0) {
                goodsInfo.setSpecType("multi");

                List<MallVirtualGoodsInitVo.SpecInfo> specList = CollectUtils.convert(mallGoodsSpecMapper.selectList(new QueryWrapper<MallGoodsSpec>().eq("goods_id", model.getGoodsId())), MallVirtualGoodsInitVo.SpecInfo.class);
                goodsInfo.setSpecList(specList);
            }

            if (model.getVirtualReceiveType().equals("verify")) {
                goodsInfo.setOrderGoodsCount(shopGoodsService.getGoodsTransactionCount(model.getGoodsId()));
            }

            if (model.getBrandId().equals(0)) {
                goodsInfo.setBrandId("");
                goodsInfo.setBrandOption(new GoodsBrand());
            }else{
                goodsInfo.setBrandOption(goodsBrandMapper.selectOne(new QueryWrapper<GoodsBrand>().eq("brand_id", model.getBrandId())));
            }

            if (model.getSupplyId().equals(0)) goodsInfo.setSupplyId("");

            goodsInfo.setActiveGoodsCount(shopGoodsService.getActiveGoodsCount(goodsId));
            Addon supply = addonMapper.selectOne(new QueryWrapper<Addon>().eq("`key`", "supply"));
            goodsInfo.setSupplySyncShow(0);
            //存在供应商
            if(ObjectUtil.isNotEmpty(supply))
            {
                if(goodsInfo.getSupplyGoodsId() > 0)
                {
                    if(goodsInfo.getSupplySync() == 1)
                    {
                        //这里需要处理同步设置，事件调用
                        SupplyGoodsSyncEventDefiner.SupplyGoodsSyncEvent event = new SupplyGoodsSyncEventDefiner.SupplyGoodsSyncEvent();
                        event.setGoodsId(goodsInfo.getSupplyGoodsId());
                        List<SupplyGoodsSyncEventDefiner.SupplyGoodsSyncResult> supplyData = EventAndSubscribeOfPublisher.publishAndCallback(event);

                        for (SupplyGoodsSyncEventDefiner.SupplyGoodsSyncResult item : supplyData) {
                            if (item == null) continue;
                            if (ObjectUtil.isNotEmpty(item.getGoodsName())) goodsInfo.setGoodsName(item.getGoodsName());
                            if (ObjectUtil.isNotEmpty(item.getSubTitle())) goodsInfo.setSubTitle(item.getSubTitle());
                            if (ObjectUtil.isNotEmpty(item.getGoodsCover())) goodsInfo.setGoodsCover(item.getGoodsCover());
                            if (ObjectUtil.isNotEmpty(item.getGoodsImage())) goodsInfo.setGoodsImage(item.getGoodsImage());
                            if (ObjectUtil.isNotEmpty(item.getGoodsDesc())) goodsInfo.setGoodsDesc(item.getGoodsDesc());
                        }
                    }
                    goodsInfo.setSupplySyncShow(1);
                    vo.setDisableEditField(goodsInfo.getSupplySync() == 1 ? StringUtils.mergeArrays(GoodsFieldEnum.DISABLE_EDIT_FIELD.getValue(), GoodsFieldEnum.ENABLE_SYNC_FIELD.getValue()) : GoodsFieldEnum.DISABLE_EDIT_FIELD.getValue());
                    vo.setDisableEditArea(GoodsFieldEnum.DISABLE_EDIT_AREA.getValue());
                }
            }
            if(!goodsInfo.getFormId().equals(""))
            {
                DiyForm formInfo = diyFormMapper.selectOne(new QueryWrapper<DiyForm>().eq("form_id", goodsInfo.getFormId()));
                if(ObjectUtil.isNotEmpty(formInfo))
                {
                    goodsInfo.setPageTitle(formInfo.getPageTitle());
                }
            }


            List<MallVirtualGoodsInitVo.MallGoodsAttrVo> voList = new Gson().fromJson(model.getAttrFormat(),new TypeToken<List<MallVirtualGoodsInitVo.MallGoodsAttrVo>>(){}.getType());
            goodsInfo.setAttrFormat(voList);

            vo.setGoodsInfo(goodsInfo);
        }
        return vo;
    }
}
