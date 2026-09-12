package com.niu.core.listener.system;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.enums.sys.AppTypeEnum;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 从 show_*_app.json 加载应用/营销列表配置
 */
public final class ShowAppConfigLoader {

    private static final String ADMIN_JSON = "sys/show_admin_app.json";
    private static final String SITE_JSON = "sys/show_site_app.json";

    private ShowAppConfigLoader() {
    }

    public static List<Map<String, String>> loadShowAppItems(String appType) {
        JSONObject config = loadConfig(appType);
        if (config == null || config.isEmpty()) {
            return List.of();
        }
        List<Map<String, String>> items = new ArrayList<>();
        appendSection(items, config.getJSONArray("app"), "operation");
        appendSection(items, config.getJSONArray("tool"), "system");
        return items;
    }

    public static List<Map<String, String>> loadPromotionItems(String appType) {
        JSONObject config = loadConfig(appType);
        if (config == null || config.isEmpty()) {
            return List.of();
        }
        List<Map<String, String>> items = new ArrayList<>();
        JSONArray promotion = config.getJSONArray("promotion");
        if (promotion == null || promotion.isEmpty()) {
            return items;
        }
        for (int i = 0; i < promotion.size(); i++) {
            JSONObject jsonObject = promotion.getJSONObject(i);
            if (jsonObject == null) {
                continue;
            }
            String groupKey = jsonObject.getStr("group_key");
            if (StrUtil.isBlank(groupKey)) {
                groupKey = "activity";
            }
            items.add(normalizeItem(jsonObject, groupKey));
        }
        return items;
    }

    private static JSONObject loadConfig(String appType) {
        String jsonFile = AppTypeEnum.SITE.getName().equals(appType) ? SITE_JSON : ADMIN_JSON;
        return JsonModuleLoader.build().mergeResultElement(jsonFile);
    }

    private static void appendSection(List<Map<String, String>> items, JSONArray section, String groupKey) {
        if (section == null || section.isEmpty()) {
            return;
        }
        for (int i = 0; i < section.size(); i++) {
            JSONObject jsonObject = section.getJSONObject(i);
            if (jsonObject == null) {
                continue;
            }
            items.add(normalizeItem(jsonObject, groupKey));
        }
    }

    private static Map<String, String> normalizeItem(JSONObject jsonObject, String defaultGroupKey) {
        Map<String, String> item = new LinkedHashMap<>();
        item.put("title", jsonObject.getStr("title", ""));
        item.put("desc", jsonObject.getStr("desc", ""));
        item.put("icon", jsonObject.getStr("icon", ""));
        item.put("key", jsonObject.getStr("key", ""));
        item.put("url", jsonObject.getStr("url", ""));
        String groupKey = jsonObject.getStr("group_key");
        item.put("group_key", StrUtil.isNotBlank(groupKey) ? groupKey : defaultGroupKey);
        String addonKey = jsonObject.getStr("addon_key");
        item.put("addon_key", ObjectUtil.isNotNull(addonKey) ? addonKey : "");
        return item;
    }
}
