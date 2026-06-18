package com.niu.mall.enums.goods;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum GoodsImportStatusEnum {
    WAIT(0, "待处理"),
    PROCESSING(1, "处理中"),
    SUCCESS(2, "导入完成"),
    FAIL(3, "导入失败");

    private final Integer status;
    private final String name;

    public static Map<Integer, String> getMap() {
        Map<Integer, String> map = new HashMap<>();
        for (GoodsImportStatusEnum item : GoodsImportStatusEnum.values()) {
            map.put(item.getStatus(), item.getName());
        }
        return map;
    }

    public static String getNameByValue(Integer value) {
        for (GoodsImportStatusEnum item : GoodsImportStatusEnum.values()) {
            if (item.getStatus().equals(value)) {
                return item.getName();
            }
        }
        return "";
    }

    public static GoodsImportStatusEnum getByValue(Integer value) {
        for (GoodsImportStatusEnum item : GoodsImportStatusEnum.values()) {
            if (item.getStatus().equals(value)) {
                return item;
            }
        }
        return null;
    }
}
