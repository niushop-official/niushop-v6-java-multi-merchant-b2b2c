package com.niu.core.enums.local_delivery;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.enums.order.TradeTypeEnum;

public class LocalDeliveryTypeEnum {
    public static JSONObject getType(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultElement(RequestUtils.siteId(), "delivery/local_delivery_type.json");
    }

    public static String getName(String type) {
        JSONObject jsonObject = LocalDeliveryTypeEnum.getType();
        String name = "";
        if(!ObjectUtil.isEmpty(type)){
            JSONObject jsonObject1 = jsonObject.getJSONObject(type);
           if(jsonObject1 != null && jsonObject1.containsKey("name")){
               name = jsonObject1.getStr("name");
           }
        }
        return name;
    }
}
