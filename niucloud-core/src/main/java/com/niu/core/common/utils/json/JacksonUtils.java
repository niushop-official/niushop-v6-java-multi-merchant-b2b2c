package com.niu.core.common.utils.json;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONNull;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class JacksonUtils {

    /**
     * 将对象转换成小写下划线
     *
     * @param object
     * @return
     */
    public static String toSnakeCaseJSONString(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        String jsonString = null;
        try {
            jsonString = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    /**
     * 将JSON字符串<下划线>转换为对象<驼峰>
     *
     * @param jsonString
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T toSnakeCaseObject(String jsonString, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        // 配置下划线到驼峰命名的转换
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        // 禁用自动关闭流特性
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        T result = null;
        try {
            result = mapper.readValue(jsonString, clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 将JSON字符串<下划线>转换为对象<驼峰>
     *
     * @param jsonObjectList
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> toSnakeCaseList(List<cn.hutool.json.JSONObject> jsonObjectList, Class<T> clazz) {
        if (jsonObjectList == null || jsonObjectList.size() <= 0) {
            return new ArrayList<>();
        }
        List<T> beanList = new ArrayList<>();
        jsonObjectList.forEach(json -> {
            T target = JacksonUtils.toSnakeCaseObject(json.toString(), clazz);
            if (target != null) {
                beanList.add(target);
            } else {
                log.warn("Failed to deserialize JSON to {}: menu_key={}", clazz.getSimpleName(), json.getStr("menu_key"));
            }
        });
        return beanList;
    }

    public static void removeNull(JSONObject object) {
        object.keySet().forEach(key->{
            Object o = object.get(key);
            if (o instanceof JSONArray) {
                removeNull((JSONArray) o);
            }
            if (o instanceof JSONObject) {
                removeNull(object.getJSONObject(key));
            }
            if (o instanceof JSONNull) {
                object.put(key, "");
            }
        });
    }
    public static void removeNull(JSONArray array) {
        Iterator<Object> iterator = array.iterator();
        while (iterator.hasNext()) {
            Object o1 = iterator.next();
            if (o1 instanceof JSONObject) {
                removeNull((JSONObject) o1);
            }
            if(o1 instanceof JSONArray) {
                removeNull((JSONArray) o1);
            }
            if(o1 instanceof JSONNull) {
                iterator.remove();
            }
        }
    }

    /**
     * 将JSONObject中的所有驼峰命名key转换为下划线命名
     *
     * @param jsonObject 需要转换的JSONObject
     * @return 转换后的JSONObject
     */
    public static JSONObject convertCamelToUnderline(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        JSONObject result = new JSONObject();
        for (String key : jsonObject.keySet()) {
            // 驼峰key转下划线
            String underlineKey = StrUtil.toUnderlineCase(key);
            Object value = jsonObject.get(key);

            // 递归处理嵌套的JSONObject
            if (value instanceof JSONObject) {
                result.put(underlineKey, convertCamelToUnderline((JSONObject) value));
            }
            // 递归处理嵌套的JSONArray
            else if (value instanceof JSONArray) {
                result.put(underlineKey, convertArrayCamelToUnderline((JSONArray) value));
            }
            // 普通值：直接放入
            else {
                result.put(underlineKey, value);
            }
        }
        return result;
    }

    /**
     * 将JSONArray中的所有JSONObject的驼峰命名key转换为下划线命名
     *
     * @param jsonArray 需要转换的JSONArray
     * @return 转换后的JSONArray
     */
    public static JSONArray convertArrayCamelToUnderline(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        JSONArray result = new JSONArray();
        for (Object element : jsonArray) {
            // 元素是JSONObject：转换驼峰key为下划线
            if (element instanceof JSONObject) {
                result.add(convertCamelToUnderline((JSONObject) element));
            }
            // 元素是JSONArray：递归处理
            else if (element instanceof JSONArray) {
                result.add(convertArrayCamelToUnderline((JSONArray) element));
            }
            // 普通元素：直接放入
            else {
                result.add(element);
            }
        }
        return result;
    }
}
