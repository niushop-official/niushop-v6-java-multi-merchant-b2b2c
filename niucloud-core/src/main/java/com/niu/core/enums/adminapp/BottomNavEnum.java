package com.niu.core.enums.adminapp;

import cn.hutool.json.JSONArray;
import com.niu.core.common.utils.json.JsonModuleLoader;

public class BottomNavEnum {

    public static JSONArray getBottomNav(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultSet("adminapp/bottom_nav.json");
    }
}
