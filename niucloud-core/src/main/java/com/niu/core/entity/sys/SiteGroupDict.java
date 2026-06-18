package com.niu.core.entity.sys;


import java.util.HashMap;
import java.util.Map;

/**
 * 站点套餐字典常量类
 */
public class SiteGroupDict {

    /**
     * 创建方式常量
     */
    public static final String SYSTEM = "system";  // 系统创建
    public static final String APP = "app";        // 应用创建
    public static final String USER = "user";      // 用户创建

    /**
     * 获取站点套餐创建方式映射
     * @param type 创建方式类型
     * @return 映射结果，如果type为空则返回完整映射表，否则返回对应的值
     */
    public static Object getCreateBy(String type) {
        Map<String, String> createByMap = new HashMap<>();
        createByMap.put(SYSTEM, "系统");
        createByMap.put(APP, "应用");
        createByMap.put(USER, "用户");

        if (type == null || type.isEmpty()) {
            return createByMap;
        }
        return createByMap.getOrDefault(type, "");
    }
}
