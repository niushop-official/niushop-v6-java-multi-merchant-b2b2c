package com.niu.mall.enums.goods;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum GoodsStatusEnum {
    ONLINE("销售中", 1),
    OFFLINE("仓库中", 0),
    WAIT_AUDIT("待审核", -1),
    AUDIT_REFUSE("审核拒绝", -2),
    AUDI_TO_FINE("强制下架", -3);

    private final String name;

    private final Integer status;

    public static String getNameByStatus(Integer status){
        for (GoodsStatusEnum item: GoodsStatusEnum.values()) {
            if(item.getStatus().equals(status))
            {
                return item.getName();
            }
        }
        return "";
    }

    public static Map<String, Object> getMapByStatus(Integer status){
        Map<String, Object> map = new HashMap<>();
        for (GoodsStatusEnum item: GoodsStatusEnum.values()) {
            if(item.getStatus().equals(status))
            {
                map.put("name", item.getName());
                map.put("status", item.getStatus());
            }
        }
        return map;
    }

    public static Map<Integer, Map<String, Object>> getMap() {
        Map<Integer, Map<String, Object>> map = new HashMap<>();
        for (GoodsStatusEnum item : GoodsStatusEnum.values()) {
            Map<String, Object> mapItem = new HashMap<>();
            mapItem.put("label", item.getName());
            mapItem.put("value", item.getStatus());
            map.put(item.getStatus(), mapItem);
        }
        return map;
    }
}
