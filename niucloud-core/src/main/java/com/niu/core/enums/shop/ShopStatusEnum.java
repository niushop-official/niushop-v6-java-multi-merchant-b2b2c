package com.niu.core.enums.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum ShopStatusEnum {
    ON("营业中", 1),
    CLOSE("已打烊", 0),
    ;
    private final String name;
    private final Integer code;

    /**
     * 获取店铺申请状态
     *
     * @return
     */
    public static Map<Integer, String> getStatus() {
        Map<Integer, String> map = new HashMap<>();
        map.put(ShopStatusEnum.ON.code, ShopStatusEnum.ON.name);
        map.put(ShopStatusEnum.CLOSE.code, ShopStatusEnum.ON.name);
        return map;
    }

    public static String getNameByKey(Integer key) {
        return Arrays.stream(ShopStatusEnum.values())
                .filter(e -> e.getCode().equals(key))
                .map(ShopStatusEnum::getName)
                .findFirst()
                .orElse("");
    }
}
