package com.niu.core.enums.diy;

import cn.hutool.json.JSONArray;
import com.niu.core.common.utils.json.JsonModuleLoader;

public class AdvPositionEnum {

    public static JSONArray getPosition(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultSet("diy/adv_position.json");
    }
}
