package com.niu.mall.service.api.cart.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.site.Site;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.mall.entity.cart.MallCart;
import com.niu.core.entity.goods.MallGoods;
import com.niu.core.entity.goods.MallGoodsSku;
import com.niu.mall.entity.goods.MallGoodsSpec;
import com.niu.core.enums.fullactive.FullReductionTypeEnum;
import com.niu.mall.mapper.cart.MallCartMapper;
import com.niu.core.mapper.goods.MallGoodsMapper;
import com.niu.core.mapper.goods.MallGoodsSkuMapper;
import com.niu.mall.mapper.goods.MallGoodsSpecMapper;
import com.niu.mall.service.api.cart.IMallCartService;
import com.niu.mall.service.api.cart.param.MallCartDeleteParam;
import com.niu.mall.service.api.cart.param.MallCartEditParam;
import com.niu.mall.service.api.cart.param.MallCartParam;
import com.niu.mall.service.api.cart.vo.MallCartGoodsListApiVo;
import com.niu.mall.service.api.cart.vo.MallCartGoodsListVo;
import com.niu.mall.service.api.cart.vo.MallCartListApiVo;
import com.niu.mall.service.api.cart.vo.MallCartListVo;
import com.niu.mall.service.api.goods.IGoodsService;
import com.niu.mall.service.api.marketing.IFullGiftService;
import com.niu.mall.service.api.marketing.IFullReductionService;
import com.niu.mall.service.api.marketing.IMallDiscountService;
import com.niu.mall.service.api.marketing.param.MarketingListParam;
import com.niu.mall.service.api.marketing.vo.MallDiscountGoodsInfoVo;
import com.niu.mall.service.core.goods.ICoreGoodsActivePriceService;
import com.niu.mall.service.core.goods.ICoreGoodsCartNumService;
import com.niu.mall.service.core.goods.ICoreGoodsLimitBuyService;
import com.niu.mall.service.core.goods.ICoreGoodsStatService;
import com.niu.mall.service.core.goods.param.GetShowPriceParam;
import com.niu.mall.service.core.goods.param.GoodsAddStatParam;
import com.niu.mall.service.core.goods.vo.ShowPriceVo;
import com.niu.mall.service.core.marketing.ICoreFullGiftService;
import com.niu.core.service.core.marketing.ICoreFullReductionService;
import com.niu.core.service.core.marketing.vo.FullReductionCalculateVo;
import com.niu.mall.service.core.order.ICoreMallOrderCreateService;
import com.niu.mall.service.core.order.param.MallOrderCalculateParam;
import com.niu.mall.service.core.order.vo.MallOrderCreateDataVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 购物车实现
 */
@Service
public class MallCartServiceImpl implements IMallCartService {

    @Resource
    MallCartMapper mallCartMapper;

    @Resource
    MallGoodsMapper mallGoodsMapper;

    @Resource
    MallGoodsSpecMapper mallGoodsSpecMapper;

    @Resource
    MallGoodsSkuMapper mallGoodsSkuMapper;

    @Resource
    SiteMapper siteMapper;

    @Resource
    IGoodsService goodsService;

    @Resource
    ICoreGoodsLimitBuyService coreGoodsLimitBuyService;

    @Resource
    ICoreGoodsStatService coreGoodsStatService;

    @Resource
    ICoreGoodsCartNumService coreGoodsCartNumService;

    @Resource
    ICoreGoodsActivePriceService coreGoodsActivePriceService;

    @Resource
    private ICoreMallOrderCreateService coreOrderCreateService;

    @Resource
    private IMallDiscountService discountService;
    @Resource
    private ICoreFullReductionService fullReductionService;
    @Resource
    private ICoreFullGiftService fullGiftService;
    @Resource
    private IFullGiftService apiFullGiftService;
    @Resource
    private IFullReductionService apiFullReductionService;


    /**
     * 购物车列表
     *
     * @return List<ShopCartListVo>
     */
    @Override
    public Map<Integer, MallCartListApiVo> list() {
        QueryWrapper<MallCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id", RequestUtils.memberId());
        queryWrapper.orderByDesc("id");

        List<MallCart> record = mallCartMapper.selectList(queryWrapper);

        List<MallCartListVo> list = new LinkedList<>();
        for (MallCart item : record) {
            MallCartListVo vo = new MallCartListVo();
            BeanUtils.copyProperties(item, vo);

            MallCartListVo.Goods goodsVo = new MallCartListVo.Goods();
            MallGoods goods = mallGoodsMapper.selectOne(new QueryWrapper<MallGoods>().eq("goods_id", item.getGoodsId()));
            BeanUtils.copyProperties(goods, goodsVo);

            //商品限购
            goodsVo.setHasBuy(coreGoodsLimitBuyService.getGoodsHasBuyNumber(goods.getSiteId(), RequestUtils.memberId(), item.getGoodsId()));

            vo.setGoods(goodsVo);

            MallCartListVo.GoodsSku skuVo = new MallCartListVo.GoodsSku();
            MallGoodsSku sku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>().eq("sku_id", item.getSkuId()));
            BeanUtils.copyProperties(sku, skuVo);
            ShowPriceVo skuShowPrice = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                    .goodsId(sku.getGoodsId())
                    .salePrice(sku.getSalePrice())
                    .skuId(sku.getSkuId())
                    .price(sku.getPrice())
                    .memberDiscount(goods.getMemberDiscount())
                    .memberPrice(sku.getMemberPrice())
                    .vipCardDiscount(goods.getVipCardDiscount())
                    .svipDiscount(sku.getSvipDiscount())
                    .svipPrice(sku.getSvipPrice())
                    .memberId(RequestUtils.memberId())
                    .vipCardDiscount(goods.getVipCardDiscount())
                    .svipPrice(sku.getSvipPrice())
                    .svipDiscount(sku.getSvipDiscount())
                    .build());
            skuVo.setShowPrice(skuShowPrice.getShowPrice());
            skuVo.setShowType(skuShowPrice.getShowType());
            List<MallCartListVo.GoodsSpec> specList = CollectUtils.convert(mallGoodsSpecMapper.selectList(new QueryWrapper<MallGoodsSpec>().eq("goods_id", item.getGoodsId())), MallCartListVo.GoodsSpec.class);
            vo.setGoodsSpec(specList);
            vo.setGoodsSku(skuVo);
            Site site = siteMapper.selectById(item.getSiteId());
            MallCartListVo.SiteInfo siteInfo = new MallCartListVo.SiteInfo();
            if (ObjectUtil.isNotEmpty(site)) {
                BeanUtils.copyProperties(site, siteInfo);
            }

            vo.setSite(siteInfo);
            list.add(vo);
        }

        Map<Integer, MallCartListApiVo> map = new HashMap<>();
        if (ObjectUtil.isNotEmpty(list)) {
            for (MallCartListVo item : list) {


                MallCartListApiVo apiVo = new MallCartListApiVo();
                if (map.containsKey(item.getSiteId())) {
                    apiVo = map.get(item.getSiteId());
                }
                apiVo.setSiteId(item.getSiteId());
                apiVo.setSiteName(item.getSite().getSiteName());
                apiVo.setLogo(item.getSite().getLogo());

                if (ObjectUtil.isEmpty(apiVo.getGoodsList())) {
                    apiVo.setGoodsList(new LinkedList<>());

                }
                apiVo.getGoodsList().add(item);

                map.put(item.getSiteId(), apiVo);
            }
        }
        return map;
    }

    @Override
    public Map<Integer, MallCartGoodsListApiVo> goodsList() {
        QueryWrapper<MallCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id", RequestUtils.memberId());
        queryWrapper.orderByDesc("id");

        List<MallCart> record = mallCartMapper.selectList(queryWrapper);

        List<MallCartGoodsListVo> list = new LinkedList<>();
        for (MallCart item : record) {
            MallCartGoodsListVo vo = new MallCartGoodsListVo();
            BeanUtils.copyProperties(item, vo);

            MallGoods goods = mallGoodsMapper.selectOne(new QueryWrapper<MallGoods>().eq("goods_id", item.getGoodsId()));
            if (goods == null) {
                continue;
            }
            MallCartGoodsListVo.Goods goodsVo = new MallCartGoodsListVo.Goods();
            BeanUtils.copyProperties(goods, goodsVo);

            //商品限购
            goodsVo.setHasBuy(coreGoodsLimitBuyService.getGoodsHasBuyNumber(goods.getSiteId(), RequestUtils.memberId(), item.getGoodsId()));

            vo.setGoods(goodsVo);

            MallGoodsSku sku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>().eq("sku_id", item.getSkuId()));
            if (sku == null) {
                continue;
            }
            MallCartGoodsListVo.GoodsSku skuVo = new MallCartGoodsListVo.GoodsSku();
            BeanUtils.copyProperties(sku, skuVo);
            skuVo.setMemberDiscount(ObjectUtil.defaultIfNull(goods.getMemberDiscount(), ""));
            skuVo.setVipCardDiscount(ObjectUtil.defaultIfNull(goods.getVipCardDiscount(), ""));

            ShowPriceVo skuShowPrice = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                    .goodsId(sku.getGoodsId())
                    .salePrice(sku.getSalePrice())
                    .skuId(sku.getSkuId())
                    .price(sku.getPrice())
                    .memberDiscount(goods.getMemberDiscount())
                    .memberPrice(sku.getMemberPrice())
                    .vipCardDiscount(goods.getVipCardDiscount())
                    .svipDiscount(sku.getSvipDiscount())
                    .svipPrice(sku.getSvipPrice())
                    .memberId(RequestUtils.memberId())
                    .build());
            skuVo.setShowPrice(skuShowPrice.getShowPrice());
            skuVo.setShowType(skuShowPrice.getShowType());

            // 对齐 PHP：顶层 sku_spec_format
            vo.setSkuSpecFormat(ObjectUtil.defaultIfNull(sku.getSkuSpecFormat(), ""));

            // 对齐 PHP：可售 skuList
            List<MallGoodsSku> sellSkuList = mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>()
                    .eq("goods_id", item.getGoodsId())
                    .eq("is_sell", 1));
            List<MallCartGoodsListVo.GoodsSku> skuListVo = new LinkedList<>();
            for (MallGoodsSku sellSku : sellSkuList) {
                MallCartGoodsListVo.GoodsSku sellSkuVo = new MallCartGoodsListVo.GoodsSku();
                BeanUtils.copyProperties(sellSku, sellSkuVo);
                sellSkuVo.setMemberDiscount(ObjectUtil.defaultIfNull(goods.getMemberDiscount(), ""));
                sellSkuVo.setVipCardDiscount(ObjectUtil.defaultIfNull(goods.getVipCardDiscount(), ""));
                ShowPriceVo sellShowPrice = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                        .goodsId(sellSku.getGoodsId())
                        .salePrice(sellSku.getSalePrice())
                        .skuId(sellSku.getSkuId())
                        .price(sellSku.getPrice())
                        .memberDiscount(goods.getMemberDiscount())
                        .memberPrice(sellSku.getMemberPrice())
                        .vipCardDiscount(goods.getVipCardDiscount())
                        .svipDiscount(sellSku.getSvipDiscount())
                        .svipPrice(sellSku.getSvipPrice())
                        .memberId(RequestUtils.memberId())
                        .build());
                sellSkuVo.setShowPrice(sellShowPrice.getShowPrice());
                sellSkuVo.setShowType(sellShowPrice.getShowType());
                skuListVo.add(sellSkuVo);
            }
            vo.setSkuList(skuListVo);

            List<MallCartGoodsListVo.GoodsSpec> specList = CollectUtils.convert(mallGoodsSpecMapper.selectList(new QueryWrapper<MallGoodsSpec>().eq("goods_id", item.getGoodsId())), MallCartGoodsListVo.GoodsSpec.class);
            vo.setGoodsSpec(specList);

            vo.setGoodsSku(skuVo);
            Site site = siteMapper.selectById(item.getSiteId());
            if (ObjectUtil.isEmpty(site)) {
                //失效数据
                continue;
            }
            MallCartGoodsListVo.SiteInfo siteInfo = new MallCartGoodsListVo.SiteInfo();
            BeanUtils.copyProperties(site, siteInfo);
            vo.setSite(siteInfo);

            vo.setInvalidRemark(ObjectUtil.defaultIfNull(item.getInvalidRemark(), ""));
            vo.setPromotionList(getSkuPromotionList(item.getSkuId()));
            list.add(vo);
        }

        // 对齐 PHP：goodsSku.market_list（满赠/满减活动标签）
        fillGoodsSkuMarketList(list);

        Map<Integer, MallCartGoodsListApiVo> map = new LinkedHashMap<>();
        if (ObjectUtil.isNotEmpty(list)) {
            for (MallCartGoodsListVo item : list) {
                MallCartGoodsListApiVo apiVo = map.get(item.getSiteId());
                if (apiVo == null) {
                    apiVo = new MallCartGoodsListApiVo();
                    apiVo.setSiteId(item.getSiteId());
                    apiVo.setSiteName(item.getSite().getSiteName());
                    apiVo.setLogo(ObjectUtil.defaultIfNull(item.getSite().getLogo(), ""));
                    apiVo.setGoodsList(new LinkedList<>());
                    map.put(item.getSiteId(), apiVo);
                }
                apiVo.getGoodsList().add(item);
            }
        }
        return map;
    }

    /**
     * 对齐 PHP getGoodsList 中 market_list 组装
     */
    private void fillGoodsSkuMarketList(List<MallCartGoodsListVo> list) {
        if (ObjectUtil.isEmpty(list)) {
            return;
        }
        MarketingListParam marketingParams = new MarketingListParam();
        marketingParams.setMemberId(RequestUtils.memberId());
        List<MarketingListParam.GoodsData> goodsDataList = new ArrayList<>();
        for (MallCartGoodsListVo item : list) {
            if (item.getGoodsSku() == null) {
                continue;
            }
            item.getGoodsSku().setMarketList(new ArrayList<>());
            MarketingListParam.GoodsData goodsData = new MarketingListParam.GoodsData();
            goodsData.setGoodsId(item.getGoodsId());
            goodsData.setSiteId(item.getSiteId());
            goodsData.setSkuId(item.getSkuId());
            goodsData.setNum(item.getNum());
            goodsData.setPrice(item.getGoodsSku().getShowPrice());
            goodsDataList.add(goodsData);
        }
        if (goodsDataList.isEmpty()) {
            return;
        }
        marketingParams.setGoodsData(goodsDataList);

        Map<Integer, Map<String, Object>> giftBySku = new HashMap<>();
        Map<Integer, Map<String, Object>> reductionBySku = new HashMap<>();
        try {
            List<Map<String, Object>> giftList = apiFullGiftService.getFullGiftList(marketingParams, false);
            if (giftList != null) {
                for (Map<String, Object> gift : giftList) {
                    Object skuIdObj = gift.get("sku_id");
                    if (skuIdObj != null) {
                        giftBySku.put(Integer.valueOf(skuIdObj.toString()), gift);
                    }
                }
            }
        } catch (Exception ignored) {
        }
        try {
            List<Map<String, Object>> reductionList = apiFullReductionService.getFullReductionList(marketingParams, false);
            if (reductionList != null) {
                for (Map<String, Object> reduction : reductionList) {
                    Object skuIdObj = reduction.get("sku_id");
                    if (skuIdObj != null) {
                        reductionBySku.put(Integer.valueOf(skuIdObj.toString()), reduction);
                    }
                }
            }
        } catch (Exception ignored) {
        }

        for (MallCartGoodsListVo item : list) {
            if (item.getGoodsSku() == null) {
                continue;
            }
            List<Map<String, Object>> marketList = new ArrayList<>();
            Map<String, Object> gift = giftBySku.get(item.getSkuId());
            if (gift != null && gift.get("active_info") instanceof Map<?, ?> activeInfo) {
                @SuppressWarnings("unchecked")
                Map<String, Object> info = (Map<String, Object>) activeInfo;
                marketList.add(info);
            }
            Map<String, Object> reduction = reductionBySku.get(item.getSkuId());
            if (reduction != null && reduction.get("active_info") instanceof Map<?, ?> activeInfo) {
                @SuppressWarnings("unchecked")
                Map<String, Object> info = (Map<String, Object>) activeInfo;
                marketList.add(info);
            }
            item.getGoodsSku().setMarketList(marketList);
        }
    }


    private List<String> getSkuPromotionList(Integer skuId) {
        List<String> promotionList = new ArrayList<>();

        // 查询 SKU 及关联商品
        MallGoodsSku sku = mallGoodsSkuMapper.selectOne(
                new QueryWrapper<MallGoodsSku>().eq("sku_id", skuId).last("limit 1"));
        if (sku == null) return promotionList;

        MallGoods goods = mallGoodsMapper.selectById(sku.getGoodsId());
        if (goods == null) return promotionList;

        // 1. 限时折扣
        MallDiscountGoodsInfoVo discountInfo = discountService.getInfoByGoods(sku.getGoodsId());
        if (discountInfo != null) {
            String activeGoodsValue = discountInfo.getActiveGoodsValue();
            try {
                List<Map<String, Object>> activeGoodsList = JSON.parseObject(activeGoodsValue, new TypeReference<>() {
                });
                Map<Long, Map<String, Object>> activeSkuMap = activeGoodsList.stream()
                        .collect(Collectors.toMap(m -> Long.parseLong(m.get("sku_id").toString()), m -> m));
                Map<String, Object> activeSku = activeSkuMap.get(skuId);
                if (activeSku != null && activeSku.containsKey("discount_rate")) {
                    String discountRate = activeSku.get("discount_rate").toString();
                    promotionList.add("限时" + discountRate + "折");
                }
            } catch (Exception e) {
                // ignore
            }
        }

        // 2. 满减
        FullReductionCalculateVo fullReductionInfo = fullReductionService.getFullReductionInfoOnlyCheck(
                sku.getGoodsId(), skuId, RequestUtils.memberId(), sku.getSiteId());
        if (fullReductionInfo != null) {
            String promotionType = fullReductionInfo.getPromotionType();
            String ruleType = fullReductionInfo.getRuleType();
            String typeUnit = FullReductionTypeEnum.NUM.getType().equals(fullReductionInfo.getType()) ? "件" : "元";
            List<Map<String, Object>> ruleJson = JSON.parseObject(fullReductionInfo.getRuleJson(), new TypeReference<>() {
            });
            if (ruleJson != null) {
                for (Map<String, Object> rule : ruleJson) {
                    BigDecimal amount = new BigDecimal(rule.get("amount").toString());
                    BigDecimal reduce = new BigDecimal(rule.get("reduce").toString());
                    String ruleTypeName = "ladder".equals(ruleType) ? "满" : "每满";
                    String promotionTypeName1 = "discount".equals(promotionType) ? "打" : "减";
                    String promotionTypeName2 = "discount".equals(promotionType) ? "折" : "元";
                    promotionList.add(ruleTypeName + amount + typeUnit + promotionTypeName1 + reduce + promotionTypeName2);
                }
            }
        }

        // 3. 满赠
        Map<String, Object> fullGiftInfo = fullGiftService.getFullGiftInfoOnlyCheck(
                sku.getGoodsId(), skuId, RequestUtils.memberId(), sku.getSiteId());
        if (fullGiftInfo != null && !fullGiftInfo.isEmpty()) {
            String ruleType = (String) fullGiftInfo.get("rule_type");
            List<Map<String, Object>> ruleJson = JSON.parseObject(fullGiftInfo.get("rule_json").toString(), new TypeReference<>() {
            });
            if (ruleJson != null) {
                for (Map<String, Object> rule : ruleJson) {
                    BigDecimal amount = new BigDecimal(rule.get("amount").toString());
                    String ruleTypeName = "ladder".equals(ruleType) ? "满" : "每满";
                    promotionList.add(ruleTypeName + amount + "元送赠品");
                }
            }
        }

        return promotionList;
    }

    /**
     * 购物车添加
     *
     * @param addParam 参数
     */
    @Override
    public void add(MallCartParam addParam) {
        // 校验 SKU 是否可售
        MallGoodsSku sku = mallGoodsSkuMapper.selectById(addParam.getSkuId());
        if (sku == null || sku.getIsSell() == 0) {
            throw new CommonException("该商品规格暂未开售");
        }

        MallCart cart = mallCartMapper.selectOne(new QueryWrapper<MallCart>()
                .eq("member_id", RequestUtils.memberId())
                .eq("sku_id", addParam.getSkuId())
        );

        MallGoods goods = mallGoodsMapper.selectById(addParam.getGoodsId());
        GoodsAddStatParam param = new GoodsAddStatParam();
        param.setSiteId(goods.getSiteId());
        param.setGoodsId(addParam.getGoodsId());
        param.setCartNum(addParam.getNum());
        coreGoodsStatService.addStat(param);
        coreGoodsCartNumService.inc(addParam.getGoodsId(), addParam.getNum());
        if (cart != null) {
            cart.setNum(cart.getNum() + addParam.getNum());
            mallCartMapper.updateById(cart);
        } else {
            MallCart model = new MallCart();
            BeanUtils.copyProperties(addParam, model);
            model.setSiteId(goods.getSiteId());
            model.setMemberId(RequestUtils.memberId());
            model.setCreateTime(System.currentTimeMillis() / 1000);
            mallCartMapper.insert(model);
        }
    }

    /**
     * 购物车编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(MallCartEditParam editParam) {
        // 2. 校验新 SKU 是否可售（若 SKU 发生变化）
        MallGoodsSku sku = mallGoodsSkuMapper.selectById(editParam.getSkuId());
        if (sku == null || sku.getIsSell() == 0) {
            throw new CommonException("该商品规格暂未开售");
        }

        MallCart model = mallCartMapper.selectOne(
                new QueryWrapper<MallCart>()
                        .eq("id", editParam.getId())
                        .eq("member_id", RequestUtils.memberId()));

        Assert.notNull(model, "数据不存在!");
        int delta = editParam.getNum() - model.getNum();

        if (!model.getSkuId().equals(editParam.getSkuId())) {
            model.setSkuId(editParam.getSkuId());

            mallCartMapper.delete(new QueryWrapper<MallCart>()
                    .eq("sku_id", editParam.getSkuId())
                    .eq("member_id", RequestUtils.memberId()));
        }
        model.setNum(editParam.getNum());
        mallCartMapper.updateById(model);

        if (delta != 0) {
            GoodsAddStatParam param = new GoodsAddStatParam();
            param.setSiteId(model.getSiteId());
            param.setGoodsId(model.getGoodsId());
            param.setCartNum(delta);
            coreGoodsStatService.addStat(param);
            coreGoodsCartNumService.inc(model.getGoodsId(), delta);
        }
    }

    /**
     * 购物车删除
     *
     * @param param 主键ID
     */
    @Override
    public void del(MallCartDeleteParam param) {
        QueryWrapper<MallCart> queryWrapper = new QueryWrapper<MallCart>()
                .eq("member_id", RequestUtils.memberId());

        if (param.getIds() instanceof Integer) {
            queryWrapper.eq("id", param.getIds());
        } else if (param.getIds() instanceof ArrayList) {
            Integer[] ids = ((ArrayList<?>) param.getIds()).toArray(new Integer[0]);
            queryWrapper.in("id", ids);
        }

        mallCartMapper.delete(queryWrapper);
    }

    /**
     * 清空购物车
     */
    @Override
    public void clear() {
        mallCartMapper.delete(new QueryWrapper<MallCart>().eq("member_id", RequestUtils.memberId()));
    }

    /**
     * 获取购物车数量
     *
     * @param goodsId
     * @return
     */
    @Override
    public Integer getSum(Integer goodsId) {
        QueryWrapper<MallCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id", RequestUtils.memberId());
        if (goodsId > 0) queryWrapper.eq("goods_id", goodsId);
        queryWrapper.select("SUM(num) as num");
        MallCart card = mallCartMapper.selectOne(queryWrapper);
        if (card == null) return 0;
        return card.getNum();
    }

    @Override
    public MallOrderCreateDataVo calculate(MallOrderCalculateParam param) {
        param.setMemberId(RequestUtils.memberId());
        param.setOrderFrom(RequestUtils.channel());
        return coreOrderCreateService.calculate(param);
    }
}