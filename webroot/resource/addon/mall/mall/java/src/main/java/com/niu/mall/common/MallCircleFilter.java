package com.niu.mall.common;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.utils.RequestUtils;

import java.util.List;

/**
 * 商圈端数据范围过滤（对齐 PHP BaseAdminService circle_ids + searchCircleIdsAttr）
 */
public final class MallCircleFilter {

    public static final String DEFAULT_CIRCLE_ID_COLUMN = "circle_id";

    private MallCircleFilter() {
    }

    /**
     * @return true 可继续查询；false 商圈端但无可管理商圈（调用方应返回空结果）
     */
    public static boolean applyCircleIdFilter(QueryWrapper<?> wrapper) {
        return applyCircleIdFilter(wrapper, DEFAULT_CIRCLE_ID_COLUMN);
    }

    public static boolean applyCircleIdFilter(QueryWrapper<?> wrapper, String column) {
        List<Integer> circleIds = resolveCircleIds();
        if (circleIds == null) {
            return true;
        }
        if (CollUtil.isEmpty(circleIds)) {
            return false;
        }
        wrapper.in(column, circleIds);
        return true;
    }

    public static boolean applyCircleIdFilter(MPJQueryWrapper<?> wrapper) {
        return applyCircleIdFilter(wrapper, DEFAULT_CIRCLE_ID_COLUMN);
    }

    public static boolean applyCircleIdFilter(MPJQueryWrapper<?> wrapper, String column) {
        List<Integer> circleIds = resolveCircleIds();
        if (circleIds == null) {
            return true;
        }
        if (CollUtil.isEmpty(circleIds)) {
            return false;
        }
        wrapper.in(column, circleIds);
        return true;
    }

    /**
     * @return null 非商圈端无需过滤；否则返回商圈 ID 列表（可能为空）
     */
    private static List<Integer> resolveCircleIds() {
        if (!"circle".equals(RequestUtils.appType())) {
            return null;
        }
        return RequestUtils.circleIds();
    }
}
