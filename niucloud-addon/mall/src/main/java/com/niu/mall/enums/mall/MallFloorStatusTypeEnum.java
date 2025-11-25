package com.niu.mall.enums.mall;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MallFloorStatusTypeEnum {
    SHOW(0, "隐藏"),
    HIDE(1, "显示"),
    ;

    private final int value;
    private final String name;

    MallFloorStatusTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static String getNameByValue(Integer value) {
        return Arrays.stream(MallFloorStatusTypeEnum.values())
                .filter(type -> ObjectUtil.equal(type.getValue(), value))
                .map(MallFloorStatusTypeEnum::getName)
                .findFirst()
                .orElse("");
    }

}
