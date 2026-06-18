package com.niu.core.entity.sys;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 行业类目字典常量类
 */
public final class SiteCategoryDict {

    /**
     * 私有构造函数，防止实例化
     */
    private SiteCategoryDict() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    /**
     * 状态常量
     */
    public static final Integer EXPIRE = 2;  // 过期
    public static final Integer ON = 1;      // 正常
    public static final Integer CLOSE = 3;   // 停止

    /**
     * 默认Logo列表
     */
    public static final List<String> LOGO = Collections.unmodifiableList(Arrays.asList(
            "/static/resource/images/site/back01.jpg",
            "/static/resource/images/site/back02.jpg",
            "/static/resource/images/site/back03.jpg",
            "/static/resource/images/site/back04.jpg"
    ));

    /**
     * 状态映射表（静态常量，只初始化一次）
     */
    private static final Map<Integer, String> STATUS_MAP;

    static {
        Map<Integer, String> map = new HashMap<>();
        map.put(ON, "正常");
        map.put(EXPIRE, "过期");
        map.put(CLOSE, "停止");
        STATUS_MAP = Collections.unmodifiableMap(map);
    }

    /**
     * 获取行业类目状态映射
     *
     * @return 不可变的状态映射表
     */
    public static Map<Integer, String> getSiteCategory() {
        return STATUS_MAP;
    }

    /**
     * 获取状态对应的中文描述
     *
     * @param status 状态值
     * @return 中文描述，未找到返回空字符串
     */
    public static String getStatusDesc(Integer status) {
        if (status == null) {
            return "";
        }
        return STATUS_MAP.getOrDefault(status, "");
    }
}