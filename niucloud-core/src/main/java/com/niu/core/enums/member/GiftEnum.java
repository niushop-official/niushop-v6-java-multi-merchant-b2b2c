package com.niu.core.enums.member;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.config.dataization.AddonModuleContext;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.site.ICoreSiteService;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 会员礼包
 */
@Slf4j
public class GiftEnum {

    public static JSONObject getType(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        
        // 获取站点插件模块列表（包含core和已安装的插件）
        List<String> addonModules = getSiteAddonModules(RequestUtils.siteId());
        
        // 存储所有加载的JSON对象
        Set<JSONObject> jsonContentSet = new HashSet<>();
        
        // 遍历每个模块，加载对应的gift.json文件
        for (String addonModule : addonModules) {
            String jsonString = AddonModuleContext.readResourceAsStreamToText(addonModule, "loader/member/gift.json");
            if (StrUtil.isNotEmpty(jsonString)) {
                jsonString = jsonString.trim();
                try {
                    if (jsonString.startsWith("{")) {
                        jsonContentSet.add(JSONUtil.parseObj(jsonString));
                    }
                } catch (Exception e) {
                    log.error(addonModule + "=>member/gift.json json context error.");
                    e.printStackTrace();
                }
            }
        }
        
        // 合并所有JSON对象
        JSONObject jsonObjectResult = new JSONObject();
        try {
            jsonObjectResult = jsonModuleLoader.mergeJsonForAttribute(jsonContentSet);
        } catch (Exception e) {
            log.error("JSON合并异常:" + e.getMessage());
            e.printStackTrace();
        }
        
        return jsonObjectResult;
    }
    
    /**
     * 获取站点插件模块列表
     * @param siteId 站点ID
     * @return 插件模块列表
     */
    private static List<String> getSiteAddonModules(Integer siteId) {
        List<String> addonModules = new LinkedList<>();
        addonModules.add("core");
        if (ObjectUtil.isNull(siteId) || siteId == 0) {
            addonModules.addAll(SpringContext.bean(ICoreAddonService.class).getInstallAddonList().keySet().stream().toList());
        } else {
            addonModules.addAll(SpringContext.bean(ICoreSiteService.class).getAddonKeysBySiteId(siteId));
        }
        return addonModules;
    }
}
