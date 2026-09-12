package com.niu.core.service.core.sys.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.enums.fab.FabEnum;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.ICoreSysFabService;
import com.niu.core.service.core.sys.vo.FabNavsVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


@Service
public class CoreSysFabServiceImpl implements ICoreSysFabService {

    @Resource
    ICoreConfigService coreConfigService;

    @Override
    public FabNavsVo getFabNavs() {
        return getFabConfig();
    }

    @Override
    public FabNavsVo getFabConfig() {
        FabNavsVo fabNavsVo = new FabNavsVo();

        JSONObject config = coreConfigService.getConfigValue(0, "FAB");
        if (ObjectUtil.isNotEmpty(config) && !config.isEmpty()) {
            fabNavsVo.setStyle(buildStyleInfo(config.getJSONObject("style")));
            fabNavsVo.setIsShow(config.getBool("is_show", false));
            fabNavsVo.setNavs(normalizeNavs(config.getJSONArray("navs")));
        } else {
            fabNavsVo.setStyle(buildStyleInfo(null));
            fabNavsVo.setIsShow(false);
            fabNavsVo.setNavs(normalizeNavs(FabEnum.getFab()));
        }
        return fabNavsVo;
    }

    public static FabNavsVo.StyleInfo buildStyleInfo(JSONObject styleJSON) {
        FabNavsVo.StyleInfo styleInfo = new FabNavsVo.StyleInfo();
        if (styleJSON != null && !styleJSON.isEmpty()) {
            styleInfo.setType(styleJSON.getInt("type", 1));
            styleInfo.setPosition(styleJSON.getStr("position", "right"));
            styleInfo.setBottom(styleJSON.getInt("bottom", 300));
            styleInfo.setIcon(styleJSON.getStr("icon", ""));
        }
        return styleInfo;
    }

    public static JSONArray normalizeNavs(JSONArray navs) {
        if (navs == null || navs.isEmpty()) {
            return new JSONArray();
        }
        JSONArray result = new JSONArray();
        for (int i = 0; i < navs.size(); i++) {
            JSONObject nav = navs.getJSONObject(i);
            if (nav == null) {
                continue;
            }
            JSONObject normalized = new JSONObject(nav);
            normalized.set("icon", normalized.getStr("icon", ""));
            normalized.set("text", normalized.getStr("text", ""));

            JSONObject url = normalized.getJSONObject("url");
            if (url == null) {
                url = new JSONObject();
            } else {
                url = new JSONObject(url);
            }
            url.set("title", url.get("title") != null ? url.getStr("title", "") : normalized.getStr("text", ""));
            String value = url.getStr("value", "");
            if (value.isEmpty()) {
                value = url.getStr("url", "");
            }
            url.set("value", value);
            normalized.set("url", url);
            result.add(normalized);
        }
        return result;
    }
}
