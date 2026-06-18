package com.niu.mall.enums.goods;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum MallGoodsSourceTypeEnum {
    PART_ADD(0, "添加商品"),
    PART_IMPORT(1, "导入商品"),
    PART_COLLECT(2, "采集商品"),
    ;

    private final Integer type;
    private final String name;

    public static Map<Integer, String> getMap() {
        Map<Integer, String> map = new HashMap<>();
        for (MallGoodsSourceTypeEnum item : MallGoodsSourceTypeEnum.values()) {
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    public static String getNameByValue(Integer value) {
        for (MallGoodsSourceTypeEnum item : MallGoodsSourceTypeEnum.values()) {
            if (item.getType().equals(value)) {
                return item.getName();
            }
        }
        return "";
    }

    public static MallGoodsSourceTypeEnum getByValue(Integer value) {
        for (MallGoodsSourceTypeEnum item : MallGoodsSourceTypeEnum.values()) {
            if (item.getType().equals(value)) {
                return item;
            }
        }
        return null;
    }
}
