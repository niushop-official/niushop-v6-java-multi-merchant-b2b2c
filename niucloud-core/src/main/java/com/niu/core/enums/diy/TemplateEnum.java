package com.niu.core.enums.diy;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.entity.addon.Addon;
import com.niu.core.service.admin.diy.param.TemplateParam;
import com.niu.core.service.core.site.ICoreSiteService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TemplateEnum {

    private static ICoreSiteService coreSiteService() {
        return SpringContext.bean(ICoreSiteService.class);
    }

    /**
     * 获取微页面模板按插件分组
     *
     * @param siteId
     * @return
     */
    public static JSONObject getTemplateAddonGroup(Integer siteId){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();

        JSONObject template = new JSONObject();

        JSONObject systemTemplate = new JSONObject();
        systemTemplate.put("title", "系统");
        systemTemplate.put("key", "app");
        systemTemplate.put("list", jsonModuleLoader.getResultElement("core", "diy/template.json"));

        List<Addon> addons = coreSiteService().getSiteAddons(siteId);
        for (Addon addon : addons) {
            JSONObject addonTemplate = jsonModuleLoader.getResultElement(addon.getKey(), "diy/template.json");
            if (addonTemplate != null) {
                JSONObject templateItem = new JSONObject();
                templateItem.put("title", addon.getTitle());
                templateItem.put("key", addon.getKey());
                templateItem.put("list", addonTemplate);
                template.put(addon.getKey(), templateItem);
            }
        }

        return template;
    }

    /**
     * 获取全部微页面模板
     * @return
     */
    public static JSONObject getTemplate(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultElement(RequestUtils.siteId(), "diy/template.json");
    }

    /**
     * 根据筛选条件获取微页面模板（对齐 PHP TemplateDict：多条件叠加过滤）
     *
     * @param param
     * @return
     */
    public static JSONObject getTemplate(TemplateParam param){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONObject diyTemplate;

        if (param.getAddon() == null || param.getAddon().isEmpty()) {
            diyTemplate = jsonModuleLoader.mergeResultElement(RequestUtils.siteId(), "diy/template.json");
        } else {
            diyTemplate = jsonModuleLoader.getResultElement(param.getAddon(), "diy/template.json");
            JSONObject systemTemplate = jsonModuleLoader.getResultElement("core", "diy/template.json");
            List<JSONObject> templateList = new ArrayList<>();
            templateList.add(diyTemplate);
            templateList.add(systemTemplate);
            diyTemplate = jsonModuleLoader.mergeJsonForAttribute(templateList);
        }

        Map<String, JSONObject> pages = new LinkedHashMap<>();
        for (String key : diyTemplate.keySet()) {
            pages.put(key, diyTemplate.getJSONObject(key));
        }

        if (param.getAppType() != null && !param.getAppType().isEmpty()) {
            pages.entrySet().removeIf(entry -> {
                List<?> appTypes = ObjectUtil.defaultIfNull(
                        entry.getValue().getByPath("app_type", List.class), new ArrayList<>());
                return !appTypes.contains(param.getAppType());
            });
        }

        if (param.getKey() != null && param.getKey().length > 0) {
            Map<String, JSONObject> filtered = new LinkedHashMap<>();
            for (String key : param.getKey()) {
                if (pages.containsKey(key)) {
                    filtered.put(key, pages.get(key));
                }
            }
            pages = filtered;
        }

        if (param.getType() != null && !param.getType().isEmpty()) {
            pages.entrySet().removeIf(entry ->
                    !param.getType().equals(ObjectUtil.defaultIfNull(entry.getValue().getStr("type"), "")));
        }

        if (param.getPage() != null && !param.getPage().isEmpty()) {
            pages.entrySet().removeIf(entry ->
                    !param.getPage().equals(ObjectUtil.defaultIfNull(entry.getValue().getStr("page"), "")));
        }

        if (param.getAction() != null && !param.getAction().isEmpty()) {
            pages.entrySet().removeIf(entry ->
                    !param.getAction().equals(ObjectUtil.defaultIfNull(entry.getValue().getStr("action"), "")));
        }

        JSONObject template = new JSONObject();
        pages.forEach(template::put);
        return template;
    }
}
