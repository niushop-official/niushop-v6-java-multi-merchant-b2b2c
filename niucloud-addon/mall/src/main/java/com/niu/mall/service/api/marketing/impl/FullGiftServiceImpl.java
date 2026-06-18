package com.niu.mall.service.api.marketing.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.mall.entity.fullactive.MallFullGift;
import com.niu.mall.entity.fullactive.MallFullGiftGoods;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.entity.goods.MallGoodsSku;
import com.niu.mall.enums.common.CommonActiveEnum;
import com.niu.mall.enums.fullactive.FullGiftGoodsTypeEnum;
import com.niu.mall.enums.goods.GoodsAuditStatusEnum;
import com.niu.mall.enums.goods.GoodsStatusEnum;
import com.niu.mall.enums.goods.GoodsTypeEnum;
import com.niu.mall.mapper.fullactive.FullGiftGoodsMapper;
import com.niu.mall.mapper.fullactive.FullGiftMapper;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import com.niu.mall.mapper.goods.MallGoodsSkuMapper;
import com.niu.mall.service.admin.marketing.vo.FullGiftInfoVo;
import com.niu.mall.service.api.marketing.IFullGiftService;
import com.niu.mall.service.api.marketing.param.MarketingListParam;
import com.niu.mall.service.core.marketing.ICoreFullGiftService;
import com.niu.mall.common.MarketingJsonMapper;
import com.niu.mall.service.core.marketing.param.FullGiftCalculateParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 满赠服务实现
 */
@Service
public class FullGiftServiceImpl implements IFullGiftService {

    @Resource
    private FullGiftMapper fullGiftMapper;

    @Resource
    private FullGiftGoodsMapper fullGiftGoodsMapper;

    @Resource
    private ICoreFullGiftService coreFullGiftService;

    @Resource
    private MallGoodsMapper goodsMapper;

    @Resource
    private MallGoodsSkuMapper skuMapper;

    @Resource
    private ISiteService siteService;

    /**
     * 根据商品列表获取可参与的满减活动信息
     *
     * @param params      参数，包含 goods_data 和 member_id（可选）
     * @param isCheckRule 是否校验规则（默认 true）
     * @return 每个匹配商品的活动信息列表
     */
    @Override
    public List<Map<String, Object>> getFullGiftList(MarketingListParam params, boolean isCheckRule) {
        var goodsData = params.getGoodsData();
        if (CollectionUtils.isEmpty(goodsData)) {
            return new ArrayList<>();
        }

        // 按站点分组
        Map<Integer, List<MarketingListParam.GoodsData>> siteGoodsMap = new HashMap<>();
        for (var item : goodsData) {
            Integer siteId = item.getSiteId();
            siteGoodsMap.computeIfAbsent(siteId, k -> new ArrayList<>()).add(item);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        Integer memberId = params.getMemberId() != null ? params.getMemberId() : 0;

        for (Map.Entry<Integer, List<MarketingListParam.GoodsData>> entry : siteGoodsMap.entrySet()) {
            Integer siteId = entry.getKey();
            List<MarketingListParam.GoodsData> goodsList = entry.getValue();

            // 组装站点商品数据：goods_id, sku_id, pay_money, num
            List<FullGiftCalculateParam.GoodsSkuData> siteGoodsData = new ArrayList<>();
            for (MarketingListParam.GoodsData goods : goodsList) {
                FullGiftCalculateParam.GoodsSkuData item = new FullGiftCalculateParam.GoodsSkuData();
                item.setGoodsId(goods.getGoodsId());
                item.setSkuId(goods.getSkuId());
                item.setPayMoney(goods.getPrice().multiply(BigDecimal.valueOf(goods.getNum())));
                siteGoodsData.add(item);
            }
            var param = new FullGiftCalculateParam();
            param.setMemberId(memberId);
            param.setSiteId(siteId);
            param.setCheckRule(isCheckRule);
            param.setGoodsSkuData(siteGoodsData);

            // 调用核心服务获取满减活动信息
            var activeList = coreFullGiftService.getFullGiftInfo(param);

            for (var active : activeList) {
                var matchedGoods = active.getMatchedGoods();
                if (matchedGoods != null) {
                    for (var matched : matchedGoods) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("goods_id", matched.getGoodsId());
                        item.put("sku_id", matched.getSkuId());
                        Map<String, Object> activeInfo = new HashMap<>();
                        activeInfo.put("title", CommonActiveEnum.FULL_GIFT.getName());
                        activeInfo.put("type", CommonActiveEnum.FULL_GIFT.getType());
                        activeInfo.put("id", active.getFullGiftId());
                        item.put("active_info", activeInfo);
                        result.add(item);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 满减活动详情
     *
     * @param id 活动ID
     * @return 详情 Map
     */
    @Override
    public Map<String, Object> detail(Integer id) {
        if (id == null || fullGiftMapper.selectById(id) == null) {
            return new HashMap<>();
        }
        // 与 PHP FullGiftService::detail + Core 组装一致（含 goods、goods_data、rule_json.gift_sku_data、member_type_info）
        FullGiftInfoVo vo = coreFullGiftService.info(id);
        var site = new HashMap<String, Object>();
        var siteInfo = siteService.info(vo.getSiteId());
        site.put("site_id", siteInfo.getSiteId());
        site.put("site_name", siteInfo.getSiteName());
        site.put("logo", siteInfo.getLogo());
        Map<String, Object> result = MarketingJsonMapper.toSnakeCaseMap(vo);
        result.put("site", site);
        return result;
    }


    /**
     * 获取活动关联的商品分页
     *
     * @param id 活动ID
     * @return 分页结果（数据为商品列表）
     */
    public PageResult<Map<String, Object>> getGoodsPage(PageParam pageParam, Integer id) {
        MallFullGift fullGift = fullGiftMapper.selectById(id);
        if (fullGift == null) {
            return PageResult.build(new Page<>());
        }

        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        String goodsType = fullGift.getGoodsType();
        Integer siteId = fullGift.getSiteId();

        if (goodsType.equals(FullGiftGoodsTypeEnum.SELECTED.getType())) {
            // 指定商品参与：从 mall_full_reduction_goods 表查询
            IPage<MallFullGiftGoods> goodsPage = fullGiftGoodsMapper.selectPage(new Page<>(page, limit),
                    new MPJLambdaWrapper<MallFullGiftGoods>().eq(MallFullGiftGoods::getFullGiftId, id)
            );
            // 获取商品信息及sku信息
            var skuIds = goodsPage.getRecords().stream().map(MallFullGiftGoods::getSkuId).toList();
            List<Map<String, Object>> skus = coreFullGiftService.selectGoodsAndSkuBySkuList(skuIds);
            for (var sku : skus) {
                sku.put("goods_type_name", GoodsTypeEnum.getNameByType(String.valueOf(sku.get("goods_type"))));
            }
            return PageResult.build(page, limit, goodsPage.getTotal()).setData(skus);
        } else {
            // 全部商品 或 指定商品不参与
            LambdaQueryWrapper<MallGoods> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MallGoods::getStatus, GoodsStatusEnum.ONLINE.getStatus())
                    .eq(MallGoods::getAuditStatus, GoodsAuditStatusEnum.AUDIT_ADOPT.getStatus())
                    .eq(MallGoods::getSiteId, siteId);

            // 排除指定商品
            if (goodsType.equals(FullGiftGoodsTypeEnum.SELECTED_NOT.getType())) {
                List<Integer> excludeGoodsIds = fullGiftGoodsMapper.selectList(
                        new LambdaQueryWrapper<MallFullGiftGoods>()
                                .eq(MallFullGiftGoods::getFullGiftId, id)
                                .select(MallFullGiftGoods::getGoodsId)
                ).stream().map(MallFullGiftGoods::getGoodsId).toList();
                if (!excludeGoodsIds.isEmpty()) {
                    wrapper.notIn(MallGoods::getGoodsId, excludeGoodsIds);
                }
            }
            IPage<MallGoods> goodsPage = goodsMapper.selectPage(new Page<>(page, limit), wrapper);
            // 转换结果（每个商品取第一个 SKU，或按实际业务取默认 SKU）
            List<Map<String, Object>> goodsData = new ArrayList<>();
            for (MallGoods goods : goodsPage.getRecords()) {
                // 获取商品的默认 SKU（通常取第一个）
                List<MallGoodsSku> skuList = skuMapper.selectList(
                        new LambdaQueryWrapper<MallGoodsSku>().eq(MallGoodsSku::getGoodsId, goods.getGoodsId()).last("limit 1")
                );
                if (skuList.isEmpty()) continue;
                MallGoodsSku sku = skuList.get(0);
                Map<String, Object> item = new HashMap<>();
                item.put("goods_id", goods.getGoodsId());
                item.put("sku_id", sku.getSkuId());
                item.put("sku_name", sku.getSkuName());
                item.put("sku_image", sku.getSkuImage());
                item.put("stock", sku.getStock());
                item.put("price", sku.getPrice());
                item.put("goods_type_name", GoodsTypeEnum.getNameByType(goods.getGoodsType()));
                item.put("goods_type", goods.getGoodsType());
                item.put("goods_name", goods.getGoodsName());
                goodsData.add(item);
            }
            return PageResult.build(page, limit, goodsPage.getTotal()).setData(goodsData);
        }
    }

}
