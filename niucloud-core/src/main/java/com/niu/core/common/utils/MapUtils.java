package com.niu.core.common.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Map工具类
 */
public class MapUtils {

    /**
     * 将Map中的Key从驼峰(CamelCase)风格转换为下划线(UnderlineCase)风格
     *
     * @param map 源Map，Key为驼峰风格
     * @return 新Map，Key为下划线风格
     */
    public static Map<String, Object> toUnderlineMap(Map<String, Object> map) {
        // 初始化一个新的HashMap，大小按原Map容量计算，避免频繁扩容
        Map<String, Object> underlineMap = new HashMap<>(map.size(), 1);

        // 遍历原Map，进行Key格式转换后存入新Map
        map.forEach((key, value) -> underlineMap.put(StrUtil.toUnderlineCase(key), value));

        return underlineMap;
    }

    private static final String APP_ID = "ai_open_app";
    private static final String APP_SECRET = "LANCY_AI_OPEN_2026_SECRET";

    public static void main(String[] args) {

        JSONObject requestBody= new JSONObject();
        requestBody.set("shopId",12);
        requestBody.set("secondLevelCategoryName","数码");
        String bodyJson = toSortedJson(requestBody); // 验签前先做递归排序规范化
        long timestamp = System.currentTimeMillis();
        String signStr = APP_ID + "\n" + timestamp + "\n" + bodyJson;
        String signature = SecureUtil.hmacSha256(APP_SECRET.getBytes(StandardCharsets.UTF_8))
                .digestHex(signStr)
                .toUpperCase();

        String str =  HttpRequest.post("http://192.168.1.118:8086/api/open/ai/product/list")
                .header("Content-Type", "application/json; charset=utf-8")
                .header("X-App-Id", APP_ID)
                .header("X-Timestamp", String.valueOf(timestamp))
                .header("X-Signature", signature)
                .body(bodyJson)
                .execute()
                .body();
        System.out.println(str);

    }

    // ===== 工具方法 =====
    private static String toSortedJson(Object obj) {
        Object parsed = JSONUtil.parse(JSONUtil.toJsonStr(obj));
        Object normalized = normalize(parsed);
        return JSONUtil.toJsonStr(normalized);
    }
    private static Object normalize(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof JSONObject jsonObject) {
            Map<String, Object> sorted = new TreeMap<>();
            for (Map.Entry<String, Object> e : jsonObject.entrySet()) {
                sorted.put(e.getKey(), normalize(e.getValue()));
            }
            return new LinkedHashMap<>(sorted);
        }
        if (value instanceof JSONArray jsonArray) {
            List<Object> list = new ArrayList<>(jsonArray.size());
            for (Object item : jsonArray) {
                list.add(normalize(item));
            }
            return list;
        }
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> sorted = new TreeMap<>();
            for (Map.Entry<?, ?> e : map.entrySet()) {
                sorted.put(String.valueOf(e.getKey()), normalize(e.getValue()));
            }
            return new LinkedHashMap<>(sorted);
        }
        if (value instanceof List<?> list) {
            List<Object> normalizedList = new ArrayList<>(list.size());
            for (Object item : list) {
                normalizedList.add(normalize(item));
            }
            return normalizedList;
        }
        return value;
    }
}
