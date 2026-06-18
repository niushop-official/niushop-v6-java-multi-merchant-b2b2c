package com.niu.mall.enums.order;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.json.JsonModuleLoader;
import jodd.util.StringUtil;

public class OrderTypeEnum {

    public static Object getOrderType(String key) {
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONObject jsonObject = jsonModuleLoader.mergeResultElement("order/order_type.json");
        if (StringUtil.isNotEmpty(key)) {
            if (StringUtil.isNotEmpty(jsonObject.getStr(key))) {
                return jsonObject.getStr(key);
            }
        }
        return jsonObject;
    }

    /**
     * 获取订单类型展示名称，普通 mall 订单返回空字符串
     */
    public static String getOrderTypeName(String orderType) {
        if (StringUtil.isEmpty(orderType) || "mall".equals(orderType)) {
            return "";
        }
        Object result = getOrderType(orderType);
        if (result == null || JSONUtil.isTypeJSON(result.toString())) {
            return "";
        }
        return result.toString();
    }

}
