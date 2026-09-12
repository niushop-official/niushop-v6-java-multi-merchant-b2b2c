package com.niu.core.enums.sys;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AppTypeEnum {

    ADMIN("admin"),
    SITE("site"),
    API("api"),
    CIRCLE("circle");

    private final String name;//端口名称

}
