package com.niu.core.enums.recharge;

import cn.hutool.json.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum RechargeOrderStatusEnum {
    WAIT_PAY("待支付", 0),
    FINISH("已完成", 10),
    CLOSE("已关闭", -1);

    private final String name;

    private final Integer status;

    public static Map<Integer, Map<String, Object>> getMap() {
        Map<Integer, Map<String, Object>> map = new HashMap<>();
        for(RechargeOrderStatusEnum item : RechargeOrderStatusEnum.values())
        {
            Map<String, Object> mapItem = new HashMap<>();
            mapItem.put("name", item.getName());
            mapItem.put("status", item.getStatus());
            if(item.getStatus().equals(WAIT_PAY.getStatus())){
                mapItem.put("is_refund", 0);
                mapItem.put("action", "[]");
                Map<String, String> memberAction = new HashMap<>();
                memberAction.put("name", "支付");
                memberAction.put("class", "");
                memberAction.put("params", "");
                JSONArray memberActionJSON = new JSONArray();
                memberActionJSON.add(memberAction);
                mapItem.put("member_action",memberActionJSON );
            }else if(item.getStatus().equals(FINISH.getStatus())){
                mapItem.put("is_refund", 0);
                mapItem.put("action", "[]");
                JSONArray memberActionJSON = new JSONArray();
                mapItem.put("member_action", memberActionJSON);
            }else if(item.getStatus().equals(CLOSE.getStatus())){
                mapItem.put("is_refund", 0);
                mapItem.put("action", "[]");
                JSONArray memberActionJSON = new JSONArray();
                mapItem.put("member_action", memberActionJSON);
            }
            map.put(item.getStatus(), mapItem);
        }
        return map;
    }

    /**
     * 通过status获取当前名称
     * @param status
     * @return
     */
    public static String getNameByStatus(Integer status){
        for(RechargeOrderStatusEnum item : RechargeOrderStatusEnum.values())
        {
            if(item.getStatus().equals(status))
            {
                return item.getName();
            }
        }
        return "";
    }
}
