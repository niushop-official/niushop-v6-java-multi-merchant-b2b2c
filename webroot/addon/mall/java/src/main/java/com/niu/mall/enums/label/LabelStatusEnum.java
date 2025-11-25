package com.niu.mall.enums.label;

import cn.hutool.core.util.ObjectUtil;
import com.niu.mall.enums.mall.MallFloorStatusTypeEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum LabelStatusEnum {
    CLOSE(0, "关闭"),
    ENABLE(1, "启用"),
    ;

    private final int value;
    private final String name;

    LabelStatusEnum(int value, String name) {
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
