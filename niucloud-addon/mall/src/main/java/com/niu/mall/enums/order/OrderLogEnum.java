package com.niu.mall.enums.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderLogEnum {
    STORE("store"),
    MEMBER("member"),
    SYSTEM("system");

    private final String name;


}
