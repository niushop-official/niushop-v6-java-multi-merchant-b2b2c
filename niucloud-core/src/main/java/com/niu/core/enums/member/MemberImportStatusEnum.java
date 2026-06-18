package com.niu.core.enums.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum MemberImportStatusEnum {
    PROCESSING(1, "进行中"),
    FINISH(2, "已完成"),
    FAIL(3, "已失败");

    private final Integer value;
    private final String desc;

    public static Map<Integer, String> getMap() {
        Map<Integer, String> map = new HashMap<>();
        for (MemberImportStatusEnum item : MemberImportStatusEnum.values()) {
            map.put(item.getValue(), item.getDesc());
        }
        return map;
    }

    public static String getNameByValue(Integer value) {
        for (MemberImportStatusEnum item : MemberImportStatusEnum.values()) {
            if (item.getValue().equals(value)) {
                return item.getDesc();
            }
        }
        return "";
    }

    public static MemberImportStatusEnum getByValue(Integer value) {
        for (MemberImportStatusEnum item : MemberImportStatusEnum.values()) {
            if (item.getValue().equals(value)) {
                return item;
            }
        }
        return null;
    }
}
