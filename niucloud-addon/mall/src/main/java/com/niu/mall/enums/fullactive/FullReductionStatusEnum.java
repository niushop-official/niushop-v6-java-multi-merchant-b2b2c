package com.niu.mall.enums.fullactive;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum FullReductionStatusEnum {
    WAIT("wait", "未开始"),
    PROCESS("process", "进行中"),
    END("end", "已结束"),
    CLOSE("close", "已关闭");

    private final String status;
    private final String name;

    public static Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        for (FullReductionStatusEnum item : FullReductionStatusEnum.values()) {
            map.put(item.getStatus(), item.getName());
        }
        return map;
    }

    public static String getNameByStatus(String status) {
        for (FullReductionStatusEnum item : FullReductionStatusEnum.values()) {
            if (item.getStatus().equals(status)) {
                return item.getName();
            }
        }
        return "";
    }

    public static FullReductionStatusEnum getEnumByStatus(String status) {
        for (FullReductionStatusEnum item : FullReductionStatusEnum.values()) {
            if (item.getStatus().equals(status)) {
                return item;
            }
        }
        return null;
    }
}