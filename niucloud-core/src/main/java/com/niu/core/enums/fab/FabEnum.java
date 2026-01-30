package com.niu.core.enums.fab;

import cn.hutool.json.JSONArray;
import com.niu.core.common.utils.json.JsonModuleLoader;

public class FabEnum {

    public static JSONArray getFab(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultSet("fab/fab.json");
    }
}
