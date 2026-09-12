package com.niu.core.enums.diy;

import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;

public class PagesEnum {


    public static JSONObject getPages(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultElement("diy/pages.json");
    }


    public static JSONObject getPagesByAddon(String type, String addon){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONObject pages;
        if (addon != null && !addon.isEmpty()) {
            pages = jsonModuleLoader.getResultElement(addon, "diy/pages.json");
        } else {
            pages = jsonModuleLoader.mergeResultElement(RequestUtils.siteId(), "diy/pages.json");
        }


        if (type != null && !type.isEmpty() && pages != null) {
            return pages.getJSONObject(type);
        }
        return pages;
    }



    /**
     * 从已合并的 pages.json 中按模板 key 取子集，避免重复 merge。
     */
    public static JSONObject getPagesFromMerged(JSONObject allPages, String type, String mode) {
        if (allPages == null || type == null || type.isEmpty()) {
            return null;
        }
        JSONObject pages = allPages.getJSONObject(type);
        if (pages == null) {
            return null;
        }
        return filterPagesByMode(pages, mode);

    }


    public static JSONObject getPagesMerged(Integer siteId) {
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultElement(siteId, "diy/pages.json");
    }


    private static JSONObject filterPagesByMode(JSONObject pages, String mode) {
        if (pages == null || mode == null || mode.isEmpty()) {
            return pages;
        }
        JSONObject modePages = new JSONObject();
        for (String key : pages.keySet()) {
            JSONObject page = pages.getJSONObject(key);
            if (page != null && mode.equals(page.getStr("mode", ""))) {
                modePages.put(key, page);
            }
        }
        return modePages;
    }


    /**
     * 获取模板页面列表（对齐 PHP PagesDict::getPages）
     */
    public static JSONObject getPages(String type, String mode) {
        JSONObject pages = getPagesMerged(RequestUtils.siteId());
        if (type != null && !type.isEmpty()) {
            if (pages != null && pages.containsKey(type)) {
                return filterPagesByMode(pages.getJSONObject(type), mode);
            }
            return new JSONObject();
        }
        return pages;
    }
}


