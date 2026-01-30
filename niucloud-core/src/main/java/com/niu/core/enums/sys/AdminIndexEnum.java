package com.niu.core.enums.sys;

import cn.hutool.json.JSONArray;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;

/**
 * 平台首页加载事件
 */
public class AdminIndexEnum {

    /**
     * 平台首页加载事件
     *
     * @return
     */
    public static JSONArray getAdminIndex(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultSet(RequestUtils.siteId(), "sys/admin_index.json");
    }
}
