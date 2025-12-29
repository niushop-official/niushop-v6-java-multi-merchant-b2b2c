package com.niu.core.enums.adminapp;

import cn.hutool.json.JSONArray;
import com.niu.core.common.utils.json.JsonModuleLoader;

public class StatAppEnum {

    public static JSONArray getStat(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultSet("adminapp/stat.json");
    }
}
