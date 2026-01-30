package com.niu.core.service.core.adminapp;

import cn.hutool.json.JSONArray;

/**
 * 手机管理端应用核心服务接口
 */
public interface ICoreAdminAppService {

    /**
     * 获取手机管理端应用配置值
     * @param uid 用户ID
     * @param siteId 站点ID
     * @param type 设置项类型 app应用 stat统计 todo待办事项
     * @return 配置值数组
     */
    JSONArray getValue(Integer uid, Integer siteId, String type);

    /**
     * 设置手机管理端应用配置值
     * @param uid 用户ID
     * @param siteId 站点ID
     * @param type 设置项类型 app应用 stat统计 todo待办事项
     * @param value 配置值数组
     * @return 是否成功
     */
    Boolean setValue(Integer uid, Integer siteId, String type, JSONArray value);
}
