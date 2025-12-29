package com.niu.core.enums.adminapp;

import cn.hutool.json.JSONArray;
import com.niu.core.common.utils.json.JsonModuleLoader;

public class AppsEnum {

    public static JSONArray getApps(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultSet("adminapp/app.json");
    }
}
