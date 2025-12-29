package com.niu.core.enums.local_delivery;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.enums.delivery.DeliveryStoreTypeEnum;

public class LocalDeliveryOrderCancelReasonEnum {

    /**
     * 获取reason类型
     * @return
     */
    public static JSONObject getType(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONObject res = jsonModuleLoader.mergeResultElement(RequestUtils.siteId(), "delivery/local_delivery_order_cancel_reason.json");
        if(ObjectUtil.isEmpty(res))
        {
            return new JSONObject();
        }
        return res;
    }

    public static String getTypeName(String type, String serviceType){
        JSONObject res = getType();
        if(ObjectUtil.isEmpty(res)) return "";
        if(res.containsKey(serviceType)){
            JSONObject typeJson = res.getJSONObject(serviceType);
            if(typeJson.containsKey(type)){
                return typeJson.getStr(type);
            }
        }
        return "";
    }
    public JSONObject getType(String ServiceType){
        JSONObject res = getType();
        if(ObjectUtil.isEmpty(res)) return new JSONObject();
        if(res.containsKey(ServiceType)){
            return res.getJSONObject(ServiceType);
        }
        return new JSONObject();
    }

    public static JSONObject getCancelByService(String service) {
        JSONObject jsonObject = LocalDeliveryOrderCancelReasonEnum.getType();
        if(ObjectUtil.isNotEmpty(jsonObject) && DeliveryStoreTypeEnum.DADA.getKey().equals(service)){
            return jsonObject;
        }else{
            return new JSONObject();
        }
    }
}
