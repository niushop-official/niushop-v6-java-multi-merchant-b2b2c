package com.niu.core.enums.promotion;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.json.JsonModuleLoader;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class AdminPromotionEnum {
    
    private static final String JSONFILE = "sys/show_admin_app.json";

    public static List<Map<String, String>> getAdminPromotionMapList() {
        JSONObject entries = JsonModuleLoader.build().mergeResultElement(JSONFILE);
        log.info("======AdminPromotionEnum entries: {}", entries.toString());

        List<Map> enums = entries.getJSONArray("promotion").toList(Map.class);
        return enums.stream().map(map -> {
            Map<String, String> newMap = new HashMap<>();
            for (Object key : map.keySet()) {
                map.get(key);
                newMap.put(key.toString(), map.get(key).toString());
            }
            return newMap;
        }).toList();
    }

    public static JSONArray getAdminPromotionEnum() {
        JSONObject entries = JsonModuleLoader.build().mergeResultElement(JSONFILE);
        log.info("======AdminPromotionEnum entries: {}", entries.toString());
        return entries.getJSONArray("promotion");
    }
}
