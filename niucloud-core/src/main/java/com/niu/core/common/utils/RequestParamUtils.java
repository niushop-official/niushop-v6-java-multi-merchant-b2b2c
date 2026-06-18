package com.niu.core.common.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.service.core.poster.param.GetPosterParam;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP 请求参数解析工具，支持 a[b][c] 嵌套格式
 */
public class RequestParamUtils {

    private RequestParamUtils() {
    }

    /**
     * 将 HttpServletRequest 的参数（支持 a[b][c] 格式）解析为嵌套 Map
     * 注意：每个参数只取第一个值（String[] -> String）
     */
    public static Map<String, Object> parseNestedParams(HttpServletRequest request) {
        Map<String, Object> root = new HashMap<>();
        Map<String, String[]> paramMap = request.getParameterMap();

        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            if (values == null || values.length == 0) {
                continue;
            }
            setNestedValue(root, key, values[0]);
        }
        return root;
    }

    /**
     * 将 HttpServletRequest 的参数（支持 a[b][c] 格式）解析为嵌套 JSONObject
     */
    public static JSONObject parseNestedParamsAsJson(HttpServletRequest request) {
        return new JSONObject(parseNestedParams(request));
    }

    @SuppressWarnings("unchecked")
    private static void setNestedValue(Map<String, Object> obj, String key, Object value) {
        if (!key.contains("[")) {
            obj.put(key, value);
            return;
        }

        String cleanKey = key.replace(']', '[');
        String[] parts = cleanKey.split("\\[");

        Map<String, Object> current = obj;
        for (int i = 0; i < parts.length; i++) {
            if (StrUtil.isEmpty(parts[i])) {
                continue;
            }

            if (i == parts.length - 1) {
                current.put(parts[i], value);
            } else {
                Object nextObj = current.get(parts[i]);
                Map<String, Object> next;
                if (nextObj instanceof Map) {
                    next = (Map<String, Object>) nextObj;
                } else {
                    next = new HashMap<>();
                    current.put(parts[i], next);
                }
                current = next;
            }
        }
    }

    /**
     * 从 HttpServletRequest 构建 GetPosterParam，支持 param[active][seckill_id] 等嵌套参数
     */
    @SuppressWarnings("unchecked")
    public static GetPosterParam buildGetPosterParam(HttpServletRequest request) {
        Map<String, Object> root = parseNestedParams(request);
        GetPosterParam param = new GetPosterParam();
        param.setId(parseInt(root.get("id"), 0));
        Object type = root.get("type");
        if (type != null) {
            param.setType(type.toString());
        }
        Object paramObj = root.get("param");
        if (paramObj instanceof Map) {
            param.setParam((Map<String, Object>) paramObj);
        }
        Object channel = root.get("channel");
        if (channel != null) {
            param.setChannel(channel.toString());
        }
        return param;
    }

    /**
     * 安全解析整数参数
     */
    public static Integer parseInt(Object value, Integer defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        String str = value.toString().trim();
        if (str.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
