package com.niu.core.common.utils.json;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 树工具类
 */
public class FastJson2TreeUtils {

    /**
     * JSONArray转树形结构
     *
     * @param arr   JSON数组
     * @param id    主键字段名
     * @param pid   上级字段名
     * @param child 子级字段名
     * @return JSONArray
     */
    public static JSONArray listToTree(JSONArray arr, String id, String pid, String child) {
        JSONArray jsonArray = new JSONArray();
        JSONObject hash = new JSONObject();
        // 顶级数组
        for (Object o : arr) {
            JSONObject json = (JSONObject) o;
            hash.put(json.getString(id), json);
        }
        // 遍历结果集
        for (Object o : arr) {
            JSONObject aVal = (JSONObject) o;
            Object parentIdObj = aVal.get(pid);
            String parentId = parentIdObj == null ? "" : parentIdObj.toString();
            if (parentId.isEmpty()) {
                jsonArray.add(aVal);
            } else {
                JSONObject hashVP = (JSONObject) hash.get(parentId);
                if (hashVP != null) {
                    if (hashVP.get(child) != null) {
                        JSONArray ch = (JSONArray) hashVP.get(child);
                        ch.add(aVal);
                        hashVP.put(child, ch);
                    } else {
                        JSONArray ch = new JSONArray();
                        ch.add(aVal);
                        hashVP.put(child, ch);
                    }
                }
            }
        }
        return jsonArray;
    }

    /**
     * @param jsonArray
     * @param parentKey
     * @param parentValue
     * @param currentKey
     * @param childrenKey
     * @return
     */
    public static List<JSONObject> treeToList(JSONArray jsonArray, String parentKey, String parentValue,
                                              String currentKey, String childrenKey) {
        List<JSONObject> jsonObjectList = new CopyOnWriteArrayList<>();
        treeToList(jsonObjectList, parentKey, parentValue, currentKey, childrenKey, jsonArray);
        return jsonObjectList;
    }

    /**
     * @param jsonObjectList
     * @param parentKey
     * @param parentValue
     * @param currentKey
     * @param childrenKey
     * @param jsonArray
     * @return
     */
    public static List<JSONObject> treeToList(List<JSONObject> jsonObjectList, String parentKey, String parentValue,
                                              String currentKey, String childrenKey, JSONArray jsonArray) {
        if (jsonObjectList == null) {
            jsonObjectList = new CopyOnWriteArrayList<>();
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            jsonObject.put(parentKey, parentValue);
            String currentKeyValue = jsonObject.getString(currentKey);
            JSONArray childrenKeyArray = jsonObject.getJSONArray(childrenKey);
            jsonObject.remove(childrenKey);
            jsonObjectList.add(jsonObject);
            if (childrenKeyArray != null && childrenKeyArray.size() > 0) {
                treeToList(jsonObjectList, parentKey, currentKeyValue, currentKey, childrenKey, childrenKeyArray);
            }
        }
        return jsonObjectList;
    }

    public static void main(String[] args) {
        String path = "D:\\MyData\\NiuCloud\\NiuShop.20240620\\niucloud-admin-java\\webroot\\addon\\shop\\menu\\";
        File file = new File(path + "site.json");
        String jsonString = JsonLoadUtils.loadJsonString(file);
        JSONArray jsonArray = JSONArray.parseArray(jsonString);
        // System.out.println(jsonArray);
        List<JSONObject> jsonObjectList = treeToList(jsonArray, "parent_key", "", "menu_key", "children");
        jsonObjectList.stream().forEach(json -> {
            System.out.println(json);
        });
//        List<SysMenu> beanList = JacksonUtils.toSnakeCaseList(jsonObjectList, SysMenu.class);
//        beanList.stream().forEach(b -> {
//            // System.out.println(b);
//        });
    }

}
