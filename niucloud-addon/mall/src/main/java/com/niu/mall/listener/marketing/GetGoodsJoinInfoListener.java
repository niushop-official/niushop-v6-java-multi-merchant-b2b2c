package com.niu.mall.listener.marketing;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.component.context.SpringContext;
import com.niu.mall.entity.active.MallActive;
import com.niu.mall.entity.active.MallActiveGoods;
import com.niu.mall.entity.fullactive.MallFullGift;
import com.niu.mall.entity.fullactive.MallFullGiftGoods;
import com.niu.mall.entity.fullactive.MallFullReduction;
import com.niu.mall.entity.fullactive.MallFullReductionGoods;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.enums.active.ActiveClassEnum;
import com.niu.mall.enums.active.ActiveGoodsTypeEnum;
import com.niu.mall.enums.common.CommonActiveEnum;
import com.niu.mall.enums.fullactive.FullGiftStatusEnum;
import com.niu.mall.enums.fullactive.FullReductionStatusEnum;
import com.niu.mall.event.marketing.GoodsJoinInfoEventDefiner;
import com.niu.mall.mapper.active.MallActiveGoodsMapper;
import com.niu.mall.mapper.active.MallActiveMapper;
import com.niu.mall.mapper.fullactive.FullGiftGoodsMapper;
import com.niu.mall.mapper.fullactive.FullGiftMapper;
import com.niu.mall.mapper.fullactive.FullReductionGoodsMapper;
import com.niu.mall.mapper.fullactive.FullReductionMapper;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import com.niu.mall.service.site.goods.IMallGoodsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@EventCallback("mall")
@Component
public class GetGoodsJoinInfoListener extends GoodsJoinInfoEventDefiner {
    @Resource
    MallGoodsMapper mallGoodsMapper;

    private IMallGoodsService siteMallGoods() {
        return SpringContext.bean(IMallGoodsService.class);
    }

    private Map<String, Object> getCommonActiveDict() {
        @SuppressWarnings("unchecked")
        Map<String, Object> dict = (Map<String, Object>) CommonActiveEnum.getActiveShort("");
        return dict;
    }

//    @Override
//    public GoodsJoinInfoResult handleCallback(GoodsJoinInfoEvent event) {
//        GoodsJoinInfoResult result = new GoodsJoinInfoResult();
//
//        List<Integer> goodsIds = event.getGoodsIds();
//        Integer siteId = event.getSiteId();
//        Integer isGetCount = event.getIsGetCount() != null ? event.getIsGetCount() : 0;
//        Integer isAll = event.getIsAll() != null ? event.getIsAll() : 0;
//
//        List<Integer> conditionGoodsIds;
//        if (isAll == 1) {
//            if (event.getWhere() != null && !event.getWhere().isEmpty()) {
//                MPJQueryWrapper<MallGoods> batchAllQuery = siteMallGoods().getBatchAllQuery(event.getWhere(), goodsIds);
//                List<MallGoods> goodsList = mallGoodsMapper.selectList(batchAllQuery);
//                conditionGoodsIds = goodsList.stream()
//                        .map(MallGoods::getGoodsId)
//                        .collect(Collectors.toList());
//            } else {
//                conditionGoodsIds = goodsIds;
//            }
//        } else {
//            conditionGoodsIds = goodsIds;
//        }
//
//        MallActiveGoodsMapper activeGoodsMapper = SpringContext.bean(MallActiveGoodsMapper.class);
//        MallActiveMapper activeMapper = SpringContext.bean(MallActiveMapper.class);
//
//        QueryWrapper<MallActiveGoods> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("site_id", siteId);
//        queryWrapper.eq("active_goods_status", "active");
//        queryWrapper.in("active_goods_type", ActiveGoodsTypeEnum.GOODS_SINGLE.getType(), ActiveGoodsTypeEnum.GOODS_INDEPENDENT.getType());
//        queryWrapper.eq("active_class", ActiveClassEnum.DISCOUNT.getType());
//        if (!conditionGoodsIds.isEmpty()) {
//            queryWrapper.in("goods_id", conditionGoodsIds);
//        }
//
//        if (isGetCount == 1) {
//            // 如果只需要获取数量，返回计数
//            result.setCount(activeGoodsMapper.selectCount(queryWrapper).intValue());
//            return result;
//        }
//
//        // 查询活动商品列表
//        List<MallActiveGoods> activeGoodsList = activeGoodsMapper.selectList(queryWrapper);
//
//        // 获取活动信息
//        Map<Integer, MallActive> activeMap = new HashMap<>();
//        if (!activeGoodsList.isEmpty()) {
//            List<Integer> activeIds = activeGoodsList.stream()
//                    .map(MallActiveGoods::getActiveId)
//                    .distinct()
//                    .collect(Collectors.toList());
//
//            QueryWrapper<MallActive> activeQueryWrapper = new QueryWrapper<>();
//            activeQueryWrapper.select("active_id,active_name,active_desc,start_time,end_time");
//            activeQueryWrapper.in("active_id", activeIds);
//            List<MallActive> activeList = activeMapper.selectList(activeQueryWrapper);
//
//            activeMap = activeList.stream()
//                    .collect(Collectors.toMap(MallActive::getActiveId, active -> active));
//        }
//
//        // 构建返回结果
//        Map<Integer, Map<String, Object>> returnMap = new HashMap<>();
//
//        // 获取活动字典信息（这里需要根据实际的字典服务来获取）
//        Map<String, Object> commonActiveDict = getCommonActiveDict();
//
//        for (MallActiveGoods activeGoods : activeGoodsList) {
//            Integer goodsId = activeGoods.getGoodsId();
//            Integer activeId = activeGoods.getActiveId();
//
//            if (!returnMap.containsKey(goodsId)) {
//                returnMap.put(goodsId, new HashMap<>());
//            }
//
//            MallActive active = activeMap.get(activeId);
//            String activeName = active != null ? active.getActiveName() : "限时折扣";
//
//            Map<String, Object> activeInfo = new HashMap<>();
//            activeInfo.put("join_id", activeId);
//            activeInfo.put("join_type", "active_" + activeGoods.getActiveClass());
//            activeInfo.put("short", commonActiveDict.get(activeGoods.getActiveClass()));
//            activeInfo.put("name", activeName);
//
//            returnMap.get(goodsId).put("active_" + activeId, activeInfo);
//        }
//        result.setInfo(returnMap);
//        return result;
//    }

    // 存储结果：商品ID -> Map<活动标识, 活动信息>
    private Map<String, Object> commonActiveShortMap = new HashMap<>();

    /**
     * 主处理方法
     *
     * @param event 参数，包含 site_id, goods_ids, is_get_count, is_all, where 等
     * @return 如果 is_get_count 为 true 则返回总数量，否则返回每个商品参与的活动列表
     */
    @Override
    public GoodsJoinInfoResult handleCallback(GoodsJoinInfoEvent event) {
        GoodsJoinInfoResult result = new GoodsJoinInfoResult();
        int total = 0;
        commonActiveShortMap = getCommonActiveDict();

        Integer siteId = event.getSiteId();
        List<Integer> goodsIds = event.getGoodsIds();
        boolean isGetCount = event.getIsGetCount() != null && event.getIsGetCount() > 0;
        boolean isAll = event.getIsAll() != null && event.getIsAll() > 0;

        // 获取最终需要查询的商品ID列表
        List<Integer> targetGoodsIds;
        if (isAll) {
            // 调用 GoodsService 获取符合条件的商品ID
            if (event.getWhere() != null && !event.getWhere().isEmpty()) {
                MPJQueryWrapper<MallGoods> batchAllQuery = siteMallGoods().getBatchAllQuery(event.getWhere(), goodsIds);
                List<MallGoods> goodsList = mallGoodsMapper.selectList(batchAllQuery);
                targetGoodsIds = goodsList.stream()
                        .map(MallGoods::getGoodsId)
                        .collect(Collectors.toList());
            } else {
                targetGoodsIds = goodsIds;
            }
        } else {
            targetGoodsIds = goodsIds;
        }

        if (CollectionUtils.isEmpty(targetGoodsIds)) {
            if (isGetCount) {
                result.setCount(0);
            } else {
                result.setInfo(new HashMap<>());
            }
            return result;
        }

        Map<Integer, Map<String, Object>> returnMap = new HashMap<>();
        // 执行三种活动查询
        total += getActive(siteId, isGetCount, targetGoodsIds, returnMap);
        total += getFullReduction(siteId, isGetCount, targetGoodsIds, returnMap);
        total += getFullGift(siteId, isGetCount, targetGoodsIds, returnMap);

        if (isGetCount) {
            result.setCount(total);
            return result;
        }
        result.setInfo(returnMap);
        return result;
    }

    /**
     * 查询限时折扣活动
     */
    private int getActive(Integer siteId, boolean isGetCount, List<Integer> goodsIds, Map<Integer, Map<String, Object>> returnMap) {
        if (CollectionUtils.isEmpty(goodsIds)) return 0;

        MallActiveGoodsMapper activeGoodsMapper = SpringContext.bean(MallActiveGoodsMapper.class);
        MallActiveMapper activeMapper = SpringContext.bean(MallActiveMapper.class);

        // 构建查询条件
        LambdaQueryWrapper<MallActiveGoods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MallActiveGoods::getSiteId, siteId)
                .eq(MallActiveGoods::getActiveGoodsStatus, "active")
                .in(MallActiveGoods::getActiveGoodsType, ActiveGoodsTypeEnum.GOODS_SINGLE.getType(), ActiveGoodsTypeEnum.GOODS_INDEPENDENT.getType())
                .eq(MallActiveGoods::getActiveClass, ActiveClassEnum.DISCOUNT.getType())
                .in(MallActiveGoods::getGoodsId, goodsIds)
                .groupBy(MallActiveGoods::getGoodsId, MallActiveGoods::getActiveId);

        // 如果只需要获取数量，返回计数
        if (isGetCount) return Math.toIntExact(activeGoodsMapper.selectCount(wrapper));

        List<MallActiveGoods> activeGoodsList = activeGoodsMapper.selectList(wrapper);
        Map<Integer, MallActive> activeMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(activeGoodsList)) {
            // 批量获取活动信息（避免循环查询）
            Set<Integer> activeIds = activeGoodsList.stream()
                    .map(MallActiveGoods::getActiveId)
                    .collect(Collectors.toSet());
            QueryWrapper<MallActive> activeQueryWrapper = new QueryWrapper<>();
            activeQueryWrapper.select("active_id,active_name,active_desc,start_time,end_time");
            activeQueryWrapper.in("active_id", activeIds);
            List<MallActive> activeList = activeMapper.selectList(activeQueryWrapper);
            activeMap = activeList.stream()
                    .collect(Collectors.toMap(MallActive::getActiveId, active -> active));
        }

        for (MallActiveGoods item : activeGoodsList) {
            Integer goodsId = item.getGoodsId();
            Integer activeId = item.getActiveId();
            String joinKey = "active_" + activeId;

            // 获取活动名称
            MallActive activeInfo = activeMap.get(activeId);
            var map = (HashMap<String, Object>) commonActiveShortMap.getOrDefault(CommonActiveEnum.DISCOUNT.getType(), new HashMap<>());
            String activeName = activeInfo != null ? activeInfo.getActiveName() : String.valueOf(map.getOrDefault("active_name", "限时折扣"));

            Map<String, Object> joinInfo = new HashMap<>();
            joinInfo.put("join_id", activeId);
            joinInfo.put("join_type", "active_" + item.getActiveClass());
            joinInfo.put("short", map.getOrDefault("short", "折扣"));
            joinInfo.put("name", activeName);

            returnMap.computeIfAbsent(goodsId, k -> new HashMap<>())
                    .put(joinKey, joinInfo);
        }
        return 0;
    }

    /**
     * 查询满减活动
     */
    private int getFullReduction(Integer siteId, boolean isGetCount, List<Integer> goodsIds, Map<Integer, Map<String, Object>> returnMap) {
        if (CollectionUtils.isEmpty(goodsIds)) return 0;
        FullReductionGoodsMapper fullReductionGoodsMapper = SpringContext.bean(FullReductionGoodsMapper.class);

        LambdaQueryWrapper<MallFullReductionGoods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MallFullReductionGoods::getSiteId, siteId)
                .eq(MallFullReductionGoods::getStatus, FullReductionStatusEnum.PROCESS.getStatus())
                .in(MallFullReductionGoods::getGoodsId, goodsIds)
                .groupBy(MallFullReductionGoods::getGoodsId, MallFullReductionGoods::getFullReductionId);

        // 如果只需要获取数量，返回计数
        if (isGetCount) return Math.toIntExact(fullReductionGoodsMapper.selectCount(wrapper));
        List<MallFullReductionGoods> goodsList = fullReductionGoodsMapper.selectList(wrapper);

        FullReductionMapper fullReductionMapper = SpringContext.bean(FullReductionMapper.class);
        Map<Integer, String> titleMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            // 批量获取活动信息（避免循环查询）
            Set<Integer> reductionIds = goodsList.stream()
                    .map(MallFullReductionGoods::getFullReductionId)
                    .collect(Collectors.toSet());
            titleMap = fullReductionMapper.selectList(new LambdaQueryWrapper<MallFullReduction>()
                            .in(MallFullReduction::getId, reductionIds))
                    .stream().collect(Collectors.toMap(MallFullReduction::getId, MallFullReduction::getTitle));
        }

        for (MallFullReductionGoods item : goodsList) {
            Integer goodsId = item.getGoodsId();
            Integer reductionId = item.getFullReductionId();
            String joinKey = CommonActiveEnum.FULL_REDUCTION.getType() + "_" + reductionId;

            var map = (HashMap<String, Object>) commonActiveShortMap.getOrDefault(CommonActiveEnum.FULL_REDUCTION.getType(), new HashMap<>());
            String title = titleMap.getOrDefault(reductionId, String.valueOf(map.getOrDefault("active_name", "满减")));

            Map<String, Object> joinInfo = new HashMap<>();
            joinInfo.put("join_id", reductionId);
            joinInfo.put("join_type", CommonActiveEnum.FULL_REDUCTION.getType());
            joinInfo.put("short", map.getOrDefault("short", "满减"));
            joinInfo.put("name", title);

            returnMap.computeIfAbsent(goodsId, k -> new HashMap<>())
                    .put(joinKey, joinInfo);
        }
        return 0;
    }

    /**
     * 查询满赠活动
     */
    private int getFullGift(Integer siteId, boolean isGetCount, List<Integer> goodsIds, Map<Integer, Map<String, Object>> returnMap) {
        if (CollectionUtils.isEmpty(goodsIds)) return 0;

        FullGiftGoodsMapper fullGiftGoodsMapper = SpringContext.bean(FullGiftGoodsMapper.class);
        LambdaQueryWrapper<MallFullGiftGoods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MallFullGiftGoods::getSiteId, siteId)
                .eq(MallFullGiftGoods::getStatus, FullGiftStatusEnum.PROCESS.getStatus())
                .in(MallFullGiftGoods::getGoodsId, goodsIds)
                .groupBy(MallFullGiftGoods::getGoodsId, MallFullGiftGoods::getFullGiftId);

        // 如果只需要获取数量，返回计数
        if (isGetCount) return Math.toIntExact(fullGiftGoodsMapper.selectCount(wrapper));
        List<MallFullGiftGoods> goodsList = fullGiftGoodsMapper.selectList(wrapper);

        FullGiftMapper fullGiftMapper = SpringContext.bean(FullGiftMapper.class);
        Map<Integer, String> titleMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            // 批量获取活动信息（避免循环查询）
            Set<Integer> reductionIds = goodsList.stream()
                    .map(MallFullGiftGoods::getFullGiftId)
                    .collect(Collectors.toSet());
            titleMap = fullGiftMapper.selectList(new LambdaQueryWrapper<MallFullGift>()
                            .in(MallFullGift::getId, reductionIds))
                    .stream().collect(Collectors.toMap(MallFullGift::getId, MallFullGift::getTitle));
        }

        for (MallFullGiftGoods item : goodsList) {
            Integer goodsId = item.getGoodsId();
            Integer giftId = item.getFullGiftId();
            String joinKey = CommonActiveEnum.FULL_GIFT.getType() + "_" + giftId;

            var map = (HashMap<String, Object>) commonActiveShortMap.getOrDefault(CommonActiveEnum.FULL_GIFT.getType(), new HashMap<>());
            String title = titleMap.getOrDefault(giftId, String.valueOf(map.getOrDefault("active_name", "满赠")));

            Map<String, Object> joinInfo = new HashMap<>();
            joinInfo.put("join_id", giftId);
            joinInfo.put("join_type", CommonActiveEnum.FULL_GIFT.getType());
            joinInfo.put("short", map.getOrDefault("short", "满赠"));
            joinInfo.put("name", title);

            returnMap.computeIfAbsent(goodsId, k -> new HashMap<>())
                    .put(joinKey, joinInfo);
        }
        return 0;
    }
}
