package com.niu.core.enums.components;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.entity.site.Site;
import com.niu.core.mapper.site.SiteMapper;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 商品选择组件（对齐 PHP ComponentsDict + ComponentsData 事件）
 * 优先按站点主应用加载对应插件枚举，其次回退 loader/components/goods_select.json
 */
public final class ComponentsEnum {

    private static final String JSON_FILE = "components/goods_select.json";

    /** trade_type → 组件枚举类（含静态 toJSONObject） */
    private static final Map<String, String> ENUM_CLASS_BY_TRADE_TYPE = Map.of(
            "front_warehouse", "com.niu.front_warehouse.enums.goods.FrontWarehouseGoodsSelectComponentsEnum",
            "mall", "com.niu.mall.enums.goods.MallGoodsSelectComponentsEnum"
    );

    private ComponentsEnum() {
    }

    /**
     * 当前站点主应用下的组件配置
     */
    public static JSONObject getComponents() {
        String tradeType = resolveTradeType();
        if (StrUtil.isNotBlank(tradeType)) {
            JSONObject fromEnum = loadFromEnum(tradeType);
            if (fromEnum != null && !fromEnum.isEmpty()) {
                return fromEnum;
            }
            JSONObject fromAddon = JsonModuleLoader.build().getResultElement(tradeType, JSON_FILE);
            if (fromAddon != null && !fromAddon.isEmpty()) {
                return fromAddon;
            }
        }
        Integer siteId = RequestUtils.siteId();
        if (siteId == null) {
            siteId = 0;
        }
        return JsonModuleLoader.build().mergeResultElement(siteId, JSON_FILE);
    }

    /**
     * 按类型取单个组件；type 为空返回全部
     */
    public static Object getComponents(String type) {
        JSONObject data = getComponents();
        if (StrUtil.isBlank(type)) {
            return data;
        }
        JSONObject item = data.getJSONObject(type);
        return ObjectUtil.defaultIfNull(item, new JSONObject());
    }

    public static String getComponentPath(String type) {
        Object item = getComponents(type);
        if (item instanceof JSONObject json && !json.isEmpty()) {
            return json.getStr("component", "");
        }
        return "";
    }

    private static JSONObject loadFromEnum(String tradeType) {
        String className = ENUM_CLASS_BY_TRADE_TYPE.get(tradeType);
        if (StrUtil.isBlank(className)) {
            return null;
        }
        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod("toJSONObject");
            Object result = method.invoke(null);
            if (result instanceof JSONObject json) {
                return json;
            }
        } catch (ClassNotFoundException ignored) {
            // 插件未安装
        } catch (Exception ignored) {
            // 枚举加载失败时回退 JSON
        }
        return null;
    }

    /**
     * 对齐 PHP：取 site.app 第一个作为 trade_type
     */
    private static String resolveTradeType() {
        Integer siteId = RequestUtils.siteId();
        if (siteId == null || siteId <= 0) {
            return "";
        }
        try {
            SiteMapper siteMapper = SpringContext.bean(SiteMapper.class);
            if (siteMapper == null) {
                return "";
            }
            Site site = siteMapper.selectById(siteId);
            if (site == null || StrUtil.isBlank(site.getApp())) {
                return "";
            }
            JSONArray appJson = cn.hutool.json.JSONUtil.parseArray(site.getApp());
            if (appJson == null || appJson.isEmpty()) {
                return "";
            }
            return appJson.getStr(0, "");
        } catch (Exception e) {
            return "";
        }
    }
}
