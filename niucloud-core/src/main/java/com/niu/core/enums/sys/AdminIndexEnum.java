package com.niu.core.enums.sys;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.config.dataization.AddonModuleContext;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.site.ICoreSiteService;

import java.util.LinkedList;
import java.util.List;

/**
 * 平台首页加载事件
 */
public class AdminIndexEnum {

    private static final String DEFAULT_VIEW_PATH = "index/index";
    private static final String ADMIN_INDEX_FILE = "sys/admin_index.json";

    /**
     * 平台首页加载事件
     *
     * @return
     */
    public static JSONArray getAdminIndex() {
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultSet(RequestUtils.siteId(), ADMIN_INDEX_FILE);
    }

    /**
     * 获取平台主页 view_path，对齐 PHP ConfigService::getAdminIndexConfig
     * PHP 事件合并顺序为插件优先、core 在后，取第一个监听器的 view_path
     */
    public static String getAdminIndexConfig() {
        JSONArray indexList = mergeAdminIndexEventOrder(RequestUtils.siteId());
        if (indexList == null || indexList.isEmpty()) {
            return DEFAULT_VIEW_PATH;
        }
        JSONObject first = indexList.getJSONObject(0);
        String viewPath = first.getStr("view_path");
        return StrUtil.isEmpty(viewPath) ? DEFAULT_VIEW_PATH : viewPath;
    }

    private static JSONArray mergeAdminIndexEventOrder(Integer siteId) {
        JSONArray result = new JSONArray();
        List<String> addonModules = new LinkedList<>();
        if (ObjectUtil.isNull(siteId) || siteId == 0) {
            addonModules.addAll(SpringContext.bean(ICoreAddonService.class).getInstallAddonList().keySet().stream().toList());
        } else {
            addonModules.addAll(SpringContext.bean(ICoreSiteService.class).getAddonKeysBySiteId(siteId));
        }
        for (String addonModule : addonModules) {
            appendAdminIndex(result, addonModule);
        }
        appendAdminIndex(result, "core");
        return result;
    }

    private static void appendAdminIndex(JSONArray result, String addonModule) {
        String jsonString = AddonModuleContext.readResourceAsStreamToText(addonModule, "loader/" + ADMIN_INDEX_FILE);
        if (StrUtil.isEmpty(jsonString)) {
            return;
        }
        JSONArray jsonArray = JSONUtil.parseArray(jsonString.trim());
        jsonArray.forEach(result::add);
    }
}
