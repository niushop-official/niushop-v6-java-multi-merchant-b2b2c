package com.niu.core.enums.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum ShopGroupCreateTypeEnum {
    APP("app", "应用"),
    SYSTEM("system", "系统"),
    USER("user", "用户");
    private final String status;

    private final String name;


    /**
     * 获取店铺关注状态
     * @return
     */
    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for (ShopGroupCreateTypeEnum item : ShopGroupCreateTypeEnum.values()) {
            map.put(item.getStatus(), item.getName());
        }
        return map;
    }

    public static String getNameByStatus(String status) {
        for (ShopGroupCreateTypeEnum item : ShopGroupCreateTypeEnum.values()) {
            if (Objects.equals(item.getStatus(), status)) {
                return item.getName();
            }
        }
        return "";
    }

    }
