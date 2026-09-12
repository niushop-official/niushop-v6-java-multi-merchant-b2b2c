package com.niu.core.enums.adminapp;

import cn.hutool.json.JSONArray;
import com.niu.core.common.utils.json.JsonModuleLoader;

public class TodoAppEnum {

    public static JSONArray getTodo( Integer siteId){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultSet(siteId, "adminapp/todo.json");
    }
}
