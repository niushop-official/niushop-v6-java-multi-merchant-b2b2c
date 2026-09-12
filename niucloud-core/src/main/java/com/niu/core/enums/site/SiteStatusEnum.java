package com.niu.core.enums.site;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 站点枚举类
 */
@Getter
@AllArgsConstructor
public enum SiteStatusEnum {
    EXPIRE("已到期", 2),
    ON("正常", 1),
    CLOSE("已停止", 3),
    DELETE("删除", 0),
    ;
    private final String name;
    private final Integer code;

    public static Map<Integer, String> getMap() {
        Map<Integer, String> map = new HashMap<>();
        for (SiteStatusEnum item : SiteStatusEnum.values()) {
            map.put(item.getCode(), item.getName());
        }
        return map;
    }

    /**
     * 通过code获取当前名称
     *
     * @param code
     * @return
     */
    public static String getNameByCode(Integer code) {
        for (SiteStatusEnum item : SiteStatusEnum.values()) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }

    public static Map<Integer, String> getStatus() {
        return Stream.of(
                SiteStatusEnum.ON,
                SiteStatusEnum.CLOSE
        ).collect(Collectors.toMap(SiteStatusEnum::getCode, SiteStatusEnum::getName));
    }
}
