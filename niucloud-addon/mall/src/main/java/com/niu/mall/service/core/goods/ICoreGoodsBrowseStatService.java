package com.niu.mall.service.core.goods;

import java.util.Map;

/**
 * 商品足迹访客统计
 */
public interface ICoreGoodsBrowseStatService {

    /**
     * 统计时间范围内站点商品访客数（去重会员）
     */
    int countDistinctMembers(Integer siteId, long startTime, long endTime);

    /**
     * 按天统计访客数，key 为 yyyy-MM-dd
     */
    Map<String, Integer> countDistinctMembersByDate(Integer siteId, long startTime, long endTime);

    /**
     * 按月统计访客数，key 为 yyyy-MM
     */
    Map<String, Integer> countDistinctMembersByMonth(Integer siteId, long startTime, long endTime);

    /**
     * 按商品统计访客数，key 为 goods_id
     */
    Map<Integer, Integer> countDistinctMembersByGoods(Integer siteId, long startTime, long endTime);
}
