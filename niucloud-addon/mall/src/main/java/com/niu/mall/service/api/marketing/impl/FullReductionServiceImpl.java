package com.niu.mall.service.api.marketing.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.entity.fullactive.MallFullReduction;
import com.niu.core.entity.fullactive.MallFullReductionGoods;
import com.niu.core.entity.goods.MallGoods;
import com.niu.core.entity.goods.MallGoodsSku;
import com.niu.mall.enums.common.CommonActiveEnum;
import com.niu.core.enums.fullactive.FullReductionGoodsTypeEnum;
import com.niu.core.enums.goods.GoodsAuditStatusEnum;
import com.niu.core.enums.goods.GoodsStatusEnum;
import com.niu.mall.common.MarketingJsonMapper;
import com.niu.core.enums.goods.GoodsTypeEnum;
import com.niu.core.mapper.fullactive.FullReductionGoodsMapper;
import com.niu.core.mapper.fullactive.FullReductionMapper;
import com.niu.core.mapper.goods.MallGoodsMapper;
import com.niu.core.mapper.goods.MallGoodsSkuMapper;
import com.niu.core.service.admin.marketing.vo.FullReductionInfoVo;
import com.niu.mall.service.api.marketing.IFullReductionService;
import com.niu.mall.service.api.marketing.param.MarketingListParam;
import com.niu.core.service.core.marketing.ICoreFullReductionService;
import com.niu.core.service.core.marketing.param.FullReductionCalculateParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 满减服务实现
 */
@Service
public class FullReductionServiceImpl implements IFullReductionService {

    @Resource
    private FullReductionMapper fullReductionMapper;

    @Resource
    private FullReductionGoodsMapper fullReductionGoodsMapper;

    @Resource
    private ICoreFullReductionService coreFullReductionService;

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
    public List<Map<String, Object>> getFullReductionList(MarketingListParam params, boolean isCheckRule) {
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
            List<FullReductionCalculateParam.GoodsSkuData> siteGoodsData = new ArrayList<>();
            for (MarketingListParam.GoodsData goods : goodsList) {
                FullReductionCalculateParam.GoodsSkuData item = new FullReductionCalculateParam.GoodsSkuData();
                item.setGoodsId(goods.getGoodsId());
                item.setSkuId(goods.getSkuId());
                item.setPayMoney(goods.getPrice());
                item.setNum(goods.getNum());
                siteGoodsData.add(item);
            }
            var param = new FullReductionCalculateParam();
            param.setMemberId(memberId);
            param.setSiteId(siteId);
            param.setCheckRule(isCheckRule);
            param.setGoodsSkuData(siteGoodsData);

            // 调用核心服务获取满减活动信息
            var activeList = coreFullReductionService.getFullReductionInfo(param);

            for (var active : activeList) {
                var matchedGoods = active.getMatchedGoods();
                if (matchedGoods != null) {
                    for (var matched : matchedGoods) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("goods_id", matched.getGoodsId());
                        item.put("sku_id", matched.getSkuId());
                        Map<String, Object> activeInfo = new HashMap<>();
                        activeInfo.put("title", CommonActiveEnum.FULL_REDUCTION.getName());
                        activeInfo.put("type", CommonActiveEnum.FULL_REDUCTION.getType());
                        activeInfo.put("id", active.getId());
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
        if (id == null || fullReductionMapper.selectById(id) == null) {
            return new HashMap<>();
        }
        // 与 PHP FullReductionService::detail + Core 组装一致（含 goods、rule_json 数组等）
        FullReductionInfoVo vo = coreFullReductionService.info(id);
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
        MallFullReduction fullReduction = fullReductionMapper.selectById(id);
        if (fullReduction == null) {
            return PageResult.build(new Page<>());
        }

        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        String goodsType = fullReduction.getGoodsType();
        Integer siteId = fullReduction.getSiteId();

        if (goodsType.equals(FullReductionGoodsTypeEnum.SELECTED.getType())) {
            // 指定商品参与：从 mall_full_reduction_goods 表查询
            IPage<MallFullReductionGoods> goodsPage = fullReductionGoodsMapper.selectPage(new Page<>(page, limit),
                    new MPJLambdaWrapper<MallFullReductionGoods>().eq(MallFullReductionGoods::getFullReductionId, id)
            );
            // 获取商品信息及sku信息
            var skuIds = goodsPage.getRecords().stream().map(MallFullReductionGoods::getSkuId).toList();
            List<Map<String, Object>> skus = coreFullReductionService.selectGoodsAndSkuBySkuList(skuIds);
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
            if (goodsType.equals(FullReductionGoodsTypeEnum.SELECTED_NOT.getType())) {
                List<Integer> excludeGoodsIds = fullReductionGoodsMapper.selectList(
                        new LambdaQueryWrapper<MallFullReductionGoods>()
                                .eq(MallFullReductionGoods::getFullReductionId, id)
                                .select(MallFullReductionGoods::getGoodsId)
                ).stream().map(MallFullReductionGoods::getGoodsId).toList();
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
