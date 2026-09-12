package com.niu.core.common.utils;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.json.JacksonUtils;
import com.niu.core.common.utils.json.JsonLoadUtils;
import com.niu.core.entity.sys.SysMenu;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * 树工具类
 */
public class TreeUtils {

    /**
     * JSONArray转树形结构（默认根节点 pid = 0，对齐 PHP list_to_tree）
     */
    public static JSONArray listToTree(JSONArray arr, String id, String pid, String child) {
        return listToTree(arr, id, pid, child, 0);
    }

    /**
     * JSONArray转树形结构
     *
     * @param arr   JSON数组
     * @param id    主键字段名
     * @param pid   上级字段名
     * @param child 子级字段名
     * @param root  根节点 pid 值，对应 PHP list_to_tree 的 $root 参数
     * @return JSONArray
     */
    public static JSONArray listToTree(JSONArray arr, String id, String pid, String child, Object root) {
        JSONArray jsonArray = new JSONArray();
        JSONObject hash = new JSONObject();
        String rootKey = normalizeTreeKey(root);
        for (Object o : arr) {
            JSONObject json = (JSONObject) o;
            hash.put(json.getStr(id), json);
        }
        for (Object o : arr) {
            JSONObject aVal = (JSONObject) o;
            String parentKey = normalizeTreeKey(resolveParentId(aVal.get(pid), root));
            if (rootKey.equals(parentKey)) {
                jsonArray.add(aVal);
            } else {
                JSONObject hashVP = (JSONObject) hash.get(parentKey);
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

    private static Object resolveParentId(Object parentIdObj, Object root) {
        if (parentIdObj == null || parentIdObj.toString().trim().isEmpty()) {
            return root;
        }
        return parentIdObj;
    }

    private static String normalizeTreeKey(Object key) {
        if (key == null) {
            return "0";
        }
        return key.toString().trim();
    }

    /**
     * @param jsonArray
     * @param parentKey
     * @param parentValue
     * @param currentKey
     * @param childrenKey
     * @return
     */
    public static List<cn.hutool.json.JSONObject> treeToList(cn.hutool.json.JSONArray jsonArray, String parentKey, String parentValue,
                                                             String currentKey, String childrenKey) {
        List<cn.hutool.json.JSONObject> jsonObjectList = new CopyOnWriteArrayList<>();
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
    public static List<cn.hutool.json.JSONObject> treeToList(List<cn.hutool.json.JSONObject> jsonObjectList, String parentKey, String parentValue,
                                                             String currentKey, String childrenKey, cn.hutool.json.JSONArray jsonArray) {
        if (jsonObjectList == null) {
            jsonObjectList = new CopyOnWriteArrayList<>();
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            cn.hutool.json.JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.get(parentKey) == null) jsonObject.put(parentKey, parentValue);
            String currentKeyValue = jsonObject.getStr(currentKey);
            cn.hutool.json.JSONArray childrenKeyArray = jsonObject.getJSONArray(childrenKey);
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
        cn.hutool.json.JSONArray jsonArray = JSONUtil.parseArray(jsonString);
        // System.out.println(jsonArray);
        List<cn.hutool.json.JSONObject> jsonObjectList = treeToList(jsonArray, "parent_key", "", "menu_key", "children");
        jsonObjectList.stream().forEach(json -> {
            System.out.println(json);
        });
        List<SysMenu> beanList = JacksonUtils.toSnakeCaseList(jsonObjectList, SysMenu.class);
        beanList.stream().forEach(b -> {
            // System.out.println(b);
        });
    }

}
