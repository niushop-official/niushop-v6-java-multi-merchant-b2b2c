package com.niu.mall.enums.order;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.enums.delivery.DeliveryTypeEnum;
import io.swagger.models.auth.In;
import jodd.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum OrderStatusEnum {
    WAIT_PAY("待支付", 1, 0),
    WAIT_DELIVERY("待发货", 2, 0),
    WAIT_TAKE("待收货", 3, 0),
    FINISH("已完成", 5, 0),
    CLOSE("已关闭", -1, 0);

    private final String name;

    private final Integer status;

    private final Integer isRefund;

    public static String getNameByStatus(Integer status,String orderType){
        Map<Integer, Map<String, Object>> jsonObject = OrderStatusJsonEnum.getOrderOrder(orderType);
        if (jsonObject != null && ObjectUtil.isNotEmpty(jsonObject.get(status))){
            return jsonObject.get(status).getOrDefault("name","").toString();
        }
        return "";
    }

    public static Map<String, Object> getMapByStatus(Integer status,String orderType){
        Map<Integer, Map<String, Object>> jsonObject = OrderStatusJsonEnum.getOrderOrder(orderType);
        Map<String, Object> map = new HashMap<>();
        if (jsonObject != null && ObjectUtil.isNotEmpty(jsonObject.get(status))){
            map = jsonObject.get(status);
        }
        return map;
    }

    public static Map<String, Object> getDeliveryTypeMapByStatus(Integer status,String deliveryType,String orderType){
        Map<String, Object> map = new HashMap<>();
        if(StringUtils.isEmpty(deliveryType)){
            map = getMapByStatus(status,orderType);
        }else{
            map = getMapByStatus(status,orderType);
            if(DeliveryTypeEnum.STORE.getType().equals(deliveryType) && ObjectUtil.isNotEmpty(status)){
                if(WAIT_DELIVERY.getStatus().equals(status)){
                    map.put("name", "待提货");
                }else if(WAIT_TAKE.getStatus().equals(status)){
                    map.put("name", "已提货");
                }
            }
        }
        return map;
    }

    public static Map<Integer, Map<String, Object>> getMap() {
        Map<Integer, Map<String, Object>> map = new HashMap<>();
        Map<Integer, Map<String, Object>> jsonObject = OrderStatusJsonEnum.getOrderOrder(null);
        if (jsonObject != null){
            map = jsonObject;
        }
        return map;
    }
}
