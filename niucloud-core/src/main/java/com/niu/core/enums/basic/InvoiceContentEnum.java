package com.niu.core.enums.basic;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum InvoiceContentEnum {
    CONTENT_GOODS_DETAILS("商品明细", "goods_details"),
    CONTENT_GOODS_CATEGORY("商品类别", "goods_category");

    private final String name;

    private final String type;


    public static String getNameByType(String type){
        for (InvoiceContentEnum item: InvoiceContentEnum.values()) {
            if(item.getType().equals(type))
            {
                return item.getName();
            }
        }
        return "";
    }

    public static  Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for (InvoiceContentEnum item : InvoiceContentEnum.values()) {
            map.put(item.getType(), item.getName());
        }
        return map;
    }
}
