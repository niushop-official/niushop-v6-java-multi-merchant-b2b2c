package com.niu.core.common.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.map.MapUtil;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 参数转换工具：将形如 position[latitude]=37.73605 的扁平参数转为嵌套对象
 */
public class ParamHelper {

    /**
     * 将扁平 Map 转换为目标对象
     *
     * @param flatMap 原始参数 Map，key 为带 [] 的路径，value 为字符串
     * @param clazz   目标对象的 Class
     * @param <T>     目标类型
     * @return 转换后的对象
     */
    public static <T> T mapToBean(Map<String, String> flatMap, Class<T> clazz) {
        if (MapUtil.isEmpty(flatMap)) {
            try {
                return clazz.getDeclaredConstructor().newInstance(); // 返回空对象
            } catch (Exception e) {
                throw new RuntimeException("无法实例化目标类：" + clazz.getName(), e);
            }
        }
        Map<String, Object> nestedMap = buildNestedMap(flatMap);
        // is_all/goods_ids → isAll/goodsIds；忽略无法转换的字段避免抛错
        return BeanUtil.toBean(nestedMap, clazz, CopyOptions.create()
                .setIgnoreError(true)
                .setFieldNameEditor(cn.hutool.core.util.StrUtil::toCamelCase));
    }

    /**
     * 支持同名多值参数（如 goods_ids[]=1&goods_ids[]=2）
     */
    public static <T> T mapToBean(MultiValueMap<String, String> multiValueMap, Class<T> clazz) {
        return mapToBean(flattenMultiValueMap(multiValueMap), clazz);
    }

    /**
     * 将 MultiValueMap 展平为单值 Map；
     * PHP/axios 风格 goods_ids[]=1&goods_ids[]=2 → goods_ids[0]=1, goods_ids[1]=2
     */
    public static Map<String, String> flattenMultiValueMap(MultiValueMap<String, String> multiValueMap) {
        Map<String, String> flatMap = new LinkedHashMap<>();
        if (multiValueMap == null || multiValueMap.isEmpty()) {
            return flatMap;
        }
        for (Map.Entry<String, List<String>> entry : multiValueMap.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            if (key == null || values == null || values.isEmpty()) {
                continue;
            }
            if (key.endsWith("[]")) {
                String baseKey = key.substring(0, key.length() - 2);
                for (int i = 0; i < values.size(); i++) {
                    flatMap.put(baseKey + "[" + i + "]", values.get(i));
                }
                continue;
            }
            if (values.size() == 1) {
                flatMap.put(key, values.get(0));
            } else {
                for (int i = 0; i < values.size(); i++) {
                    flatMap.put(key + "[" + i + "]", values.get(i));
                }
            }
        }
        return flatMap;
    }

    /**
     * 构建嵌套 Map
     * 例如：{"position[latitude]":"37.73605", "position[longitude]":"112.56566"}
     * → {"position": {"latitude":"37.73605", "longitude":"112.56566"}}
     * {"goods_ids[0]":"1", "goods_ids[1]":"2"} → {"goods_ids": [1, 2]}
     * {"goods_ids[]":"49"} → {"goods_ids": ["49"]}
     */
    public static Map<String, Object> buildNestedMap(Map<String, String> flatMap) {
        Map<String, Object> nested = new LinkedHashMap<>(); // 保留顺序
        for (Map.Entry<String, String> entry : flatMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            // 跳过空值（可根据需要保留空字符串）
            if (value == null) continue;
            List<String> path = parsePath(key);
            if (path.isEmpty()) continue;
            setNestedValue(nested, path, value);
        }
        convertIndexedMapsToLists(nested);
        return nested;
    }

    /**
     * 解析路径：将 "position[latitude]" 拆分为 ["position", "latitude"]
     * 支持多层： "a[b][c]" → ["a","b","c"]
     * 支持下标数组： "goods_ids[0]" → ["goods_ids", "0"]（0 起，后续转为 List）
     */
    private static List<String> parsePath(String key) {
        List<String> segments = new ArrayList<>();
        int start = 0;
        while (start < key.length()) {
            int bracket = key.indexOf('[', start);
            if (bracket == -1) {
                if (start < key.length()) {
                    segments.add(key.substring(start));
                }
                break;
            }
            if (bracket > start) {
                segments.add(key.substring(start, bracket));
            }
            int end = key.indexOf(']', bracket);
            if (end == -1) {
                segments.add(key.substring(start));
                break;
            }
            segments.add(key.substring(bracket + 1, end));
            start = end + 1;
        }
        return segments;
    }

    /** 判断路径段是否为数组下标（从 0 开始，如 goods_ids[0] 中的 "0"） */
    private static boolean isArrayIndexSegment(String segment) {
        if (segment == null || segment.isEmpty()) {
            return false;
        }
        for (int i = 0; i < segment.length(); i++) {
            if (!Character.isDigit(segment.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private static void convertIndexedMapsToLists(Map<String, Object> map) {
        for (String key : new ArrayList<>(map.keySet())) {
            Object value = map.get(key);
            if (value instanceof Map<?, ?> childMap) {
                Map<String, Object> child = (Map<String, Object>) childMap;
                convertIndexedMapsToLists(child);
                if (isIndexedMap(child)) {
                    map.put(key, toIndexedList(child));
                }
            }
        }
    }

    private static boolean isIndexedMap(Map<String, Object> map) {
        if (map.isEmpty()) {
            return false;
        }
        for (String key : map.keySet()) {
            if (!isArrayIndexSegment(key)) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private static List<Object> toIndexedList(Map<String, Object> map) {
        List<Object> list = new ArrayList<>();
        map.entrySet().stream()
                .sorted((a, b) -> Integer.compare(Integer.parseInt(a.getKey()), Integer.parseInt(b.getKey())))
                .forEach(entry -> {
                    Object value = entry.getValue();
                    if (value instanceof Map<?, ?> childMap) {
                        Map<String, Object> child = (Map<String, Object>) childMap;
                        convertIndexedMapsToLists(child);
                        if (isIndexedMap(child)) {
                            value = toIndexedList(child);
                        }
                    }
                    list.add(value);
                });
        return list;
    }

    private static List<Integer> toIntegerList(Object value) {
        List<Integer> result = new ArrayList<>();
        if (value == null) {
            return result;
        }
        if (value instanceof List<?> list) {
            for (Object item : list) {
                addInteger(result, item);
            }
            return result;
        }
        if (value instanceof String str) {
            for (String part : str.split(",")) {
                addInteger(result, part);
            }
            return result;
        }
        addInteger(result, value);
        return result;
    }

    private static void addInteger(List<Integer> out, Object raw) {
        if (raw == null) {
            return;
        }
        if (raw instanceof Number number) {
            int v = number.intValue();
            if (v > 0) {
                out.add(v);
            }
            return;
        }
        String str = String.valueOf(raw).trim();
        if (str.isEmpty()) {
            return;
        }
        try {
            int v = Integer.parseInt(str);
            if (v > 0) {
                out.add(v);
            }
        } catch (NumberFormatException ignored) {
            // ignore invalid number
        }
    }

    /**
     * 递归设置嵌套值
     * 支持 goods_ids[0]、goods_ids[]（PHP/axios 空下标）
     */
    private static boolean isEmptyArraySegment(String segment) {
        return segment != null && segment.isEmpty();
    }

    @SuppressWarnings("unchecked")
    private static void setNestedValue(Map<String, Object> current, List<String> path, Object value) {
        if (path.isEmpty()) {
            return;
        }
        String segment = path.get(0);
        if (path.size() == 1) {
            current.put(segment, value);
            return;
        }
        String nextSegment = path.get(1);
        Object child = current.get(segment);
        // goods_ids[]=49 → 追加到 List
        if (isEmptyArraySegment(nextSegment)) {
            if (!(child instanceof List<?>)) {
                child = new ArrayList<Object>();
                current.put(segment, child);
            }
            List<Object> list = (List<Object>) child;
            if (path.size() == 2) {
                list.add(value);
                return;
            }
            // goods_ids[][name]=x：追加一个 Map 再继续嵌套
            Map<String, Object> item = new LinkedHashMap<>();
            list.add(item);
            setNestedValue(item, path.subList(2, path.size()), value);
            return;
        }
        if (isArrayIndexSegment(nextSegment)) {
            if (!(child instanceof List<?>)) {
                child = new ArrayList<Object>();
                current.put(segment, child);
            }
            setNestedListValue((List<Object>) child, path.subList(1, path.size()), value);
            return;
        }
        if (!(child instanceof Map)) {
            child = new LinkedHashMap<String, Object>();
            current.put(segment, child);
        }
        setNestedValue((Map<String, Object>) child, path.subList(1, path.size()), value);
    }

    @SuppressWarnings("unchecked")
    private static void setNestedListValue(List<Object> current, List<String> path, Object value) {
        if (path.isEmpty()) {
            return;
        }
        String segment = path.get(0);
        if (!isArrayIndexSegment(segment)) {
            return;
        }
        int index = Integer.parseInt(segment);
        while (current.size() <= index) {
            current.add(null);
        }
        if (path.size() == 1) {
            current.set(index, value);
            return;
        }
        String nextSegment = path.get(1);
        Object child = current.get(index);
        if (isArrayIndexSegment(nextSegment)) {
            if (!(child instanceof List<?>)) {
                child = new ArrayList<>();
                current.set(index, child);
            }
            setNestedListValue((List<Object>) child, path.subList(1, path.size()), value);
            return;
        }
        if (!(child instanceof Map)) {
            child = new LinkedHashMap<String, Object>();
            current.set(index, child);
        }
        setNestedValue((Map<String, Object>) child, path.subList(1, path.size()), value);
    }

    // 兼容旧注释：items[0][id]=1 会生成 List，首项为 {"id":"1"}
}