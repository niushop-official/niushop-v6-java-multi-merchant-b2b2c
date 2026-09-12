package com.niu.mall.enums.goods;

import com.niu.core.enums.sys.AppTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 商城商品选择组件
 * 对应 PHP mall GoodsSelectComponentsListener
 */
@Getter
@AllArgsConstructor
public enum MallGoodsSelectComponentsEnum {

    GOODS_SELECT(
            "goods_select",
            "选择商品",
            AppTypeEnum.SITE.getName(),
            "/src/addon/mall/views/site/goods/components/goods-select-popup.vue"
    ),
    FULL_REDUCTION_GOODS_SELECT(
            "full_reduction_goods_select",
            "选择满减商品",
            AppTypeEnum.SITE.getName(),
            "/src/addon/mall/views/site/marketing/full_reduction/components/goods-select-popup.vue"
    );

    private final String key;
    private final String name;
    private final String appType;
    private final String component;

    public static final String TRADE_TYPE = "mall";

    public static MallGoodsSelectComponentsEnum getByKey(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }
        for (MallGoodsSelectComponentsEnum item : values()) {
            if (item.key.equals(key)) {
                return item;
            }
        }
        return null;
    }

    public static Map<String, Map<String, String>> getMap(String appType) {
        Map<String, Map<String, String>> map = new LinkedHashMap<>();
        if (appType == null || AppTypeEnum.ADMIN.getName().equals(appType)) {
            return map;
        }
        for (MallGoodsSelectComponentsEnum item : values()) {
            if (!item.appType.equals(appType)) {
                continue;
            }
            Map<String, String> row = new LinkedHashMap<>();
            row.put("key", item.key);
            row.put("name", item.name);
            row.put("component", item.component);
            map.put(item.key, row);
        }
        return map;
    }

    public static Map<String, Map<String, String>> getMap(String tradeType, String appType) {
        if (!TRADE_TYPE.equals(tradeType)) {
            return new LinkedHashMap<>();
        }
        return getMap(appType);
    }

    /**
     * 转为 JSONObject，供 ComponentsEnum 按站点主应用加载
     */
    public static cn.hutool.json.JSONObject toJSONObject() {
        cn.hutool.json.JSONObject root = new cn.hutool.json.JSONObject(true);
        for (MallGoodsSelectComponentsEnum item : values()) {
            if (!AppTypeEnum.SITE.getName().equals(item.appType)) {
                continue;
            }
            cn.hutool.json.JSONObject row = new cn.hutool.json.JSONObject(true);
            row.set("key", item.key);
            row.set("name", item.name);
            row.set("component", item.component);
            root.set(item.key, row);
        }
        return root;
    }
}
