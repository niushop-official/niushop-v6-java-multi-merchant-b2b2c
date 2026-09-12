package com.niu.core.common.utils.json;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;

import java.util.Collections;
import java.util.Map;

public class JsonViewUtils {

    /**
     * 将数据中的JSON字符串展示到页面并且为对象
     *
     * @param value
     * @return
     */
    public static Object toJsonObject(String value) {
        if (!StrUtil.isEmpty(value)) {
            String trim = value.trim();
            if (trim.startsWith("[")) {
                return JSON.parseArray(trim);
            } else if (trim.startsWith("{")) {
                return JSON.parseObject(trim);
            }
        }
        // 等级权益/礼包等应为对象；无法解析时返回空对象，避免前端拿到 [] 导致礼包不同步
        return new JSONObject();
    }

    /**
     * 安全解析 JSON 对象字符串（仅 {@code {...}}，数组/纯文本返回 null）
     */
    public static JSONObject parseJsonObject(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        String trim = value.trim();
        if (!trim.startsWith("{")) {
            return null;
        }
        try {
            return JSONUtil.parseObj(trim);
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * 安全解析 JSON 对象到 Map（用于 member_price 等键值对字段）
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> parseStringMap(String value) {
        if (StrUtil.isBlank(value)) {
            return Collections.emptyMap();
        }
        String trim = value.trim();
        if (!trim.startsWith("{")) {
            return Collections.emptyMap();
        }
        try {
            Map<String, String> map = JSONUtil.toBean(trim, Map.class);
            return map != null ? map : Collections.emptyMap();
        } catch (Exception ignored) {
            return Collections.emptyMap();
        }
    }

}
