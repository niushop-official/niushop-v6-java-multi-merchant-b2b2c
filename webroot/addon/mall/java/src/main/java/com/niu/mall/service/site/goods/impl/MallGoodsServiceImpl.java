package com.niu.mall.service.site.goods.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.diy_form.DiyForm;
import com.niu.core.entity.goods.GoodsBrand;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.diy_form.DiyFormMapper;
import com.niu.core.mapper.goods.GoodsBrandMapper;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.api.member.vo.MemberLevelInfoVo;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.site.ICoreSiteService;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.core.service.site.shop.ISiteShopSettingService;
import com.niu.mall.entity.active.MallActiveGoods;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.entity.goods.MallGoodsSku;
import com.niu.mall.entity.goods.MallGoodsSpec;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.enums.active.ActiveGoodsTypeEnum;
import com.niu.mall.enums.goods.*;
import com.niu.mall.event.goods.AfterGoodsEditEvent;
import com.niu.mall.event.goods.AfterMallGoodsStatusEditEvent;
import com.niu.mall.event.goods.CheckGoodsSkuPriceEvent;
import com.niu.mall.event.goods.SupplyGoodsSyncEventDefiner;
import com.niu.mall.event.marketing.GoodsJoinInfoEventDefiner;
import com.niu.mall.event.marketing.GoodsMarketCalculateEventDefiner;
import com.niu.mall.mapper.active.MallActiveGoodsMapper;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import com.niu.mall.mapper.goods.MallGoodsSkuMapper;
import com.niu.mall.mapper.goods.MallGoodsSpecMapper;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.service.api.goods.IGoodsService;
import com.niu.mall.service.core.goods.ICoreGoodsConfigService;
import com.niu.mall.service.core.goods.ICoreGoodsLimitBuyService;
import com.niu.mall.service.core.goods.ICoreGoodsOnlineCheckService;
import com.niu.mall.service.core.goods.vo.GoodsConfigVo;
import com.niu.mall.service.site.goods.IMallGoodsService;
import com.niu.mall.service.site.goods.IMallGoodsSkuService;
import com.niu.mall.service.site.goods.IMallGoodsSpecService;
import com.niu.mall.service.site.goods.model.DisableEditField;
import com.niu.mall.service.site.goods.param.*;
import com.niu.mall.service.site.goods.vo.*;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 商品实现
 */
@Service
public class MallGoodsServiceImpl implements IMallGoodsService {

    @Resource
    MallGoodsMapper mallGoodsMapper;

    @Resource
    MallGoodsSkuMapper mallGoodsSkuMapper;

    @Resource
    MallGoodsSpecMapper mallGoodsSpecMapper;

    @Resource
    IMallGoodsSkuService mallGoodsSkuService;

    @Resource
    IMallGoodsSpecService mallGoodsSpecService;

    @Resource
    GoodsBrandMapper goodsBrandMapper;

    @Resource
    MallActiveGoodsMapper mallActiveGoodsMapper;

    @Resource
    MallOrderGoodsMapper mallOrderGoodsMapper;

    @Resource
    ICoreUploadService coreUploadService;

    @Resource
    IGoodsService goodsService;

    @Resource
    IMemberLevelService memberLevelService;

    @Resource
    ICoreGoodsLimitBuyService coreGoodsLimitBuyService;

    @Resource
    AddonMapper addonMapper;

    @Resource
    DiyFormMapper diyFormMapper;

    @Resource
    ISiteShopSettingService siteShopSettingService;

    @Resource
    ICoreAddonService coreAddonService;

    @Resource
    ICoreGoodsConfigService coreGoodsConfigService;
    @Resource
    ICoreSiteService coreSiteService;
    @Resource
    ICoreGoodsOnlineCheckService coreGoodsOnlineCheckService;

    /**
     * 商品列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsListVo>
     */
    @Override
    public PageResult<MallGoodsListVo> page(PageParam pageParam, MallGoodsSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<MallGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("g")
                .innerJoin("?_mall_goods_sku gs ON g.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site s ON g.supply_id = s.site_id ".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("g.goods_id,g.site_id,goods_name,goods_type,goods_cover,g.stock,g.sale_num,g.pay_money,g.status,g.shop_sort,g.create_time,member_discount,g.audit_reason,g.supply_id,g.supply_goods_id,s.site_name as supplyName");
        queryWrapper.eq("g.site_id", RequestUtils.siteId());
        queryWrapper.eq("delete_time", 0);
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsName()))
            queryWrapper.like("goods_name", searchParam.getGoodsName());
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsType()))
            queryWrapper.eq("goods_type", searchParam.getGoodsType());
        if (ObjectUtil.isNotEmpty(searchParam.getBrandId())) queryWrapper.eq("brand_id", searchParam.getBrandId());
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsCategory()))
            queryWrapper.like("goods_category", "\"" + searchParam.getGoodsCategory() + "\"");
        if (ObjectUtil.isNotEmpty(searchParam.getLabelIds()))
            queryWrapper.like("label_ids", "\"" + searchParam.getLabelIds() + "\"");

        if (ObjectUtil.isNotEmpty(searchParam.getGoodsSource())){
            if(searchParam.getGoodsSource().equals("self_goods") || searchParam.getGoodsSource().equals("0")){
                queryWrapper.eq("g.supply_goods_id", 0);
            }else{
                queryWrapper.ne("g.supply_goods_id", 0);
            }
        }
        if (ObjectUtil.isNotEmpty(searchParam.getStatus())){
            if (GoodsStatusEnum.OFFLINE.getStatus().equals(searchParam.getStatus())) {
                queryWrapper.eq("g.status", GoodsStatusEnum.OFFLINE.getStatus());
                queryWrapper.eq("g.is_supply_update", 0);
            } else if(searchParam.getStatus().equals(-4)){
                queryWrapper.eq("g.status",GoodsStatusEnum.OFFLINE.getStatus());
                queryWrapper.eq("g.is_supply_update", 1);
            }else {
                queryWrapper.eq("g.status", searchParam.getStatus());
            }
        }
        QueryMapperUtils.buildByBetween(queryWrapper, "g.sale_num", new Integer[]{searchParam.getStartSaleNum(), searchParam.getEndSaleNum()});
        QueryMapperUtils.buildByBetween(queryWrapper, "gs.price", new Double[]{searchParam.getStartPrice(), searchParam.getEndPrice()});
        if (searchParam.getStartTime() != null) {
            QueryMapperUtils.buildByBetween(queryWrapper, "g.create_time", new Long[]{searchParam.getStartTime(), searchParam.getEndTime()});
        }

        if (ObjectUtil.isNotEmpty(searchParam.getOrder())) {
            queryWrapper.orderBy(true, searchParam.getSort().equals("asc"), searchParam.getOrder());
        } else {
            queryWrapper.orderByDesc("g.goods_id");
        }


        IPage<MallGoodsListVo> iPage = mallGoodsMapper.selectJoinPage(new Page<>(page, limit), MallGoodsListVo.class, queryWrapper);
        //处理商品活动
        formatGoodsJoinActive(iPage.getRecords());
        List<MallGoodsListVo> list = new LinkedList<>();
        for (MallGoodsListVo vo : iPage.getRecords()) {
            MallGoodsSku goodsSku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>()
                    .select("sku_id", "goods_id", "price", "member_price")
                    .eq("goods_id", vo.getGoodsId()).eq("is_default", 1));
            MallGoodsListVo.GoodsSku goodsSkuVo = new MallGoodsListVo.GoodsSku();
            BeanUtils.copyProperties(goodsSku, goodsSkuVo);
            vo.setGoodsSku(goodsSkuVo);
            vo.setGoodsCoverThumbSmall(coreUploadService.thumb(RequestUtils.siteId(), vo.getGoodsCover(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType()));
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 处理商品参与的活动数据
     * @param list
     */
    private void formatGoodsJoinActive(List<MallGoodsListVo> list){
        List<Integer> goodsIds = list.stream().map(MallGoodsListVo::getGoodsId).toList();
        // 创建具体的事件对象
        GoodsJoinInfoEventDefiner.GoodsJoinInfoEvent event = new GoodsJoinInfoEventDefiner.GoodsJoinInfoEvent();
        event.setGoodsIds(goodsIds);
        event.setSiteId(RequestUtils.siteId());
        event.setIsGetCount(0);
        List<GoodsJoinInfoEventDefiner.GoodsJoinInfoResult> marketData = EventAndSubscribeOfPublisher.publishAndCallback(event);
        Map<Integer, Map<String, Object>> itemMap = new HashMap<>();
        if (!marketData.isEmpty()){
            for (GoodsJoinInfoEventDefiner.GoodsJoinInfoResult item : marketData){
                if(ObjectUtil.isNotEmpty(item.getInfo())){
                    itemMap = item.getInfo();
                }
            }
        }
        Map<Integer, List<Map<String, Object>>> goodsActiveMap = new HashMap<>();
        if(!itemMap.isEmpty()){
            for (Integer goodsId : itemMap.keySet()){
                if(itemMap.get(goodsId).isEmpty()){
                    continue;
                }
                for (String activeId : itemMap.get(goodsId).keySet()){
                    if (!goodsActiveMap.containsKey(goodsId)){
                        goodsActiveMap.put(goodsId, new ArrayList<>());
                    }
                    Map<String, Object> activeInfo = (Map<String, Object>)itemMap.get(goodsId).get(activeId);
                    // 合并数组值
                    List<Map<String, Object>> currentList = goodsActiveMap.get(goodsId);
                    currentList.add(activeInfo);
                    itemMap.get(goodsId).put(activeId, currentList);
                }
            }
        }
        if(!goodsActiveMap.isEmpty()){
            Map<Integer,Map<String, Map<String, Object>>> activeTips = new HashMap<>();
            for (Integer goodsId : goodsActiveMap.keySet()){
                activeTips.put(goodsId, new HashMap<>());
                for (Map<String, Object> activeInfo : goodsActiveMap.get(goodsId)){
                    Map<String, Object> type = new HashMap<>();
                    String joinType = activeInfo.get("join_type").toString();
                    if(activeTips.get(goodsId).get(joinType) == null){
                        type = new HashMap<>();
                    }else{
                        type = activeTips.get(goodsId).get(joinType);
                    }

                    if (ObjectUtil.isEmpty(type.get("name"))){
                        type.put("name","");
                    }
                    if(ObjectUtil.isNotEmpty(activeInfo.get("short"))){
                        type.put("short",activeInfo.get("short"));
                        activeInfo.remove("short");
                        type.computeIfAbsent("list", k -> new ArrayList<>());

                        // 合并数组值
                        List<Map<String, Object>> currentList = (List<Map<String, Object>>)type.get("list");
                        currentList.add(activeInfo);
                        type.put("list",currentList);
                        String name = type.get("name").toString();
                        type.put("name",name + activeInfo.get("name") +" \n ");
                    }
                    activeTips.get(goodsId).put(joinType, type );
                }
            }
            if(!activeTips.isEmpty()){
                for (int i=0; i<list.size(); i++){
                    list.get( i).setActive(JSONUtil.parseObj(activeTips.get(list.get( i).getGoodsId())).toString());
                }
            }
        }
    }


    /**
     * 商品详情
     *
     * @param id 主键
     * @return ShopGoodsInfoVo
     */
    @Override
    public MallGoodsInfoVo info(Integer id) {
        MallGoods model = mallGoodsMapper.selectOne(
                new QueryWrapper<MallGoods>()
                        .eq("goods_id", id)
                        .eq("site_id", RequestUtils.siteId())
                        .eq("delete_time", 0));
        if (model == null) return null;

        MallGoodsInfoVo vo = new MallGoodsInfoVo();
        BeanUtils.copyProperties(model, vo);

        return vo;
    }

    /**
     * 商品添加
     *
     * @param addParam 参数
     */
    @Override
    @Transactional
    public void add(MallGoodsParam addParam) {
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
        model.setDeliveryType(JSONUtil.parseArray(addParam.getDeliveryType()).toString());
        model.setStatus(coreGoodsConfigService.getConfig().getIsVerifyOpen().equals(1) ? GoodsStatusEnum.WAIT_AUDIT.getStatus() : addParam.getStatus());
        model.setAttrIds(ObjectUtil.isNotEmpty(addParam.getAttrIds()) ? addParam.getAttrIds().toString() : "");
        model.setShopAttrIds(ObjectUtil.isNotEmpty(addParam.getShopAttrIds()) ? addParam.getShopAttrIds().toString() : "");
        model.setAttrFormat(ObjectUtil.isNotEmpty(addParam.getAttrFormat()) ? addParam.getAttrFormat().toString() : "");
        model.setFormId(addParam.getFormId());
        model.setApplyTime(coreGoodsConfigService.getConfig().getIsVerifyOpen().equals(1)  ? System.currentTimeMillis() / 1000 : 0);
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
            sku.setSkuSpecFormat("");
            sku.setIsDefault(1);
            sku.setIsSell(1);
            skuData.add(sku);
        } else {
            if (addParam.getGoodsSkuData().keySet().size() == 0) throw new CommonException("请添加商品规格");

            Integer defaultCount = 0;
            for (String key : addParam.getGoodsSkuData().keySet()) {
                MallGoodsParam.SkuData skuParam = addParam.getGoodsSkuData().get(key);

                List<String> skuSpecFormat = new LinkedList<>();
                for (int i = 0; i < skuParam.getSkuSpec().size(); i++) {
                    JSONObject spec = skuParam.getSkuSpec().getJSONObject(i);
                    skuSpecFormat.add(spec.getStr("spec_value_name", ""));
                }

                MallGoodsSku sku = new MallGoodsSku();
                BeanUtils.copyProperties(skuParam, sku);
                sku.setSkuName(skuParam.getSpecName());
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
            mallGoodsSpecService.insertAll(specData);
        }

        mallGoodsSkuService.insertAll(skuData);

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
    public void edit(Integer id, MallGoodsParam editParam) {
        // 校验供货商商品规格价格
        editCheckGoodsSkuPrice(id, editParam);

        Integer siteId = RequestUtils.siteId();
        MallGoods model = mallGoodsMapper.selectOne(new QueryWrapper<MallGoods>().eq("goods_id", id).eq("site_id", siteId).eq("delete_time", 0));
        Assert.notNull(model, "商品不存在");

        Long activeGoodsCount = getActiveGoodsCount(id);
        // 如果有正在进行的活动不允许下架
        if (editParam.getStatus().equals(0) && activeGoodsCount > 0)
            throw new CommonException("商品正在参与营销活动，禁止修改");
        //校验商品审核信息

        Boolean auditStatus = checkAuditGoodsInfo(id, editParam);
        BeanUtils.copyProperties(editParam, model);
        model.setGoodsCover(editParam.getGoodsImage().split(",")[0]);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        model.setGoodsCategory(JSONUtil.parseArray(intArrToStrArr(editParam.getGoodsCategory())).toString());
        model.setLabelIds(JSONUtil.parseArray(intArrToStrArr(editParam.getLabelIds())).toString());
        model.setServiceIds(JSONUtil.parseArray(intArrToStrArr(editParam.getServiceIds())).toString());
        model.setDeliveryType(JSONUtil.parseArray(editParam.getDeliveryType()).toString());
        model.setStatus(auditStatus ? GoodsStatusEnum.WAIT_AUDIT.getStatus() : editParam.getStatus());
        model.setAttrIds(ObjectUtil.isNotEmpty(editParam.getAttrIds()) ? editParam.getAttrIds().toString() : "");
        model.setShopAttrIds(ObjectUtil.isNotEmpty(editParam.getShopAttrIds()) ? editParam.getShopAttrIds().toString() : "");
        model.setAttrFormat(ObjectUtil.isNotEmpty(editParam.getAttrFormat()) ? editParam.getAttrFormat().toString() : "");
        model.setFormId(editParam.getFormId());
        model.setApplyTime(auditStatus ? System.currentTimeMillis() / 1000 : 0);
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
                sku.setIsDefault(1);
                sku.setSkuSpecFormat("");
                skuData.add(sku);
            } else {
                MallGoodsSku sku = skuList.get(0);
                sku.setSkuImage(model.getGoodsCover());
                sku.setSkuNo(editParam.getSkuNo());
                sku.setSkuName("");
                sku.setSkuSpecFormat("");
                sku.setWeight(editParam.getWeight());
                sku.setVolume(editParam.getVolume());
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

            MallGoodsParam.SkuData firstSkuData = editParam.getGoodsSkuData().get(editParam.getGoodsSkuData().keySet().iterator().next());
            if (activeGoodsCount > 0 && firstSkuData.getSkuId() == null) {
                throw new CommonException("商品正在参与营销活动，禁止修改");
            }

            // 检测规格项是否发生变化
            if (firstSkuData.getSkuId() != null) {
                List<Integer> skuIdArr = new LinkedList<>();

                Integer defaultCount = 0;
                for (String key : editParam.getGoodsSkuData().keySet()) {
                    MallGoodsParam.SkuData skuParam = editParam.getGoodsSkuData().get(key);

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
                    sku.setWeight(skuParam.getWeight());
                    sku.setVolume(skuParam.getVolume());
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
                    if (getSkuTransactionCount(removeSkuIdArr) > 0)
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

                mallGoodsSpecService.insertAll(specData);
            } else {
                if (getGoodsTransactionCount(id) > 0) throw new CommonException("存在未完成的订单，无法编辑商品");

                // 删除原来的规格
                mallGoodsSkuMapper.delete(new QueryWrapper<MallGoodsSku>().eq("goods_id", id));
                // 删除原规格值
                mallGoodsSpecMapper.delete(new QueryWrapper<MallGoodsSpec>().eq("goods_id", id));

                Integer defaultCount = 0;
                for (String key : editParam.getGoodsSkuData().keySet()) {
                    MallGoodsParam.SkuData skuParam = editParam.getGoodsSkuData().get(key);

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
                mallGoodsSpecService.insertAll(specData);
            }
        }

        if (skuData.size() > 0) mallGoodsSkuService.insertAll(skuData);

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
     * 判断商品信息是否有变动
     *
     * @return
     */
    public Boolean checkAuditGoodsInfo(Integer goodsId, MallGoodsParam editParam) {
        Boolean auditStatus = false;
        //查询商品配置
        GoodsConfigVo config = coreGoodsConfigService.getConfig();
        if(config != null && config.getIsVerifyOpen().equals(1)){
            // 查询商品信息
            MallGoods model = mallGoodsMapper.selectOne(new QueryWrapper<MallGoods>().eq("goods_id", goodsId).eq("site_id", RequestUtils.siteId()).eq("delete_time", 0));
            Assert.notNull(model, "商品不存在");

            List<Integer> auditStatusList = Arrays.asList(GoodsStatusEnum.WAIT_AUDIT.getStatus(),GoodsStatusEnum.AUDIT_REFUSE.getStatus(),GoodsStatusEnum.AUDI_TO_FINE.getStatus());
            if (auditStatusList.contains(model.getStatus())) auditStatus = true;

            //比较商品内容是否发生了变化
            auditStatus = checkGoodsContent(editParam, model, auditStatus);

            //查询规格
            List<MallGoodsSku> skuList = mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().eq("goods_id", goodsId));
            if (editParam.getSpecType().equals("single")) {
                //多规格变单规格，需要审核
                if(skuList.size()>1){
                    auditStatus = true;
                } else if (!skuList.isEmpty()) {
                    auditStatus =  checkGoodsSkuContent(editParam, skuList.get(0), auditStatus);
                }
            }else if (editParam.getSpecType().equals("multi")){
                if (!editParam.getGoodsSkuData().keySet().isEmpty()){
                    for (String key : editParam.getGoodsSkuData().keySet()) {
                        MallGoodsParam.SkuData skuParam = editParam.getGoodsSkuData().get(key);
                        if(skuParam.getSkuId() == null){
                            continue;
                        }else{
                            auditStatus = true;
                        }
                        List<String> skuSpecFormat = new LinkedList<>();
                        for (int i = 0; i < skuParam.getSkuSpec().size(); i++) {
                            JSONObject spec = skuParam.getSkuSpec().getJSONObject(i);
                            skuSpecFormat.add(spec.getStr("spec_value_name", ""));
                        }

                        MallGoodsSku sku = new MallGoodsSku();
                        BeanUtils.copyProperties(skuParam, sku);
                        sku.setSkuName(skuParam.getSpecName());
                        sku.setSkuImage(skuParam.getSkuImage().isEmpty() ? model.getGoodsCover() : skuParam.getSkuImage());
                        sku.setSalePrice(skuParam.getPrice());
                        sku.setGoodsId(model.getGoodsId());
                        sku.setSkuSpecFormat(String.join(",", skuSpecFormat));

                        //查询商品规格
                        List<MallGoodsSku> multiSkuThis = skuList.stream().filter(item -> item.getSkuId().equals(skuParam.getSkuId()) && item.getGoodsId().equals(model.getGoodsId())).toList();
                        if(!multiSkuThis.isEmpty()){
                            auditStatus =  checkGoodsSkuContentTow(sku, multiSkuThis.get(0), auditStatus);
                        }
                    }
                }else{
                    if(!skuList.isEmpty()){
                        auditStatus = true;
                    }
                }
                //查询规格值
                List<MallGoodsSpec> specList = mallGoodsSpecMapper.selectList(new QueryWrapper<MallGoodsSpec>().eq("goods_id", goodsId));
                if (!editParam.getGoodsSpecFormat().isEmpty()){
                    for (int i = 0; i < editParam.getGoodsSpecFormat().size(); i++) {
                        JSONObject goodsSpec = editParam.getGoodsSpecFormat().getJSONObject(i);
                        if(goodsSpec.getStr("spec_id") == null){
                            continue;
                        }else{
                            auditStatus = true;
                        }
                        MallGoodsSpec spec = new MallGoodsSpec();
                        spec.setGoodsId(model.getGoodsId());
                        spec.setSpecName(goodsSpec.getStr("spec_name", ""));

                        List<String> specValues = new LinkedList<>();
                        for (int v = 0; v < goodsSpec.getJSONArray("values").size(); v++) {
                            JSONObject value = goodsSpec.getJSONArray("values").getJSONObject(v);
                            specValues.add(value.getStr("spec_value_name", ""));
                        }

                        spec.setSpecValues(String.join(",", specValues));
                        //查询商品值
                        List<MallGoodsSpec> multiSpecThis = specList.stream().filter(item -> item.getSpecId().equals(spec.getSpecId()) && item.getGoodsId().equals(model.getGoodsId())).toList();
                        if(!multiSpecThis.isEmpty()){
                            auditStatus =  checkGoodsSpecContent(spec, multiSpecThis.get(0), auditStatus);
                        }
                    }
                }else{
                    if(!specList.isEmpty()){
                        auditStatus = true;
                    }
                }
            }

        }
        return auditStatus;
    }

    /**
     * 检测商品规格内容是否发生了变化
     *
     * @param editParam
     * @param model
     * @return
     */
    private Boolean checkGoodsSkuContent(MallGoodsParam editParam, MallGoodsSku model,Boolean isEdit) {
        if (!(ObjectUtil.isNotEmpty(editParam.getGoodsImage()) && ObjectUtil.isNotEmpty(model.getSkuImage())
                && editParam.getGoodsImage().equals(model.getSkuImage()) || ObjectUtil.isEmpty(editParam.getGoodsImage()) && ObjectUtil.isEmpty(model.getSkuImage()))) {
            isEdit = true;
        }
        if (!(ObjectUtil.isNotEmpty(editParam.getSkuNo()) && ObjectUtil.isNotEmpty(model.getSkuNo())
                && editParam.getSkuNo().equals(model.getSkuNo()) || ObjectUtil.isEmpty(editParam.getSkuNo()) && ObjectUtil.isEmpty(model.getSkuNo()))) {
            isEdit = true;
        }
        return isEdit;
    }

    /**
     * 检测商品规格内容是否发生了变化
     *
     * @param editParam
     * @param model
     * @return
     */
    private Boolean checkGoodsSkuContentTow(MallGoodsSku editParam, MallGoodsSku model,Boolean isEdit) {
        if (!(ObjectUtil.isNotEmpty(editParam.getSkuImage()) && ObjectUtil.isNotEmpty(model.getSkuImage())
                && editParam.getSkuImage().equals(model.getSkuImage()) || ObjectUtil.isEmpty(editParam.getSkuImage()) && ObjectUtil.isEmpty(model.getSkuImage()))) {
            isEdit = true;
        }
        if (!(ObjectUtil.isNotEmpty(editParam.getSkuNo()) && ObjectUtil.isNotEmpty(model.getSkuNo())
                && editParam.getSkuNo().equals(model.getSkuNo()) || ObjectUtil.isEmpty(editParam.getSkuNo()) && ObjectUtil.isEmpty(model.getSkuNo()))) {
            isEdit = true;
        }
        if (!(ObjectUtil.isNotEmpty(editParam.getSkuSpecFormat()) && ObjectUtil.isNotEmpty(model.getSkuSpecFormat())
                && editParam.getSkuSpecFormat().equals(model.getSkuSpecFormat()) || ObjectUtil.isEmpty(editParam.getSkuSpecFormat()) && ObjectUtil.isEmpty(model.getSkuSpecFormat()))) {
            isEdit = true;
        }
        return isEdit;
    }

    /**
     * 检测商品规格内容是否发生了变化
     *
     * @param editParam 编辑
     * @param model 模型
     * @return 是否发生了变化
     */
    private Boolean checkGoodsSpecContent(MallGoodsSpec editParam, MallGoodsSpec model,Boolean isEdit) {
        if (!(ObjectUtil.isNotEmpty(editParam.getSpecName()) && ObjectUtil.isNotEmpty(model.getSpecName())
                && editParam.getSpecName().equals(model.getSpecName()) || ObjectUtil.isEmpty(editParam.getSpecName()) && ObjectUtil.isEmpty(model.getSpecName()))) {
            isEdit = true;
        }
        if (!(ObjectUtil.isNotEmpty(editParam.getSpecValues()) && ObjectUtil.isNotEmpty(model.getSpecValues())
                && editParam.getSpecValues().equals(model.getSpecValues()) || ObjectUtil.isEmpty(editParam.getSpecValues()) && ObjectUtil.isEmpty(model.getSpecValues()))) {
            isEdit = true;
        }
        return isEdit;
    }

    /**
     * 检测商品内容是否发生了变化
     *
     * @param editParam
     * @param model
     * @return
     */
    private Boolean checkGoodsContent(MallGoodsParam editParam, MallGoods model,Boolean isEdit) {
        if (!(ObjectUtil.isNotEmpty(editParam.getGoodsName()) && ObjectUtil.isNotEmpty(model.getGoodsName())
                && editParam.getGoodsName().equals(model.getGoodsName()) || ObjectUtil.isEmpty(editParam.getGoodsName()) && ObjectUtil.isEmpty(model.getGoodsName()))) {
            isEdit = true;
        }
        if (!(ObjectUtil.isNotEmpty(editParam.getSubTitle()) && ObjectUtil.isNotEmpty(model.getSubTitle())
                && editParam.getSubTitle().equals(model.getSubTitle()) || ObjectUtil.isEmpty(editParam.getSubTitle()) && ObjectUtil.isEmpty(model.getSubTitle()))) {
            isEdit = true;
        }
        if (!(ObjectUtil.isNotEmpty(editParam.getGoodsType()) && ObjectUtil.isNotEmpty(model.getGoodsType())
                && editParam.getGoodsType().equals(model.getGoodsType()) || ObjectUtil.isEmpty(editParam.getGoodsType()) && ObjectUtil.isEmpty(model.getGoodsType()))) {
            isEdit = true;
        }
        if (!(ObjectUtil.isNotEmpty(editParam.getGoodsImage()) && ObjectUtil.isNotEmpty(model.getGoodsImage())
                && editParam.getGoodsImage().equals(model.getGoodsImage()) || ObjectUtil.isEmpty(editParam.getGoodsImage()) && ObjectUtil.isEmpty(model.getGoodsImage()))) {
            isEdit = true;
        }
        if (!(ObjectUtil.isNotEmpty(editParam.getGoodsVideo()) && ObjectUtil.isNotEmpty(model.getGoodsVideo())
                && editParam.getGoodsVideo().equals(model.getGoodsVideo()) || ObjectUtil.isEmpty(editParam.getGoodsVideo()) && ObjectUtil.isEmpty(model.getGoodsVideo()))) {
            isEdit = true;
        }
        if (!(ObjectUtil.isNotEmpty(editParam.getGoodsDesc()) && ObjectUtil.isNotEmpty(model.getGoodsDesc())
                && editParam.getGoodsDesc().equals(model.getGoodsDesc()) || ObjectUtil.isEmpty(editParam.getGoodsDesc()) && ObjectUtil.isEmpty(model.getGoodsDesc()))) {
            isEdit = true;
        }
        if (!(ObjectUtil.isNotEmpty(editParam.getBrandId()) && ObjectUtil.isNotEmpty(model.getBrandId())
                && editParam.getBrandId().equals(model.getBrandId()) || ObjectUtil.isEmpty(editParam.getBrandId()) && ObjectUtil.isEmpty(model.getBrandId()))) {
            isEdit = true;
        }

        String goodsCategory = "[]";
        if(editParam.getGoodsCategory() != null && editParam.getGoodsCategory().length > 0){
            goodsCategory = JSONUtil.parseArray(editParam.getGoodsCategory()).toString();
        }
        if(!goodsCategory.equals(model.getGoodsCategory())){
            isEdit = true;
        }
        if (!(ObjectUtil.isNotEmpty(editParam.getGoodsMallCategory()) && ObjectUtil.isNotEmpty(model.getGoodsMallCategory())
                && editParam.getGoodsMallCategory().equals(model.getGoodsMallCategory()) || ObjectUtil.isEmpty(editParam.getGoodsMallCategory()) && ObjectUtil.isEmpty(model.getGoodsMallCategory()))) {
            isEdit = true;
        }
        String attrIds = "[]";
        if(editParam.getAttrIds() != null && !editParam.getAttrIds().isEmpty()){
            attrIds = editParam.getAttrIds().toString();
        }
        if(!attrIds.equals(model.getAttrIds())){
            isEdit = true;
        }
        String shopAttrIds = "[]";
        if(editParam.getShopAttrIds() != null && !editParam.getShopAttrIds().isEmpty()){
            shopAttrIds = editParam.getShopAttrIds().toString();
        }
        if(!shopAttrIds.equals(model.getShopAttrIds())){
            isEdit = true;
        }
        String attrFormat = "[]";
        if(editParam.getAttrFormat() != null && !editParam.getAttrFormat().isEmpty()){
            attrFormat = editParam.getAttrFormat().toString();
        }
        if(!attrFormat.equals(model.getAttrFormat())){
            isEdit = true;
        }

        return isEdit;
    }


    /**
     * 编辑商品校验商品规格价格和状态
     *
     * @param goodsId   商品ID
     * @param editParam 编辑参数
     */
    public void editCheckGoodsSkuPrice(Integer goodsId, MallGoodsParam editParam) {
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
                            MallGoodsParam.SkuData skuParam = editParam.getGoodsSkuData().get(key);
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
     * 检查插件是否安装
     * @param key
     */
    private Boolean checkAddon(String key) {
        Map<String, InstallAddonListVo> addonListVoMap = coreAddonService.getInstallAddonList();
        return addonListVoMap.get(key) != null;
    }

    /**
     * 商品删除
     *
     * @param param
     */
    @Override
    public void del(GetGoodsIdsParam param) {
        Integer[] goodsIds = null;
        if (param.getGoodsIds() instanceof Integer) {
            goodsIds = new Integer[]{(Integer) param.getGoodsIds()};
        } else if (param.getGoodsIds() instanceof ArrayList) {
            goodsIds = ((ArrayList<?>) param.getGoodsIds()).toArray(new Integer[0]);
        }
        if (getActiveGoodsCount(goodsIds) > 0) throw new CommonException("商品正在参与营销活动，禁止删除");
        if (getSupplyGoodsCount(goodsIds) > 0) throw new CommonException("供货商商品不允许删除");

        MallGoods model = new MallGoods();
        model.setStatus(0);
        model.setDeleteTime(System.currentTimeMillis() / 1000);
        mallGoodsMapper.update(model, new QueryWrapper<MallGoods>().eq("site_id", RequestUtils.siteId()).in("goods_id", goodsIds));
    }

    /**
     * 查询商品是否是供应商
     *
     * @return
     */
    public Long getSupplyGoodsCount(Integer[] goodsIds) {
        return mallGoodsMapper.selectCount(new QueryWrapper<MallGoods>()
                .eq("site_id", RequestUtils.siteId())
                .in("goods_id", Arrays.asList(goodsIds))
        );
    }

    /**
     * 恢复商品
     *
     * @param param
     */
    @Override
    public void recycle(GetGoodsIdsParam param) {
        Integer[] goodsIds = null;
        if (param.getGoodsIds() instanceof Integer) {
            goodsIds = new Integer[]{(Integer) param.getGoodsIds()};
        } else if (param.getGoodsIds() instanceof ArrayList) {
            goodsIds = ((ArrayList<?>) param.getGoodsIds()).toArray(new Integer[0]);
        }

        MallGoods model = new MallGoods();
        model.setDeleteTime(Long.valueOf(0));
        mallGoodsMapper.update(model, new QueryWrapper<MallGoods>().eq("site_id", RequestUtils.siteId()).in("goods_id", goodsIds));
    }

    /**
     * 商品回收站
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsListVo>
     */
    @Override
    public PageResult<MallGoodsListVo> recyclePage(PageParam pageParam, MallGoodsSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<MallGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("goods_id,site_id,goods_name,goods_type,goods_cover,stock,sale_num,status,sort,create_time,member_discount");
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.ne("delete_time", 0);

        if (ObjectUtil.isNotEmpty(searchParam.getGoodsName()))
            queryWrapper.like("goods_name", searchParam.getGoodsName());
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsType()))
            queryWrapper.eq("goods_type", searchParam.getGoodsType());
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsCategory()))
            queryWrapper.like("goods_category", "\"" + searchParam.getGoodsCategory() + "\"");
        queryWrapper.orderByDesc("goods_id");

        IPage<MallGoods> iPage = mallGoodsMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<MallGoodsListVo> list = new LinkedList<>();
        for (MallGoods item : iPage.getRecords()) {
            MallGoodsListVo vo = new MallGoodsListVo();
            BeanUtils.copyProperties(item, vo);

            MallGoodsSku goodsSku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>()
                    .select("sku_id", "goods_id", "price", "member_price")
                    .eq("goods_id", item.getGoodsId()).eq("is_default", 1));
            MallGoodsListVo.GoodsSku goodsSkuVo = new MallGoodsListVo.GoodsSku();
            if (ObjectUtil.isNotEmpty(goodsSku)) {
                BeanUtils.copyProperties(goodsSku, goodsSkuVo);
            }

            vo.setGoodsSku(goodsSkuVo);
            String thumb = coreUploadService.thumb(RequestUtils.siteId(), item.getGoodsCover(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType());
            if (ObjectUtil.isNotEmpty(thumb)) {
                vo.setGoodsCoverThumbSmall(thumb);
            } else vo.setGoodsCoverThumbSmall("");
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public Object getStatus() {
        Map<Integer, Map<String, Object>> map = GoodsStatusEnum.getMap();
        List<String> addons = coreAddonService.getAddonCache();
        //查询状态
        QueryWrapper<MallGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId()).eq("delete_time", 0);
        queryWrapper.eq("status", GoodsStatusEnum.OFFLINE.getStatus());
        queryWrapper.eq("is_supply_update", 1);
        List<MallGoods> list = mallGoodsMapper.selectList(queryWrapper);

        //查询是否有supply插件
        if (!addons.isEmpty() && addons.contains("supply")) {
            Map<String, Object> supplyMap = new HashMap<>();
            supplyMap.put("label", "货源更新");
            supplyMap.put("value", -4);
            supplyMap.put("count", list.size());
            map.put(-4, supplyMap);
        }
        return map;
    }

    /**
     * 修改商品上下架状态
     *
     * @param param
     */
    @Override
    public void editStatus(EditGoodsStatusParam param) {
        Integer isAll = param.getIsAll();
        List<Integer> explodeGoodsIds = new ArrayList<>();
        List<Integer> goodsIds = new ArrayList<>();
        if (param.getGoodsIds() != null) {
            if (param.getGoodsIds() instanceof List) {
                goodsIds = (List<Integer>) param.getGoodsIds();
            } else if (param.getGoodsIds() instanceof Integer) {
                goodsIds = Arrays.asList((Integer) param.getGoodsIds());
            }
        }
        if(goodsIds.isEmpty()&&param.getIsAll()!=1){
            throw new CommonException("请选择商品");
        }
        SiteInfoVo shopBasicInfo = coreSiteService.getShopBasicInfo(RequestUtils.siteId());
        if (shopBasicInfo.getStatus().equals(SiteStatusEnum.EXPIRE.getCode()) && param.getStatus().equals(GoodsStatusEnum.ONLINE.getStatus())) {
            throw new CommonException("商铺状态不符合要求，请检查商铺状态为 " + SiteStatusEnum.ON.getName() + "，禁止商品上架");
        }
        if (shopBasicInfo.getStatus().equals(SiteStatusEnum.CLOSE.getCode()) && param.getStatus().equals(GoodsStatusEnum.ONLINE.getStatus())) {
            throw new CommonException("商铺状态不符合要求，请检查商铺状态为 " + SiteStatusEnum.ON.getName() + "，禁止商品上架");
        }
        if (param.getStatus().equals(GoodsStatusEnum.OFFLINE.getStatus())) {
            // 查询商品参与营销活动的数量
            explodeGoodsIds = getActiveGoodsIds(goodsIds, isAll, param.getWhere());
        }
        if (isAll == 1) {
            MPJQueryWrapper<MallGoods> queryWrapper = getBatchAllQuery(param.getWhere(), explodeGoodsIds);
            List<MallGoods> goodsList = mallGoodsMapper.selectList(queryWrapper);
            goodsIds = goodsList.stream().map(MallGoods::getGoodsId).collect(Collectors.toList());
        } else {
            if (param.getGoodsIds() instanceof List) {
                goodsIds = (List<Integer>) param.getGoodsIds();
            } else if (param.getGoodsIds() instanceof Integer) {
                goodsIds = Arrays.asList((Integer) param.getGoodsIds());
            }
        }
        if (param.getStatus() == GoodsStatusEnum.ONLINE.getStatus()) {
            coreGoodsOnlineCheckService.checkGoodsStatus(goodsIds);
            coreGoodsOnlineCheckService.checkGoodsSkuPriceBatch(goodsIds);
        }
        //更新商品状态
        if (isAll == 0) {
            MallGoods model = new MallGoods();
            model.setStatus(param.getStatus());
            QueryWrapper<MallGoods> updateWrapper = new QueryWrapper<>();
            updateWrapper.in("goods_id", goodsIds)
                    .eq("site_id", RequestUtils.siteId())
                    .ge("status", 0);
            if (!explodeGoodsIds.isEmpty()) {
                updateWrapper.notIn("goods_id", explodeGoodsIds);
            }
            mallGoodsMapper.update(model, updateWrapper);
        } else {
            MPJQueryWrapper<MallGoods> updateWrapper = getBatchAllQuery((Map<String, Object>) param.getWhere(), explodeGoodsIds);
            updateWrapper.ge("goods.status", 0);
            List<MallGoods> goodsList = mallGoodsMapper.selectList(updateWrapper);
            if (!goodsList.isEmpty()) {
                List<Integer> targetGoodsIds = goodsList.stream()
                        .map(MallGoods::getGoodsId)
                        .collect(Collectors.toList());
                MallGoods model = new MallGoods();
                model.setStatus(param.getStatus());
                mallGoodsMapper.update(model, new QueryWrapper<MallGoods>()
                        .in("goods_id", targetGoodsIds));
            }
        }

        AfterMallGoodsStatusEditEvent event = new AfterMallGoodsStatusEditEvent();
        event.setSiteId(RequestUtils.siteId());
        event.setGoodsIds(goodsIds);
        EventAndSubscribeOfPublisher.publishAll(event);
    }


    /**
     * 获取参与营销活动的商品ID列表
     * @param goodsId 商品ID列表
     * @param isAll 是否全部查询
     * @param where 查询条件
     * @return 参与营销活动的商品ID列表
     */
    private List<Integer> getActiveGoodsIds(List<Integer> goodsId, Integer isAll, Map<String, Object> where) {
        List<Integer> goodsIdList;
        if (goodsId == null) {
            goodsIdList = new ArrayList<>();
        } else {
            goodsIdList = new ArrayList<>(goodsId);
        }
        // 创建具体的事件对象
        GoodsJoinInfoEventDefiner.GoodsJoinInfoEvent event = new GoodsJoinInfoEventDefiner.GoodsJoinInfoEvent();
        event.setGoodsIds(goodsIdList);
        event.setSiteId(RequestUtils.siteId());
        event.setIsGetCount(0);
        event.setIsAll(isAll);
        event.setWhere(where);
        List<GoodsJoinInfoEventDefiner.GoodsJoinInfoResult> marketData = EventAndSubscribeOfPublisher.publishAndCallback(event);
        List<Integer> activeGoodsIds = new ArrayList<>();
        if (!marketData.isEmpty()){
            for (GoodsJoinInfoEventDefiner.GoodsJoinInfoResult item : marketData){
                if(ObjectUtil.isNotEmpty(item.getInfo())){
                    Map<Integer, Map<String, Object>> itemMap = item.getInfo();
                    activeGoodsIds.addAll(itemMap.keySet());
                }
            }
        }

        return activeGoodsIds;
    }

    /**
     * 检测商品sku价格
     * @param goods
     */
    public void checkGoodsSkuPrice(MallGoods goods) {
        Integer supplyGoodsId = 0;
        if (checkAddon("supply")) {
            // 查询供应商品ID
            if (goods != null) {
                supplyGoodsId = goods.getSupplyGoodsId();
            }

            if (supplyGoodsId != null && supplyGoodsId > 0) {
                // 查询商品SKU数据
                List<MallGoodsSku> skuData = mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().eq("goods_id", goods.getGoodsId()));

                Map<Integer, BigDecimal> filteredSkuData = new HashMap<>();
                skuData.forEach(sku -> {
                    filteredSkuData.put(sku.getSupplySkuId(), sku.getPrice());
                });

                // 触发事件
                CheckGoodsSkuPriceEvent event = new CheckGoodsSkuPriceEvent();
                event.setGoodsId(supplyGoodsId);
                event.setStatus(goods.getStatus());
                event.setSkuData(filteredSkuData);
                EventAndSubscribeOfPublisher.publishAll(event);
            }
        }
    }

    /**
     * 修改商品排序
     *
     * @param param
     */
    @Override
    public void editSort(EditGoodsSortParam param) {
        MallGoods model = new MallGoods();
        //model.setSort(param.getShopSort());
        model.setShopSort(param.getShopSort());
        mallGoodsMapper.update(model, new QueryWrapper<MallGoods>()
                .eq("goods_id", param.getGoodsId()));

    }

    /**
     * 获取商品添加/编辑数据
     *
     * @param goodsId
     * @return
     */
    @Override
    public MallGoodsInitVo getInit(Integer goodsId) {
        MallGoodsInitVo vo = new MallGoodsInitVo();
        if (goodsId > 0) {
            MallGoods model = mallGoodsMapper.selectOne(
                    new QueryWrapper<MallGoods>()
                            .eq("goods_id", goodsId)
                            .eq("site_id", RequestUtils.siteId())
                            .eq("delete_time", 0));
            if (model == null) return null;

            MallGoodsInitVo.GoodsInfo goodsInfo = new MallGoodsInitVo.GoodsInfo();
            BeanUtils.copyProperties(model, goodsInfo);
            goodsInfo.setFormId(model.getFormId() == 0 ? "" : model.getFormId());
            goodsInfo.setGoodsCategory(strArrToIntArr(Convert.toStrArray(JSONUtil.parseArray(model.getGoodsCategory()))));
            goodsInfo.setLabelIds(strArrToIntArr(Convert.toStrArray(JSONUtil.parseArray(model.getLabelIds()))));
            goodsInfo.setServiceIds(strArrToIntArr(Convert.toStrArray(JSONUtil.parseArray(model.getServiceIds()))));
            goodsInfo.setStatus(String.valueOf(model.getStatus()));
            if (goodsInfo.getDiyDetailId() != null && goodsInfo.getDiyDetailId() == 0) {
                goodsInfo.setDiyDetailId(null);
            }

            List<MallGoodsInitVo.SkuInfo> skuList = CollectUtils.convert(mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().eq("goods_id", model.getGoodsId())), MallGoodsInitVo.SkuInfo.class);
            goodsInfo.setSkuList(skuList);

            List<MallGoodsInitVo.SkuInfo> tempSkuList = skuList.stream().filter(i -> i.getSkuSpecFormat() != null && !i.getSkuSpecFormat().isEmpty()).collect(Collectors.toList());
            if (tempSkuList.size() > 0) {
                goodsInfo.setSpecType("multi");

                List<MallGoodsInitVo.SpecInfo> specList = CollectUtils.convert(mallGoodsSpecMapper.selectList(new QueryWrapper<MallGoodsSpec>().eq("goods_id", model.getGoodsId())), MallGoodsInitVo.SpecInfo.class);
                goodsInfo.setSpecList(specList);
            }

            if (model.getBrandId().equals(0)) {
                goodsInfo.setBrandId("");
                goodsInfo.setBrandOption(new GoodsBrand());
            } else {
                goodsInfo.setBrandOption(goodsBrandMapper.selectOne(new QueryWrapper<GoodsBrand>().eq("brand_id", model.getBrandId())));
            }

            if (model.getSupplyId().equals(0)) goodsInfo.setSupplyId("");

            goodsInfo.setActiveGoodsCount(getActiveGoodsCount(goodsId));

            Addon supply = addonMapper.selectOne(new QueryWrapper<Addon>().eq("`key`", "supply"));
            goodsInfo.setSupplySyncShow(0);
            //存在供应商
            if (ObjectUtil.isNotEmpty(supply)) {
                if (goodsInfo.getSupplyGoodsId() > 0) {
                    if (goodsInfo.getSupplySync() == 1) {
                        //这里需要处理同步设置，事件调用
                        SupplyGoodsSyncEventDefiner.SupplyGoodsSyncEvent event = new SupplyGoodsSyncEventDefiner.SupplyGoodsSyncEvent();
                        event.setGoodsId(goodsInfo.getSupplyGoodsId());
                        List<SupplyGoodsSyncEventDefiner.SupplyGoodsSyncResult> supplyData = EventAndSubscribeOfPublisher.publishAndCallback(event);

                        for (SupplyGoodsSyncEventDefiner.SupplyGoodsSyncResult item : supplyData) {
                            if (item == null) continue;
                            if (ObjectUtil.isNotEmpty(item.getGoodsName())) goodsInfo.setGoodsName(item.getGoodsName());
                            if (ObjectUtil.isNotEmpty(item.getSubTitle())) goodsInfo.setSubTitle(item.getSubTitle());
                            if (ObjectUtil.isNotEmpty(item.getGoodsCover()))
                                goodsInfo.setGoodsCover(item.getGoodsCover());
                            if (ObjectUtil.isNotEmpty(item.getGoodsImage()))
                                goodsInfo.setGoodsImage(item.getGoodsImage());
                            if (ObjectUtil.isNotEmpty(item.getGoodsDesc())) goodsInfo.setGoodsDesc(item.getGoodsDesc());
                        }
                    }
                    goodsInfo.setSupplySyncShow(1);
                    vo.setDisableEditField(goodsInfo.getSupplySync() == 1 ? StringUtils.mergeArrays(GoodsFieldEnum.DISABLE_EDIT_FIELD.getValue(), GoodsFieldEnum.ENABLE_SYNC_FIELD.getValue()) : GoodsFieldEnum.DISABLE_EDIT_FIELD.getValue());
                    vo.setDisableEditArea(GoodsFieldEnum.DISABLE_EDIT_AREA.getValue());
                }
            }
            if (!goodsInfo.getFormId().equals("")) {
                DiyForm formInfo = diyFormMapper.selectOne(new QueryWrapper<DiyForm>().eq("form_id", goodsInfo.getFormId()));
                if (ObjectUtil.isNotEmpty(formInfo)) {
                    goodsInfo.setPageTitle(formInfo.getPageTitle());
                }
            }
            List<MallVirtualGoodsInitVo.MallGoodsAttrVo> voList = new Gson().fromJson(model.getAttrFormat(), new TypeToken<List<MallVirtualGoodsInitVo.MallGoodsAttrVo>>() {
            }.getType());
            goodsInfo.setAttrFormat(voList);

            vo.setGoodsInfo(goodsInfo);
        }
        return vo;
    }

    /**
     * 查询商品正在交易中的数量
     *
     * @return
     */
    public Long getGoodsTransactionCount(Integer goodsId) {
        Integer[] orderStatus = {1, 2, 3};
        MPJQueryWrapper<MallOrderGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("og")
                .leftJoin("?_mall_order o ON og.order_id = o.order_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.eq("og.goods_id", goodsId);
        queryWrapper.in("o.status", Arrays.asList(orderStatus));
        return mallOrderGoodsMapper.selectJoinCount(queryWrapper);
    }

    /**
     * 查询商品正在交易中的数量
     *
     * @return
     */
    public Long getSkuTransactionCount(Integer[] skuIds) {
        Integer[] orderStatus = {1, 2, 3};
        MPJQueryWrapper<MallOrderGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("og")
                .leftJoin("?_mall_order o ON og.order_id = o.order_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.in("og.sku_id", Arrays.asList(skuIds));
        queryWrapper.in("o.status", Arrays.asList(orderStatus));
        return mallOrderGoodsMapper.selectJoinCount(queryWrapper);
    }

    /**
     * 查询商品参与营销活动的数量
     *
     * @return
     */
    public Long getActiveGoodsCount(Integer goodsId) {
        String[] activeType = {ActiveGoodsTypeEnum.GOODS_SINGLE.getType(), ActiveGoodsTypeEnum.GOODS_INDEPENDENT.getType()};
        return mallActiveGoodsMapper.selectCount(new QueryWrapper<MallActiveGoods>()
                .eq("site_id", RequestUtils.siteId())
                .eq("active_goods_status", "active")
                .in("active_goods_type", Arrays.asList(activeType))
                .eq("goods_id", goodsId)
        );
    }

    /**
     * 查询商品参与营销活动的数量
     *
     * @return
     */
    public Long getActiveGoodsCount(Integer[] goodsIds) {
        String[] activeType = {ActiveGoodsTypeEnum.GOODS_SINGLE.getType(), ActiveGoodsTypeEnum.GOODS_INDEPENDENT.getType()};
        return mallActiveGoodsMapper.selectCount(new QueryWrapper<MallActiveGoods>()
                .eq("site_id", RequestUtils.siteId())
                .eq("active_goods_status", "active")
                .in("active_goods_type", Arrays.asList(activeType))
                .in("goods_id", Arrays.asList(goodsIds))
        );
    }

    /**
     * 复制商品
     *
     * @param goodsId
     */
    @Override
    @Transactional
    public void copy(Integer goodsId) {
        Integer siteId = RequestUtils.siteId();
        MallGoods model = mallGoodsMapper.selectOne(new QueryWrapper<MallGoods>().eq("goods_id", goodsId).eq("site_id", siteId).eq("delete_time", 0));
        Assert.notNull(model, "商品不存在");

        model.setGoodsId(null);
        model.setGoodsName(model.getGoodsName() + "_副本");
        model.setSort(0);
        model.setStatus(0);
        model.setSaleNum(0);
        model.setCreateTime(System.currentTimeMillis() / 1000);
        mallGoodsMapper.insert(model);

        // 复制sku
        List<MallGoodsSku> skuList = mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().eq("goods_id", goodsId));
        for (MallGoodsSku item : skuList) {
            item.setSkuId(null);
            item.setGoodsId(model.getGoodsId());
            item.setSaleNum(0);
        }
        mallGoodsSkuService.insertAll(skuList);

        // 复制规格
        List<MallGoodsSpec> specList = mallGoodsSpecMapper.selectList(new QueryWrapper<MallGoodsSpec>().eq("goods_id", goodsId));
        for (MallGoodsSpec item : specList) {
            item.setSpecId(null);
            item.setGoodsId(model.getGoodsId());
        }
        mallGoodsSpecService.insertAll(specList);
    }

    /**
     * 商品选择分页列表
     *
     * @param pageParam
     * @param searchParam
     * @return
     */
    @Override
    public MallSelectGoodsListVo getSelectPage(PageParam pageParam, MallGoodsSelectSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<MallGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("g")
                .innerJoin("?_mall_goods_sku gs ON g.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("g.goods_id,g.site_id,g.goods_image,goods_name,goods_type,goods_cover,g.stock,g.sale_num,g.status,sort,create_time,member_discount");
        queryWrapper.eq("g.site_id", RequestUtils.siteId());
        queryWrapper.eq("delete_time", 0);
        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) {
            queryWrapper.and(i -> i.like("goods_name", searchParam.getKeyword()).or().like("sub_title", searchParam.getKeyword()));
        }

        if (ObjectUtil.isNotEmpty(searchParam.getSkuIds())) {
            List<MallGoodsSku> selectGoodsList = mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().select("goods_id").in("sku_id", searchParam.getSkuIds()).groupBy("goods_id"));
            if (ObjectUtil.isNotEmpty(selectGoodsList)) {
                List<Integer> goodsIds = selectGoodsList.stream().map(i -> i.getGoodsId()).toList();
                if (ObjectUtil.isEmpty(searchParam.getGoodsIds())) searchParam.setGoodsIds(goodsIds);
            }
        }

        if (searchParam.getSelectType().equals("all")) {
            queryWrapper.gt("g.stock", 0);
            queryWrapper.eq("g.status", 1);
        }

        if (searchParam.getSelectType().equals("selected") && ObjectUtil.isNotEmpty(searchParam.getGoodsIds())) {
            queryWrapper.in("g.goods_id", searchParam.getGoodsIds());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getGoodsType()))
            queryWrapper.eq("goods_type", searchParam.getGoodsType());

        if (ObjectUtil.isNotEmpty(searchParam.getGoodsCategory()))
            queryWrapper.like("goods_category", "\"" + searchParam.getGoodsCategory() + "\"");

        List<Integer> verifyGoodsIds = new LinkedList<>();
        List<Integer> verifySkuIds = new LinkedList<>();

        // 检测商品id集合是否存在，移除不存在的商品id，纠正数据准确性
        if (ObjectUtil.isNotEmpty(searchParam.getVerifyGoodsIds())) {
            List<MallGoods> verifyGoodsList = mallGoodsMapper.selectList(new QueryWrapper<MallGoods>().select("goods_id").in("goods_id", searchParam.getVerifyGoodsIds()).eq("site_id", RequestUtils.siteId()).eq("delete_time", 0).eq("status", 1));
            if (ObjectUtil.isNotEmpty(verifyGoodsList)) {
                verifyGoodsIds = verifyGoodsList.stream().map(i -> i.getGoodsId()).collect(Collectors.toList());
            }
        }

        if (ObjectUtil.isNotEmpty(searchParam.getVerifySkuIds())) {
            List<MallGoodsSku> verifySkuList = mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().in("sku_id", searchParam.getVerifySkuIds()).select("sku_id"));
            if (ObjectUtil.isNotEmpty(verifySkuList)) {
                verifySkuIds = verifySkuList.stream().map(i -> i.getSkuId()).collect(Collectors.toList());
            }
        }

        queryWrapper.orderByDesc("g.goods_id");

        IPage<MallSelectGoodsListVo.ListVo> iPage = mallGoodsMapper.selectJoinPage(new Page<>(page, limit), MallSelectGoodsListVo.ListVo.class, queryWrapper);
        List<MallSelectGoodsListVo.ListVo> list = new LinkedList<>();
        for (MallSelectGoodsListVo.ListVo vo : iPage.getRecords()) {
            vo.setGoodsImage(vo.getGoodsImage());

            List<MallGoodsSku> skuList = mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().eq("goods_id", vo.getGoodsId()));
            vo.setSkuList(skuList);

            for (MallGoodsSku item : skuList) {
                if (item.getIsDefault().equals(1)) {
                    MallSelectGoodsListVo.GoodsSku goodsSkuVo = new MallSelectGoodsListVo.GoodsSku();
                    BeanUtils.copyProperties(item, goodsSkuVo);
                    vo.setGoodsSku(goodsSkuVo);
                }
            }
            vo.setGoodsCoverThumbSmall(coreUploadService.thumb(RequestUtils.siteId(), vo.getGoodsCover(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType()));
            vo.setGoodsCoverThumbMid(coreUploadService.thumb(RequestUtils.siteId(), vo.getGoodsCover(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType()));
            list.add(vo);
        }
        PageResult pageResutl = PageResult.build(page, limit, iPage.getTotal()).setData(list);

        MallSelectGoodsListVo vo = new MallSelectGoodsListVo();
        BeanUtils.copyProperties(pageResutl, vo);

        vo.setVerifyGoodsIds(verifyGoodsIds);
        vo.setVerifySkuIds(verifySkuIds);

        return vo;
    }

    /**
     * 获取商品选择列表带ku
     *
     * @param searchParam
     */
    @Override
    public List<MallSelectGoodsListVo.ListVo> getSelectGoodsSku(MallGoodsSelectSearchParam searchParam) {
        MPJQueryWrapper<MallGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("g")
                .innerJoin("?_mall_goods_sku gs ON g.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("g.goods_id,g.site_id,g.goods_image,goods_name,goods_type,goods_cover,g.stock,g.sale_num,g.status,sort,create_time,member_discount");
        queryWrapper.eq("g.site_id", RequestUtils.siteId());
        queryWrapper.eq("g.status", 1);
        queryWrapper.eq("delete_time", 0);

        // 检测商品id集合是否存在，移除不存在的商品id，纠正数据准确性
        if (ObjectUtil.isNotEmpty(searchParam.getVerifyGoodsIds())) {
            List<MallGoods> verifyGoodsList = mallGoodsMapper.selectList(new QueryWrapper<MallGoods>().select("goods_id").in("goods_id", searchParam.getVerifyGoodsIds()).eq("site_id", RequestUtils.siteId()).eq("delete_time", 0).eq("status", 1));
            if (ObjectUtil.isNotEmpty(verifyGoodsList)) {
                List<Integer> verifyGoodsIds = verifyGoodsList.stream().map(i -> i.getGoodsId()).collect(Collectors.toList());
                queryWrapper.in("g.goods_id", verifyGoodsIds);
            }
        }

        if (ObjectUtil.isNotEmpty(searchParam.getVerifySkuIds())) {
            List<MallGoodsSku> verifySkuList = mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().in("sku_id", searchParam.getVerifySkuIds()).select("goods_id").groupBy("goods_id"));
            if (ObjectUtil.isNotEmpty(verifySkuList)) {
                List<Integer> verifyGoodsIds = verifySkuList.stream().map(i -> i.getGoodsId()).collect(Collectors.toList());
                queryWrapper.in("g.goods_id", verifyGoodsIds);
            }
        }

        List<MallSelectGoodsListVo.ListVo> list = mallGoodsMapper.selectJoinList(MallSelectGoodsListVo.ListVo.class, queryWrapper);
        for (MallSelectGoodsListVo.ListVo vo : list) {
            vo.setGoodsImage(vo.getGoodsImage());

            List<MallGoodsSku> skuList = mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().eq("goods_id", vo.getGoodsId()));
            vo.setSkuList(skuList);

            for (MallGoodsSku item : skuList) {
                if (item.getIsDefault().equals(1)) {
                    MallSelectGoodsListVo.GoodsSku goodsSkuVo = new MallSelectGoodsListVo.GoodsSku();
                    BeanUtils.copyProperties(item, goodsSkuVo);
                    vo.setGoodsSku(goodsSkuVo);
                }
            }
            vo.setGoodsCoverThumbSmall(coreUploadService.thumb(RequestUtils.siteId(), vo.getGoodsCover(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType()));
            vo.setGoodsCoverThumbMid(coreUploadService.thumb(RequestUtils.siteId(), vo.getGoodsCover(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType()));
        }
        return list;
    }

    /**
     * 查询商品SKU规格列表
     *
     * @param goodsId
     * @return
     */
    @Override
    public List<MallGoodsSkuListVo> getSkuList(Integer goodsId) {
        return CollectUtils.convert(mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().eq("goods_id", goodsId).eq("site_id", RequestUtils.siteId())), MallGoodsSkuListVo.class);
    }

    /**
     * 编辑商品规格列表库存
     *
     * @param param
     */
    @Override
    @Transactional
    public void editGoodsListStock(EditGoodsListStockParam param) {
        Integer siteId = RequestUtils.siteId();
        MallGoods model = mallGoodsMapper.selectOne(new QueryWrapper<MallGoods>().eq("goods_id", param.getGoodsId()).eq("site_id", siteId).select("goods_id"));
        Assert.notNull(model, "商品不存在");

        List<MallGoodsSku> skuList = new LinkedList<>();
        Integer stock = 0;
        for (EditGoodsListStockParam.Sku item : param.getSkuList()) {
            MallGoodsSku sku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>().eq("sku_id", item.getSkuId()).eq("goods_id", param.getGoodsId()).select("sku_id"));
            Assert.notNull(sku, "商品规格不存在");
            sku.setStock(item.getStock());
            skuList.add(sku);
            stock += item.getStock();
        }

        mallGoodsSkuService.insertAll(skuList);

        model.setStock(stock);
        mallGoodsMapper.updateById(model);
    }

    /**
     * 编辑商品规格列表价格
     *
     * @param param
     */
    @Override
    @Transactional
    public void editGoodsListPrice(EditGoodsListPriceParam param) {
        Integer siteId = RequestUtils.siteId();
        MallGoods model = mallGoodsMapper.selectOne(new QueryWrapper<MallGoods>().eq("goods_id", param.getGoodsId()).eq("site_id", siteId).select("goods_id"));
        Assert.notNull(model, "商品不存在");

        Long activeGoodsCount = getActiveGoodsCount(param.getGoodsId());

        List<MallGoodsSku> skuList = new LinkedList<>();
        for (EditGoodsListPriceParam.Sku item : param.getSkuList()) {
            MallGoodsSku sku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>().eq("sku_id", item.getSkuId()).eq("goods_id", param.getGoodsId()).select("sku_id"));
            Assert.notNull(sku, "商品规格不存在");
            sku.setCostPrice(item.getCostPrice());
            sku.setMarketPrice(item.getMarketPrice());
            if (activeGoodsCount == 0) {
                sku.setPrice(item.getPrice());
                sku.setSalePrice(item.getPrice());
            }
            skuList.add(sku);
        }

        mallGoodsSkuService.insertAll(skuList);
    }

    /**
     * 编辑商品规格列表会员价
     *
     * @param param
     */
    @Override
    @Transactional
    public void editGoodsListMemberPrice(EditGoodsListMemberPriceParam param) {
        Integer siteId = RequestUtils.siteId();
        MallGoods model = mallGoodsMapper.selectOne(new QueryWrapper<MallGoods>().eq("goods_id", param.getGoodsId()).eq("site_id", siteId).select("goods_id"));
        Assert.notNull(model, "商品不存在");

        List<MallGoodsSku> skuList = new LinkedList<>();
        for (EditGoodsListMemberPriceParam.Sku item : param.getSkuList()) {
            MallGoodsSku sku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>().eq("sku_id", item.getSkuId()).eq("goods_id", param.getGoodsId()).select("sku_id"));
            Assert.notNull(sku, "商品规格不存在");
            sku.setMemberPrice(item.getMemberPrice().toString());
            skuList.add(sku);
        }

        mallGoodsSkuService.insertAll(skuList);

        model.setMemberDiscount(param.getMemberDiscount());
        mallGoodsMapper.updateById(model);
    }

    @Override
    public PageResult<GetBuyGoodsSelectVo> getBuyGoodsSelect(PageParam pageParam, BuyGoodsSelectParam param) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<MallGoods> goodsQueryWrapper = new QueryWrapper<>();
        goodsQueryWrapper.eq("site_id", RequestUtils.siteId())
                .gt("stock", 0)
                .eq("status", 1)
                .eq("is_gift", 0)
                .orderByDesc("sort", "create_time");

        if (ObjectUtil.isNotEmpty(param.getGoodsCategory())) {
            goodsQueryWrapper.like("goods_category", param.getGoodsCategory());
        }

        if (ObjectUtil.isNotEmpty(param.getGoodsType())) {
            goodsQueryWrapper.eq("goods_type", param.getGoodsType());
        }

        if (ObjectUtil.isNotEmpty(param.getKeyword())) {
            goodsQueryWrapper.and(wrapper -> wrapper
                    .like("goods_name", param.getKeyword())
                    .or()
                    .like("sub_title", param.getKeyword()));
        }

        Page<MallGoods> iPage = mallGoodsMapper.selectPage(new Page<>(page, limit), goodsQueryWrapper);

        Set<Integer> goodsIds = CollStreamUtil.toSet(iPage.getRecords(), MallGoods::getGoodsId);
        Map<Integer, List<MallGoodsSku>> skuMap = new HashMap<>(goodsIds.size(), 1);
        if (ObjectUtil.isNotEmpty(goodsIds)) {
            QueryWrapper<MallGoodsSku> skuQueryWrapper = new QueryWrapper<>();
            skuQueryWrapper.in("goods_id", goodsIds);
            skuMap.putAll(mallGoodsSkuMapper.selectList(skuQueryWrapper)
                    .stream().collect(Collectors.groupingBy(MallGoodsSku::getGoodsId)));
        }

        IPage<GetBuyGoodsSelectVo> convert = iPage.convert(o -> {
            GetBuyGoodsSelectVo vo = new GetBuyGoodsSelectVo();
            BeanUtils.copyProperties(o, vo);
            vo.setGoodsTypeName(GoodsTypeEnum.getNameByType(vo.getGoodsType()));

            String mid = coreUploadService.thumb(RequestUtils.siteId(), vo.getGoodsCover(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
            vo.setGoodsCoverThumbMid(mid);
            String small = coreUploadService.thumb(RequestUtils.siteId(), vo.getGoodsCover(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType());
            vo.setGoodsCoverThumbSmall(small);

            List<MallGoodsSku> skus = skuMap.get(vo.getGoodsId());
            if (ObjectUtil.isNotEmpty(skus)) {
                MallGoodsSku sku = skus.get(0);
                MemberLevelInfoVo memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());
                JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(sku.getMemberPrice()) ? JSONUtil.parseObj(sku.getMemberPrice()) : new JSONObject();
                BigDecimal price = goodsService.calculateMemberPrice(memberLevel, vo.getMemberDiscount(), memberPriceConfig, sku.getPrice());
                sku.setMemberPrice(price.toString());
                vo.setGoodsSku(sku);
            }

            Integer hasBuy = coreGoodsLimitBuyService.getGoodsHasBuyNumber(RequestUtils.siteId(), param.getMemberId(), vo.getGoodsId());
            vo.setHasBuy(hasBuy);

            return vo;
        });

        return PageResult.build(convert);
    }

    @Override
    public GetBuySkuSelectVo getBuySkuSelect(BuySkuSelectParam param) {
        QueryWrapper<MallGoodsSku> goodsSkuQueryWrapper = new QueryWrapper<>();
        goodsSkuQueryWrapper.eq("site_id", RequestUtils.siteId())
                .eq("sku_id", param.getSkuId());
        MallGoodsSku mallGoodsSku = mallGoodsSkuMapper.selectOne(goodsSkuQueryWrapper);
        if (mallGoodsSku == null) {
            return null;
        }

        GetBuySkuSelectVo vo = new GetBuySkuSelectVo();
        BeanUtils.copyProperties(mallGoodsSku, vo);

        String small = coreUploadService.thumb(RequestUtils.siteId(), mallGoodsSku.getSkuImage(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType());
        String mid = coreUploadService.thumb(RequestUtils.siteId(), mallGoodsSku.getSkuImage(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
        String big = coreUploadService.thumb(RequestUtils.siteId(), mallGoodsSku.getSkuImage(), UploadThumbTypeEnum.BIG.getType()).getDataMap().get(UploadThumbTypeEnum.BIG.getType());

        vo.setSkuImageThumbSmall(small);
        vo.setSkuImageThumbMid(mid);
        vo.setSkuImageThumbBig(big);

        MallGoods goods = mallGoodsMapper.selectById(vo.getGoodsId());
        GetBuySkuSelectVo.GoodsDTO goodsDTO = new GetBuySkuSelectVo.GoodsDTO();
        vo.setGoods(goodsDTO);

        QueryWrapper<MallGoodsSku> shopGoodsSkuQueryWrapper = new QueryWrapper<>();
        shopGoodsSkuQueryWrapper.eq("goods_id", goods.getGoodsId());
        List<MallGoodsSku> mallGoodsSkuses = mallGoodsSkuMapper.selectList(shopGoodsSkuQueryWrapper);
        vo.setSkuList(mallGoodsSkuses);

        QueryWrapper<MallGoodsSpec> shopGoodsSpecQueryWrapper = new QueryWrapper<>();
        shopGoodsSpecQueryWrapper.eq("goods_id", goods.getGoodsId());
        List<MallGoodsSpec> mallGoodsSpecs = mallGoodsSpecMapper.selectList(shopGoodsSpecQueryWrapper);
        vo.setGoodsSpec(mallGoodsSpecs);

        if (ObjectUtil.isNotEmpty(param.getMemberId())) {
            MemberLevelInfoVo memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());
            JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(mallGoodsSku.getMemberPrice()) ? JSONUtil.parseObj(mallGoodsSku.getMemberPrice()) : new JSONObject();
            BigDecimal memberPrice = goodsService.calculateMemberPrice(memberLevel, goods.getMemberDiscount(), memberPriceConfig, mallGoodsSku.getPrice());

            vo.setMemberPrice(memberPrice.toString());
            vo.setSkuList(getMemberPriceByList(memberLevel, goods.getMemberDiscount(), vo.getSkuList()));

            Integer hasBuy = coreGoodsLimitBuyService.getGoodsHasBuyNumber(RequestUtils.siteId(), param.getMemberId(), vo.getGoodsId());
            vo.setHasBuy(hasBuy);
        }
        return vo;
    }

    @Override
    public List<GetBuyGoodsSelectedVo> getBuyGoodsSelected(BuyGoodsSelectedParam param) {
        if (ObjectUtil.isEmpty(param.getSkuIds())) {
            return Collections.emptyList();
        }

        QueryWrapper<MallGoodsSku> shopGoodsSkuQueryWrapper = new QueryWrapper<>();
        shopGoodsSkuQueryWrapper.eq("site_id", RequestUtils.siteId())
                .in("sku_id", param.getSkuIds());
        List<MallGoodsSku> mallGoodsSkuses = mallGoodsSkuMapper.selectList(shopGoodsSkuQueryWrapper);
        if (ObjectUtil.isEmpty(mallGoodsSkuses)) {
            return Collections.emptyList();
        }

        Set<Integer> goodsIds = CollStreamUtil.toSet(mallGoodsSkuses, MallGoodsSku::getGoodsId);
        Map<Integer, MallGoods> goodsMap = new HashMap<>(goodsIds.size(), 1);
        if (ObjectUtil.isNotEmpty(goodsIds)) {
            goodsMap.putAll(mallGoodsMapper.selectBatchIds(goodsIds).stream().collect(Collectors.toMap(MallGoods::getGoodsId, o -> o)));
        }

        List<GetBuyGoodsSelectedVo> skuList = mallGoodsSkuses.stream()
                .map(sku -> {
                    GetBuyGoodsSelectedVo vo = new GetBuyGoodsSelectedVo();
                    BeanUtils.copyProperties(sku, vo);
                    vo.setGoods(goodsMap.get(vo.getGoodsId()));

                    String small = coreUploadService.thumb(RequestUtils.siteId(), sku.getSkuImage(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType());
                    vo.setGoodsCoverThumbMid(small);
                    String mid = coreUploadService.thumb(RequestUtils.siteId(), sku.getSkuImage(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
                    vo.setGoodsCoverThumbMid(mid);
                    return vo;
                })
                .toList();

        if (ObjectUtil.isNotEmpty(param.getMemberId())) {
            MemberLevelInfoVo memberLevel = memberLevelService.findMemberLevel(param.getMemberId());

            for (GetBuyGoodsSelectedVo sku : skuList) {
                if (ObjectUtil.isNotEmpty(sku.getGoods())) {
                    JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(sku.getMemberPrice()) ? JSONUtil.parseObj(sku.getMemberPrice()) : new JSONObject();
                    BigDecimal memberPrice = goodsService.calculateMemberPrice(memberLevel, sku.getGoods().getMemberDiscount(), memberPriceConfig, sku.getPrice());
                    sku.setMemberPrice(memberPrice.toString());
                }

                Integer hasBuy = coreGoodsLimitBuyService.getGoodsHasBuyNumber(RequestUtils.siteId(), param.getMemberId(), sku.getGoodsId());
                sku.setHasBuy(hasBuy);
            }
        }

        return skuList;
    }

    private List<MallGoodsSku> getMemberPriceByList(MemberLevelInfoVo memberLevelInfoVo, String memberDiscount, List<MallGoodsSku> skuList) {
        boolean isDefault = ObjectUtil.hasEmpty(memberDiscount, memberLevelInfoVo);
        for (MallGoodsSku sku : skuList) {
            if (isDefault) {
                sku.setMemberPrice(sku.getPrice().toString());
                continue;
            }

            BigDecimal memberPrice = switch (memberDiscount) {
                case "discount" -> {
                    if (ObjectUtil.isEmpty(memberLevelInfoVo.getLevelBenefits()) || ObjectUtil.isEmpty(memberLevelInfoVo.getLevelBenefits().getJSONObject("discount")) ||
                            ObjectUtil.isEmpty(memberLevelInfoVo.getLevelBenefits().getJSONObject("discount").get("is_use"))) {
                        yield sku.getPrice();
                    }

                    yield sku.getPrice().multiply(memberLevelInfoVo.getLevelBenefits().getJSONObject("discount").getBigDecimal("discount").divide(BigDecimal.TEN));
                }
                case "fixed_price" -> {
                    if (ObjectUtil.isEmpty(sku.getMemberPrice())) {
                        throw new RuntimeException("价格计算失败");
                    }

                    JSONObject mp = JSONUtil.parseObj(sku.getMemberPrice());
                    BigDecimal levelPrice = mp.getBigDecimal("level" + memberLevelInfoVo.getLevelName());
                    yield ObjectUtil.isEmpty(levelPrice) ? sku.getPrice() : levelPrice;
                }
                default -> throw new RuntimeException("价格计算失败");
            };

            sku.setMemberPrice(memberPrice.toString());
        }
        return skuList;
    }

    @Override
    public List<Map<String, Object>> getSource() {
        JSONObject sourceType = GoodsSourceEnum.getSourceType();
        List<Map<String, Object>> result = new ArrayList<>();
        for (String key : sourceType.keySet()) {
            JSONObject item = sourceType.getJSONObject(key);
            Map<String, Object> map = new HashMap<>();
            map.put("key", key);
            map.put("name", item.getStr("name"));
            result.add(map);
        }

        return result;
    }

    @Override
    public Map<String, String> getBatchSetDict() {
        return GoodsBatchTypeEnum.getMap();
    }


    private List<Integer> supplyGoodsFilter(List<Integer> goodsIds, String setType) {
        // 如果goodsIds为空，直接返回空列表（与PHP逻辑一致）
        if (ObjectUtil.isEmpty(goodsIds)) {
            return new ArrayList<>();
        }
        
        // PHP中没有setType的条件限制，直接过滤供货商商品
        QueryWrapper<MallGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("goods_id", goodsIds)
                .eq("site_id", RequestUtils.siteId())
                .eq("supply_goods_id", 0)
                .select("goods_id");

        List<MallGoods> filteredGoods = mallGoodsMapper.selectList(queryWrapper);

        return filteredGoods.stream()
                .map(MallGoods::getGoodsId)
                .collect(Collectors.toList());
    }


    @Override
    public void batchSet(GoodsBatchSetParam param) {
        Integer isAll = param.getIsAll();
        List<Integer> goodsIds = param.getGoodsIds();
        if (ObjectUtil.isEmpty(param.getSetType())) {
            throw new AdminException("NOT_GET_SET_TYPE");
        }
        if (isAll == 0 && ObjectUtil.isEmpty(goodsIds)) {
            throw new AdminException("NOT_GET_MALL_INFO");
        }
        if (ObjectUtil.isNotEmpty(goodsIds)) {
            goodsIds = supplyGoodsFilter(goodsIds, param.getSetType());
        }
        QueryWrapper<MallGoods> supplyQueryWrapper = new QueryWrapper<>();
        supplyQueryWrapper.eq("site_id", RequestUtils.siteId())
                .ne("supply_goods_id", 0)
                .select("goods_id");
        List<MallGoods> supplyGoods = mallGoodsMapper.selectList(supplyQueryWrapper);
        List<Integer> supplyGoodsIds = supplyGoods.stream()
                .map(MallGoods::getGoodsId)
                .collect(Collectors.toList());
        Map<String, Object> saveData = new HashMap<>();
        Map<String, Object> filedData = new HashMap<>();
        Map<String, Object> skuSaveData = new HashMap<>();

        GoodsBatchTypeEnum batchTypeEnum = GoodsBatchTypeEnum.getEnumByType(param.getSetType());
        if (ObjectUtil.isNotEmpty(batchTypeEnum)) {
            switch (batchTypeEnum) {
                case LABEL:
                    if (param.getSetValue() != null && ObjectUtil.isNotEmpty(param.getSetValue().getLabelIds())) {
                        List<String> stringLabelIds = param.getSetValue().getLabelIds().stream()
                                .map(String::valueOf)
                                .collect(Collectors.toList());
                        filedData.put("label", Map.of("label_ids", stringLabelIds));
                    }
                    break;
                case SERVICE:
                    if (param.getSetValue() != null && ObjectUtil.isNotEmpty(param.getSetValue().getServiceIds())) {
                        List<String> stringServiceIds = param.getSetValue().getServiceIds().stream()
                                .map(String::valueOf)
                                .collect(Collectors.toList());
                        filedData.put("service", Map.of("service_ids", stringServiceIds));
                    }
                    break;
                case VIRTUAL_SALE_NUM:
                    // 处理虚拟销量设置
                    if (param.getSetValue() != null) {
                        filedData.put("virtual_sale_num", Map.of("virtual_sale_num", param.getSetValue().getVirtualSaleNum()));
                    }
                    break;
                case PLAT_CATEGORY:
                    // 处理平台分类设置
                    if (param.getSetValue() != null && ObjectUtil.isNotEmpty(param.getSetValue().getMallGoodsCategory())) {
                        String mallGoodsCategory = convertObjectToString(param.getSetValue().getMallGoodsCategory());
                        filedData.put("category", Map.of("goods_mall_category", mallGoodsCategory));
                    }
                    break;
                case CATEGORY:
                    // 处理商品分类设置
                    if (param.getSetValue() != null && ObjectUtil.isNotEmpty(param.getSetValue().getGoodsCategory())) {
                        List<String> stringCategoryIds = param.getSetValue().getGoodsCategory().stream()
                                .map(String::valueOf)
                                .collect(Collectors.toList());
                        filedData.put("category", Map.of("goods_category", stringCategoryIds));
                    }
                    break;
                case BRAND:
                    // 处理品牌设置
                    if (param.getSetValue() != null && ObjectUtil.isNotEmpty(param.getSetValue().getBrandId())) {
                        String brandId = convertObjectToString(param.getSetValue().getBrandId());
                        filedData.put("brand", Map.of("brand_id", brandId));
                    }
                    break;
                case DIY_FORM:
                    // 处理自定义表单设置
                    if (param.getSetValue() != null && ObjectUtil.isNotEmpty(param.getSetValue().getFormId())) {
                        String formId = convertObjectToString(param.getSetValue().getFormId());
                        filedData.put("diy_form", Map.of("form_id", formId));
                    }
                    break;
                case DELIVERY:
                    // 处理配送设置
                    if (param.getSetValue() != null && ObjectUtil.isNotEmpty(param.getSetValue().getDeliveryType())) {
                        List<String> stringDeliveryTypes = param.getSetValue().getDeliveryType().stream()
                                .map(String::valueOf)
                                .collect(Collectors.toList());
                        Map<String, Object> deliveryData = new HashMap<>();
                        deliveryData.put("delivery_type", stringDeliveryTypes);
                        deliveryData.put("is_free_shipping", param.getSetValue().getIsFreeShipping() != null ?
                                param.getSetValue().getIsFreeShipping() : 1);
                        deliveryData.put("fee_type", ObjectUtil.isNotEmpty(param.getSetValue().getFeeType()) ?
                                convertObjectToString(param.getSetValue().getFeeType()) : "template");
                        deliveryData.put("delivery_money", param.getSetValue().getDeliveryMoney() != null ?
                                param.getSetValue().getDeliveryMoney() : 0);
                        deliveryData.put("delivery_template_id", ObjectUtil.isNotEmpty(param.getSetValue().getDeliveryTemplateId()) ?
                                convertObjectToString(param.getSetValue().getDeliveryTemplateId()) : "0");
                        filedData.put("delivery", deliveryData);
                    }
                    break;
                case MEMBER_DISCOUNT:
                    // 处理会员折扣设置
                    if (param.getSetValue() != null) {
                        String memberDiscount = ObjectUtil.isNotEmpty(param.getSetValue().getMemberDiscount()) ?
                                convertObjectToString(param.getSetValue().getMemberDiscount()) : "";
                        filedData.put("member_discount", Map.of("member_discount", memberDiscount));
                    }
                    break;
                case STOCK:
                    // 检查库存设置参数是否完整
                    if (param.getSetValue() == null ||
                            ObjectUtil.isEmpty(param.getSetValue().getStockType()) ||
                            ObjectUtil.isEmpty(param.getSetValue().getStock()) ||
                            Integer.parseInt(param.getSetValue().getStock()) <= 0) {
                        return;
                    }

                    int updateStock = Integer.parseInt(param.getSetValue().getStock());

                    if ("inc".equals(param.getSetValue().getStockType())) {
                        MPJQueryWrapper<MallGoods> queryWrapper;
                        if (isAll == 1) { // 全选模式
                            queryWrapper = getBatchAllQuery(convertWhereToMap(param.getWhere()), supplyGoodsIds);
                        } else { // 指定商品模式
                            queryWrapper = new MPJQueryWrapper<>();
                            queryWrapper.setAlias("goods");
                            queryWrapper.innerJoin("?_mall_goods_sku goodsSku on goods.goods_id = goodsSku.goods_id".replace("?_", GlobalConfig.tablePrefix));
                            queryWrapper.eq("goodsSku.site_id", RequestUtils.siteId());
                            queryWrapper.eq("goods.site_id", RequestUtils.siteId());
                            queryWrapper.in("goods.goods_id", goodsIds);
                        }

                        // 执行库存增加操作
                        List<MallGoods> goodsList = mallGoodsMapper.selectList(queryWrapper);
                        List<Integer> targetGoodsIds = goodsList.stream()
                                .map(MallGoods::getGoodsId)
                                .collect(Collectors.toList());

                        if (!targetGoodsIds.isEmpty()) {
                            for (Integer goodsId : targetGoodsIds) {
                                mallGoodsSkuMapper.update(null, new UpdateWrapper<MallGoodsSku>()
                                        .eq("goods_id", goodsId)
                                        .eq("site_id", RequestUtils.siteId())
                                        .setSql("stock = stock + " + updateStock));
                            }
                        }
                    } else { // 减库存操作
                        MPJQueryWrapper<MallGoods> queryWrapper;
                        if (isAll == 1) { // 全选模式
                            queryWrapper = getBatchAllQuery(convertWhereToMap(param.getWhere()), supplyGoodsIds);
                        } else { // 指定商品模式
                            queryWrapper = new MPJQueryWrapper<>();
                            queryWrapper.setAlias("goods");
                            queryWrapper.innerJoin("?_mall_goods_sku goodsSku on goods.goods_id = goodsSku.goods_id".replace("?_", GlobalConfig.tablePrefix));
                            queryWrapper.eq("goodsSku.site_id", RequestUtils.siteId());
                            queryWrapper.eq("goods.site_id", RequestUtils.siteId());
                            queryWrapper.in("goods.goods_id", goodsIds);
                        }

                        // 执行库存减少操作
                        List<MallGoods> goodsList = mallGoodsMapper.selectList(queryWrapper);
                        List<Integer> targetGoodsIds = goodsList.stream()
                                .map(MallGoods::getGoodsId)
                                .collect(Collectors.toList());

                        if (!targetGoodsIds.isEmpty()) {
                            for (Integer goodsId : targetGoodsIds) {
                                mallGoodsSkuMapper.update(null, new UpdateWrapper<MallGoodsSku>()
                                        .eq("goods_id", goodsId)
                                        .eq("site_id", RequestUtils.siteId())
                                        .setSql("stock = CASE WHEN stock >= " + updateStock + " THEN stock - " + updateStock + " ELSE 0 END"));
                            }
                        }
                    }

                    // 库存同步操作 - 将商品总库存更新为所有SKU库存的总和
                    MPJQueryWrapper<MallGoods> syncQueryWrapper;
                    if (isAll == 1) { // 全选模式
                        syncQueryWrapper = getBatchAllQuery(convertWhereToMap(param.getWhere()), supplyGoodsIds);
                    } else { // 指定商品模式
                        syncQueryWrapper = new MPJQueryWrapper<>();
                        syncQueryWrapper.setAlias("goods");
                        syncQueryWrapper.eq("goods.site_id", RequestUtils.siteId());
                        syncQueryWrapper.in("goods.goods_id", goodsIds);
                    }

                    List<MallGoods> syncGoodsList = mallGoodsMapper.selectList(syncQueryWrapper);
                    List<Integer> syncTargetGoodsIds = syncGoodsList.stream()
                            .map(MallGoods::getGoodsId)
                            .collect(Collectors.toList());

                    if (!syncTargetGoodsIds.isEmpty()) {
                        for (Integer goodsId : syncTargetGoodsIds) {
                            mallGoodsMapper.update(null, new UpdateWrapper<MallGoods>()
                                    .eq("goods_id", goodsId)
                                    .eq("site_id", RequestUtils.siteId())
                                    .setSql("stock = (SELECT COALESCE(SUM(stock), 0) FROM " + GlobalConfig.tablePrefix + "mall_goods_sku WHERE goods_id = " + goodsId + " AND site_id = " + RequestUtils.siteId() + ")"));
                        }
                    }
                    return; // 库存操作完成直接返回
            }
        }

        // 处理其他字段的批量更新
        if (!filedData.isEmpty()) {
            String field = filedData.keySet().iterator().next(); // 修复：使用filedData而不是saveData
            Map<String, Object> update = (Map<String, Object>) filedData.get(field);

            if (isAll == 1) { // 全选模式
                MPJQueryWrapper<MallGoods> queryWrapper = getBatchAllQuery(convertWhereToMap(param.getWhere()), supplyGoodsIds);
                
                if (param.getSetType().equals(GoodsBatchTypeEnum.DELIVERY.getType())) {
                    queryWrapper.eq("goods_type", GoodsTypeEnum.REAL.getType());
                }

                // 构建更新数据
                Map<String, Object> updateData = new HashMap<>();
                for (Map.Entry<String, Object> entry : update.entrySet()) {
                    String column = entry.getKey();
                    Object value = entry.getValue();
                    
                    // 根据字段类型决定是否需要JSON序列化
                    Object finalValue;
                    if (value instanceof List) {
                        // List字段转为JSON
                        finalValue = JSONUtil.toJsonStr(value);
                    } else {
                        finalValue = value;
                    }
                    
                    updateData.put("goods." + column, finalValue);
                }

                // 执行批量更新
                List<MallGoods> goodsList = mallGoodsMapper.selectList(queryWrapper);
                if (!goodsList.isEmpty()) {
                    List<Integer> targetGoodsIds = goodsList.stream()
                            .map(MallGoods::getGoodsId)
                            .collect(Collectors.toList());

                    UpdateWrapper<MallGoods> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.in("goods_id", targetGoodsIds);
                    updateWrapper.eq("site_id", RequestUtils.siteId());

                    for (Map.Entry<String, Object> entry : updateData.entrySet()) {
                        String column = entry.getKey().substring(6); // 去掉"goods."前缀
                        Object value = entry.getValue();
                        updateWrapper.set(column, value);
                    }

                    mallGoodsMapper.update(null, updateWrapper);

                    // 如果是会员折扣设置，记录goods_ids用于后续清空SKU会员价
                    if (param.getSetType().equals(GoodsBatchTypeEnum.MEMBER_DISCOUNT.getType())) {
                        goodsIds = targetGoodsIds; // 重新赋值goods_ids（与PHP逻辑一致）
                    }
                }
            } else { // 指定商品模式
                // 如果是配送设置，过滤出实物商品
                if (param.getSetType().equals(GoodsBatchTypeEnum.DELIVERY.getType())) {
                    QueryWrapper<MallGoods> filterWrapper = new QueryWrapper<>();
                    filterWrapper.in("goods_id", goodsIds)
                            .eq("goods_type", GoodsTypeEnum.REAL.getType())
                            .eq("site_id", RequestUtils.siteId())
                            .select("goods_id");
                    List<MallGoods> realGoods = mallGoodsMapper.selectList(filterWrapper);
                    goodsIds = realGoods.stream()
                            .map(MallGoods::getGoodsId)
                            .collect(Collectors.toList());
                }

                // 批量更新指定商品
                if (!goodsIds.isEmpty()) {
                    UpdateWrapper<MallGoods> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.in("goods_id", goodsIds);
                    updateWrapper.eq("site_id", RequestUtils.siteId());

                    for (Map.Entry<String, Object> entry : update.entrySet()) {
                        String column = entry.getKey();
                        Object value = entry.getValue();
                        
                        // 根据字段类型决定是否需要JSON序列化
                        Object finalValue;
                        if (value instanceof List) {
                            // List字段转为JSON
                            finalValue = JSONUtil.toJsonStr(value);
                        } else {
                            finalValue = value;
                        }
                        
                        updateWrapper.set(column, finalValue);
                    }

                    mallGoodsMapper.update(null, updateWrapper);
                }
            }
            
            if (param.getSetType().equals(GoodsBatchTypeEnum.MEMBER_DISCOUNT.getType()) && ObjectUtil.isNotEmpty(goodsIds)) {
                mallGoodsSkuMapper.update(null, new UpdateWrapper<MallGoodsSku>()
                        .in("goods_id", goodsIds)
                        .eq("site_id", RequestUtils.siteId())
                        .set("member_price", ""));
            }
        }
    }

    /**
     * 将Object字段转换为String值
     */
    private String convertObjectToString(Object obj) {
        if (obj == null) {
            return "";
        }
        if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            return list.isEmpty() ? "" : list.get(0).toString();
        }
        return obj.toString();
    }

    /**
     * 将Where对象转换为Map
     */
    private Map<String, Object> convertWhereToMap(GoodsBatchSetParam.Where where) {
        if (where == null) {
            return new HashMap<>();
        }
        
        Map<String, Object> whereMap = new HashMap<>();
        if (where.getGoodsName() != null) whereMap.put("goods_name", where.getGoodsName());
        if (where.getGoodsCategory() != null) whereMap.put("goods_category", where.getGoodsCategory());
        if (where.getGoodsType() != null) whereMap.put("goods_type", where.getGoodsType());
        if (where.getBrandId() != null) whereMap.put("brand_id", where.getBrandId());
        if (where.getLabelIds() != null) whereMap.put("label_ids", where.getLabelIds());
        if (where.getGoodsSource() != null) whereMap.put("goods_source", where.getGoodsSource());
        if (where.getOrder() != null) whereMap.put("order", where.getOrder());
        if (where.getSort() != null) whereMap.put("sort", where.getSort());
        if (where.getStartPrice() != null) whereMap.put("start_price", where.getStartPrice());
        if (where.getEndPrice() != null) whereMap.put("end_price", where.getEndPrice());
        if (where.getStartSaleNum() != null) whereMap.put("start_sale_num", where.getStartSaleNum());
        if (where.getEndSaleNum() != null) whereMap.put("end_sale_num", where.getEndSaleNum());
        if (where.getStatus() != null) whereMap.put("status", where.getStatus());
        
        return whereMap;
    }

    public MPJQueryWrapper<MallGoods> getBatchAllQuery(Map<String, Object> where, List<Integer> goodsIds) {
        MPJQueryWrapper<MallGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("goods");
        queryWrapper.leftJoin("?_mall_goods_sku goodsSku on goods.goods_id = goodsSku.goods_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("goods.goods_id");

        queryWrapper.eq("goods.site_id", RequestUtils.siteId());

        queryWrapper.eq("goodsSku.is_default", 1);

        if (where != null) {
            Object startPriceObj = where.get("start_price");
            Object endPriceObj = where.get("end_price");

            String startPriceStr = (startPriceObj != null && !startPriceObj.toString().trim().isEmpty()) ? startPriceObj.toString().trim() : null;
            String endPriceStr = (endPriceObj != null && !endPriceObj.toString().trim().isEmpty()) ? endPriceObj.toString().trim() : null;

            if (startPriceStr != null && endPriceStr != null) {
                BigDecimal startPrice = new BigDecimal(startPriceStr);
                BigDecimal endPrice = new BigDecimal(endPriceStr);
                if (startPrice.compareTo(endPrice) > 0) {
                    BigDecimal temp = startPrice;
                    startPrice = endPrice;
                    endPrice = temp;
                }

                queryWrapper.between("goodsSku.price", startPrice, endPrice);
            } else if (startPriceStr != null) {
                queryWrapper.ge("goodsSku.price", new BigDecimal(startPriceStr));
            } else if (endPriceStr != null) {
                queryWrapper.le("goodsSku.price", new BigDecimal(endPriceStr));
            }
        }
        if (where != null) {
            Object goodsName = where.get("goods_name");
            if (goodsName != null && !goodsName.toString().trim().isEmpty()) {
                queryWrapper.like("goods.goods_name", goodsName.toString().trim());
            }

            Object goodsType = where.get("goods_type");
            if (goodsType != null && !goodsType.toString().trim().isEmpty()) {
                queryWrapper.eq("goods.goods_type", goodsType.toString());
            }
            Object brandId = where.get("brand_id");
            if (brandId != null && !brandId.toString().trim().isEmpty()) {
                queryWrapper.eq("goods.brand_id", brandId.toString());
            }

            Object goodsCategory = where.get("goods_category");
            if (goodsCategory != null) {
                if (goodsCategory instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<String> categoryList = (List<String>) goodsCategory;
                    if (!categoryList.isEmpty()) {
                        queryWrapper.in("goods.goods_category", categoryList);
                    }
                } else if (!goodsCategory.toString().trim().isEmpty()) {
                    queryWrapper.eq("goods.goods_category", goodsCategory.toString());
                }
            }

            Object goodsMallCategory = where.get("goods_mall_category");
            if (goodsMallCategory != null && !goodsMallCategory.toString().trim().isEmpty()) {
                queryWrapper.eq("goods.goods_mall_category", goodsMallCategory.toString());
            }

            // label_ids搜索
            Object labelIds = where.get("label_ids");
            if (labelIds != null && !labelIds.toString().trim().isEmpty()) {
                queryWrapper.like("goods.label_ids", labelIds.toString());
            }
            Object serviceIds = where.get("service_ids");
            if (serviceIds != null && !serviceIds.toString().trim().isEmpty()) {
                queryWrapper.like("goods.service_ids", serviceIds.toString());
            }

            Object startSaleNum = where.get("start_sale_num");
            Object endSaleNum = where.get("end_sale_num");
            // 检查值是否有效（不为null且不为空字符串）
            String startSaleNumStr = (startSaleNum != null && !startSaleNum.toString().trim().isEmpty()) ? startSaleNum.toString().trim() : null;
            String endSaleNumStr = (endSaleNum != null && !endSaleNum.toString().trim().isEmpty()) ? endSaleNum.toString().trim() : null;
            if (startSaleNumStr != null && endSaleNumStr != null) {
                Integer start = Integer.valueOf(startSaleNumStr);
                Integer end = Integer.valueOf(endSaleNumStr);
                if (start > end) {
                    Integer temp = start;
                    start = end;
                    end = temp;
                }
                queryWrapper.between("goods.sale_num", start, end);
            } else if (startSaleNumStr != null) {
                queryWrapper.ge("goods.sale_num", Integer.valueOf(startSaleNumStr));
            } else if (endSaleNumStr != null) {
                queryWrapper.le("goods.sale_num", Integer.valueOf(endSaleNumStr));
            }
            Object status = where.get("status");
            if (status != null && !status.toString().trim().isEmpty()) {
                queryWrapper.eq("goods.status", status.toString());
            }

            Object goodsSource = where.get("goods_source");
            if (goodsSource != null && !goodsSource.toString().trim().isEmpty()) {
                queryWrapper.eq("goods.goods_source", goodsSource.toString());
            }
        }
        if (goodsIds != null && !goodsIds.isEmpty()) {
            queryWrapper.notIn("goods.goods_id", goodsIds);
        }
        return queryWrapper;
    }

    @Override
    public Map<String, Object> sync(Integer goodsId, Integer supplySync) {
        Map<String, Object> result = new HashMap<>();
        if (ObjectUtil.isNotEmpty(goodsId)){
            //查询商品
            MallGoods goods = mallGoodsMapper.selectById(goodsId);
            if (ObjectUtil.isNotEmpty(goods)){
                // 供货商设置同步
                if(checkAddon("supply") && goods.getSupplyId() > 0){
                    if(supplySync == 1){
                        //这里需要处理同步设置，事件调用
                        SupplyGoodsSyncEventDefiner.SupplyGoodsSyncEvent event = new SupplyGoodsSyncEventDefiner.SupplyGoodsSyncEvent();
                        event.setGoodsId(goods.getSupplyGoodsId());
                        List<SupplyGoodsSyncEventDefiner.SupplyGoodsSyncResult> supplyData = EventAndSubscribeOfPublisher.publishAndCallback(event);

                        for (SupplyGoodsSyncEventDefiner.SupplyGoodsSyncResult item : supplyData) {
                            if (item == null) continue;
                            if (ObjectUtil.isNotEmpty(item.getGoodsName())) goods.setGoodsName(item.getGoodsName());
                            if (ObjectUtil.isNotEmpty(item.getSubTitle())) goods.setSubTitle(item.getSubTitle());
                            if (ObjectUtil.isNotEmpty(item.getGoodsCover())) goods.setGoodsCover(item.getGoodsCover());
                            if (ObjectUtil.isNotEmpty(item.getGoodsImage())) goods.setGoodsImage(item.getGoodsImage());
                            if (ObjectUtil.isNotEmpty(item.getGoodsDesc())) goods.setGoodsDesc(item.getGoodsDesc());
                        }
                    }
                    // 商品禁止编辑字段（只对供货商商品生效）
                    List<String> mergedList = new ArrayList<>();
                    if (supplySync == 1) {
                        // 启用同步时，合并基础禁用字段和同步启用字段
                        mergedList.addAll(DisableEditField.DISABLE_EDIT_FIELD);
                        mergedList.addAll(DisableEditField.ENABLE_SYNC_FIELD);
                    } else {
                        // 禁用同步时，只返回基础禁用字段
                        mergedList.addAll(DisableEditField.DISABLE_EDIT_FIELD);
                    }
                    result.put("disable_edit_field", mergedList);
                }
                Map<String, Object> goodsInfo = new HashMap<>();
                goodsInfo.put("goods_id", goods.getGoodsId());
                goodsInfo.put("goods_name", goods.getGoodsName());
                goodsInfo.put("goods_cover", goods.getGoodsCover());
                goodsInfo.put("goods_image", goods.getGoodsImage());
                goodsInfo.put("goods_desc", goods.getGoodsDesc());
                goodsInfo.put("goods_video", goods.getGoodsVideo());
                goodsInfo.put("sub_title", goods.getSubTitle());
                goodsInfo.put("supply_goods_id", goods.getSupplyGoodsId());
                goodsInfo.put("supply_id", goods.getSupplyId());
                result.put("goods_info", goodsInfo);
            }
        }

        return result;
    }
}
