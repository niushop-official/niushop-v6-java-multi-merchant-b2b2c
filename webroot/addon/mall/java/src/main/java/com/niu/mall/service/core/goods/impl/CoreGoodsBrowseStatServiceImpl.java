package com.niu.mall.service.core.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.mall.entity.goods.MallGoodsBrowse;
import com.niu.mall.mapper.goods.MallGoodsBrowseMapper;
import com.niu.mall.service.core.goods.ICoreGoodsBrowseStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CoreGoodsBrowseStatServiceImpl implements ICoreGoodsBrowseStatService {

    private final MallGoodsBrowseMapper mallGoodsBrowseMapper;

    @Override
    public int countDistinctMembers(Integer siteId, long startTime, long endTime) {
        MPJQueryWrapper<MallGoodsBrowse> wrapper = buildBaseWrapper(siteId, startTime, endTime);
        wrapper.select("COUNT(DISTINCT b.member_id) AS cnt");
        List<Map<String, Object>> rows = mallGoodsBrowseMapper.selectJoinMaps(wrapper);
        return extractCount(rows);
    }

    @Override
    public Map<String, Integer> countDistinctMembersByDate(Integer siteId, long startTime, long endTime) {
        MPJQueryWrapper<MallGoodsBrowse> wrapper = buildBaseWrapper(siteId, startTime, endTime);
        wrapper.select("FROM_UNIXTIME(b.browse_time, '%Y-%m-%d') AS stat_date",
                        "COUNT(DISTINCT b.member_id) AS cnt")
                .groupBy("stat_date");
        List<Map<String, Object>> rows = mallGoodsBrowseMapper.selectJoinMaps(wrapper);
        Map<String, Integer> result = new HashMap<>();
        for (Map<String, Object> row : rows) {
            if (row.get("stat_date") == null) {
                continue;
            }
            result.put(row.get("stat_date").toString(), extractCount(List.of(row)));
        }
        return result;
    }

    @Override
    public Map<Integer, Integer> countDistinctMembersByGoods(Integer siteId, long startTime, long endTime) {
        MPJQueryWrapper<MallGoodsBrowse> wrapper = buildBaseWrapper(siteId, startTime, endTime);
        wrapper.select("b.goods_id AS goods_id", "COUNT(DISTINCT b.member_id) AS cnt")
                .groupBy("b.goods_id");
        List<Map<String, Object>> rows = mallGoodsBrowseMapper.selectJoinMaps(wrapper);
        Map<Integer, Integer> result = new HashMap<>();
        for (Map<String, Object> row : rows) {
            if (row.get("goods_id") == null) {
                continue;
            }
            result.put(Integer.parseInt(row.get("goods_id").toString()), extractCount(List.of(row)));
        }
        return result;
    }

    @Override
    public Map<String, Integer> countDistinctMembersByMonth(Integer siteId, long startTime, long endTime) {
        MPJQueryWrapper<MallGoodsBrowse> wrapper = buildBaseWrapper(siteId, startTime, endTime);
        wrapper.select("DATE_FORMAT(FROM_UNIXTIME(b.browse_time), '%Y-%m') AS stat_month",
                        "COUNT(DISTINCT b.member_id) AS cnt")
                .groupBy("stat_month");
        List<Map<String, Object>> rows = mallGoodsBrowseMapper.selectJoinMaps(wrapper);
        Map<String, Integer> result = new HashMap<>();
        for (Map<String, Object> row : rows) {
            if (row.get("stat_month") == null) {
                continue;
            }
            result.put(row.get("stat_month").toString(), extractCount(List.of(row)));
        }
        return result;
    }

    private MPJQueryWrapper<MallGoodsBrowse> buildBaseWrapper(Integer siteId, long startTime, long endTime) {
        MPJQueryWrapper<MallGoodsBrowse> wrapper = new MPJQueryWrapper<>();
        wrapper.setAlias("b")
                .innerJoin("?_mall_goods g ON b.goods_id = g.goods_id".replace("?_", GlobalConfig.tablePrefix))
                .eq("g.site_id", siteId)
                .eq("g.delete_time", 0)
                .ge("b.browse_time", startTime)
                .le("b.browse_time", endTime);
        return wrapper;
    }

    private int extractCount(List<Map<String, Object>> rows) {
        if (ObjectUtil.isEmpty(rows) || rows.get(0).get("cnt") == null) {
            return 0;
        }
        return Integer.parseInt(rows.get(0).get("cnt").toString());
    }
}
