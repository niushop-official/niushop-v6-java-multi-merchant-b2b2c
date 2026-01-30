package com.niu.mall.enums.order;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.json.JsonModuleLoader;
import jodd.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class OrderStatusJsonEnum {
    public  static Map<Integer, Map<String, Object>> getOrderOrder(String orderType){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONObject jsonObject =  jsonModuleLoader.mergeResultElement("order/order_status.json");
        if(jsonObject != null && !jsonObject.isEmpty()){
            Map<Integer, Map<String, Object>> map = new HashMap<>();
            for (String key : jsonObject.keySet()) {
                if(key.equals("6")){
                    if (StringUtil.isNotEmpty(orderType) && orderType.equals("pintuan")){
                        map.put(Integer.parseInt(key), jsonObject.getJSONObject(key));
                    }
                }else{
                    map.put(Integer.parseInt(key), jsonObject.getJSONObject(key));
                }
            }
            return map;
        }
        return null;
    }
}
