package com.niu.core.enums.adminapp;

import cn.hutool.json.JSONArray;
import com.niu.core.common.utils.json.JsonModuleLoader;

public class TodoAppEnum {

    public static JSONArray getTodo(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultSet("adminapp/todo.json");
    }
}
