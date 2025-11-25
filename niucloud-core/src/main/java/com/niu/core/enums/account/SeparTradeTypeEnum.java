package com.niu.core.enums.account;

import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;

public class SeparTradeTypeEnum {

    public static JSONObject getType(){
        return JsonModuleLoader.build().mergeResultElement("separ_trade_type.json");
    }

    public static String getName(String type){
        JSONObject types = getType();
        if(types.containsKey(type))
        {
            return types.get(type).toString();
        }
        return "";
    }
}
