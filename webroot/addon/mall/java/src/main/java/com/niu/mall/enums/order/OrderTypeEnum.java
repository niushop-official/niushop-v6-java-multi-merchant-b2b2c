package com.niu.mall.enums.order;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.json.JsonModuleLoader;
import jodd.util.StringUtil;

public class OrderTypeEnum {
    public  static  Object getOrderType(String key){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONObject jsonObject =  jsonModuleLoader.mergeResultElement("order/order_type.json");
        if(StringUtil.isNotEmpty( key)){
            if(StringUtil.isNotEmpty(jsonObject.getStr( key))){
                return jsonObject.getStr( key);
            }
        }
        return jsonObject;
    }

}
