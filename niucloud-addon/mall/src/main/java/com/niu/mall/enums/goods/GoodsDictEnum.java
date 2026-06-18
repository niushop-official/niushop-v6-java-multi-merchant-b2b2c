package com.niu.mall.enums.goods;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum GoodsDictEnum {
    CUSTOM("单独定制", "custom"),
    SITE("默认设置(店铺统一设置)", "site"),
    ;

    private final String name;
    private final String value;

    GoodsDictEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static String getNameByValue(String value) {
        return Arrays.stream(GoodsDictEnum.values())
                .filter(t -> t.getValue().equals(value))
                .map(GoodsDictEnum::getName)
                .findFirst()
                .orElse("");
    }

    public static GoodsDictEnum getEnumByValue(String value) {
        return Arrays.stream(GoodsDictEnum.values())
                .filter(t -> t.getValue().equals(value))
                .findFirst()
                .orElse(null);
    }
}
