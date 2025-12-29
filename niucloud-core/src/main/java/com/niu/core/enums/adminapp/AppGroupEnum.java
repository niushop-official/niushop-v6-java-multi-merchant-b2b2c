package com.niu.core.enums.adminapp;

import cn.hutool.json.JSONArray;
import com.niu.core.common.utils.json.JsonModuleLoader;

public class AppGroupEnum {

    public static JSONArray getAppGroup(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultSet("adminapp/app_group.json");
    }
}
