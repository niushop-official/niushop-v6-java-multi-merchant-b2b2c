package com.niu.core.enums.local_delivery;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.enums.delivery.DeliveryStoreTypeEnum;

/**
 * 本地配送订单预状态
 */
public class LocalDeliveryOrderPreStatusEnum {
    public static JSONObject getStatus(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONObject res = jsonModuleLoader.mergeResultElement(RequestUtils.siteId(), "delivery/local_delivery_order_pre_status.json");
        if(ObjectUtil.isEmpty(res))
        {
            return new JSONObject();
        }
        return res;
    }

    public static JSONObject getStatusByService(String service) {
        JSONObject jsonObject = LocalDeliveryOrderPreStatusEnum.getStatus();
        if(ObjectUtil.isNotEmpty(jsonObject) && DeliveryStoreTypeEnum.MERCHANT.getKey().equals(service)){
            return jsonObject;
        }else{
            return new JSONObject();
        }
    }
}
