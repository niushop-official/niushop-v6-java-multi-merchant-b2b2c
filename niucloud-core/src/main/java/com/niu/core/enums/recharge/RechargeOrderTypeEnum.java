package com.niu.core.enums.recharge;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum RechargeOrderTypeEnum {
    ORDER_CREATE_ACTION("order_create", "订单创建"),
    ORDER_PAY_ACTION("order_pay", "订单支付"),
    ORDER_CLOSE_ACTION("order_close", "订单关闭");

    private final String type;

    private final String name;

    public static Map<String, Object> getMap() {
         Map<String, Object> map = new HashMap<>();
        for(RechargeOrderTypeEnum item : RechargeOrderTypeEnum.values()){
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    /**
     * 通过status获取当前名称
     * @param type
     * @return
     */
    public static String getNameByStatus(String type){
        for(RechargeOrderTypeEnum item : RechargeOrderTypeEnum.values()){
            if(item.getType().equals(type)){
                return item.getName();
            }
        }
        return "";
    }
}
