package com.niu.core.enums.shop;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum ShopApplyEnum {
    WAIT_AUDIT(1, "待审核"),
    PASS(2, "已通过"),
    REFUSE(-1, "已拒绝"),
    WAIT_OPEN(1, "待开店"),
    OPENED(2, "已开店");

    private final Integer status;

    private final String name;


    /**
     * 获取店铺申请状态
     * @return
     */
    public static Map<Integer, String> getStatus() {
        Map<Integer, String> map = new HashMap<>();
        map.put(ShopApplyEnum.WAIT_AUDIT.status, ShopApplyEnum.WAIT_AUDIT.name);
        map.put(ShopApplyEnum.PASS.status, ShopApplyEnum.PASS.name);
        map.put(ShopApplyEnum.REFUSE.status, ShopApplyEnum.REFUSE.name);
        return map;
    }
    //获取单个状态status
    public  static  Integer getStatusByName(String name) {
        for (ShopApplyEnum value : ShopApplyEnum.values()) {
            if (value.name.equals(name)) {
                return value.status;
            }
        }
        return null;
    }


    /**
     * 转前端需要的json结构
     * @param statusMap
     * @return
     */
    public static List<Map<String, Object>> convertToLabelValueList(Map<Integer, String> statusMap) {
        return statusMap.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("value", entry.getKey());
                    item.put("label", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }

    public static String getNameByStatus(Integer status) {
        Map<Integer, String> map = ShopApplyEnum.getStatus();
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            if (entry.getKey().equals(status)) {
                return entry.getValue();
            }
        }
        return "";
    }

    /**
     * 获取店铺开启状态
     * @return
     */
    public static Map<Integer, String> getOpenStatus(){
        Map<Integer, String> map = new HashMap<>();
        map.put(ShopApplyEnum.WAIT_OPEN.status, ShopApplyEnum.WAIT_OPEN.name);
        map.put(ShopApplyEnum.OPENED.status, ShopApplyEnum.OPENED.name);
        return map;
    }

    public static String getNameByOpenStatus(Integer status) {
        Map<Integer, String> map = ShopApplyEnum.getOpenStatus();
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            if (entry.getKey().equals(status)) {
                return entry.getValue();
            }
        }
        return "";
    }


    @Data
    class ShopStatusList{

        private String label;
        private String value;


    }
}
