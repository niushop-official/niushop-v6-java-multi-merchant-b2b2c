package com.niu.core.enums.order;


import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.json.JsonModuleLoader;

/**
 * 获取交易类型
 */
public class TradeTypeEnum {

    public static JSONObject getType() {
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultElement("trade_type.json");
    }

    public static String getType(String type) {
        JSONObject jsonObject = TradeTypeEnum.getType();
        return jsonObject.getStr(type);
    }
}
