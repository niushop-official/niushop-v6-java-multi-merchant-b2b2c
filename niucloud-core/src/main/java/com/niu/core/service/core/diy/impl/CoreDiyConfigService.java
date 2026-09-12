package com.niu.core.service.core.diy.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.diy.BottomConfigValue;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.service.core.diy.ICoreDiyConfigService;
import com.niu.core.service.core.diy.param.StartUpPageConfigParam;
import com.niu.core.service.core.diy.vo.BottomConfigVo;
import com.niu.core.service.core.diy.vo.StartUpPageConfigVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
public class CoreDiyConfigService implements ICoreDiyConfigService {

    @Resource
    AddonMapper addonMapper;

    @Resource
    ICoreConfigService coreConfigService;

    private final JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();

    private JSONArray getBottomJson() {
        return jsonModuleLoader.mergeContentToArray("diy/bottom.json");
    }

    private JSONArray getSiteBottomJson() {
        return jsonModuleLoader.mergeContentToArray("diy/bottom_site.json");
    }

    private Map<String, Addon> loadAddonMap(List<String> keys) {
        Map<String, Addon> addonMap = new HashMap<>();
        Addon appAddon = new Addon();
        appAddon.setTitle("系统");
        addonMap.put("app", appAddon);

        List<String> dbKeys = keys.stream().filter(key -> !"app".equals(key)).collect(Collectors.toList());
        if (!dbKeys.isEmpty()) {
            List<Addon> addons = addonMapper.selectList(new QueryWrapper<Addon>().in("`key`", dbKeys));
            for (Addon addon : addons) {
                addonMap.put(addon.getKey(), addon);
            }
        }
        return addonMap;
    }

    private List<BottomConfigVo> buildBottomListFromJson(JSONArray bottomJsonArray, Integer siteId) {
        List<String> keys = new ArrayList<>();
        for (Object bottomMap : bottomJsonArray) {
            keys.add(((JSONObject) bottomMap).getStr("key"));
        }
        Map<String, Addon> addonMap = loadAddonMap(keys);

        List<BottomConfigVo> list = new ArrayList<>();
        for (Object bottomMap : bottomJsonArray) {
            JSONObject jsonObject = (JSONObject) bottomMap;
            BottomConfigVo vo = new BottomConfigVo();
            String key = jsonObject.getStr("key");
            vo.setInfo(addonMap.getOrDefault(key, new Addon()));
            vo.setKey(key);
            JSONObject bottomValueJson = jsonObject.getJSONObject("value");
            BottomConfigValue bottomConfigValue = JSONUtil.toBean(bottomValueJson, BottomConfigValue.class);
            if (siteId != null && siteId > 0) {
                JSONArray bottomList = bottomConfigValue.getList();
                if (bottomList != null && !bottomList.isEmpty()) {
                    // 遍历所有导航项，修改每个链接的 url
                    for (int i = 0; i < bottomList.size(); i++) {
                        JSONObject item = bottomList.getJSONObject(i);
                        JSONObject link = item.getJSONObject("link");
                        if (link != null) {
                            String oldUrl = link.getStr("url");
                            if (oldUrl != null) {
                                // 如果已经有参数，则追加 &site_id=xxx，否则追加 ?site_id=xxx
                                String newUrl = oldUrl + (oldUrl.contains("?") ? "&" : "?") + "site_id=" + siteId;
                                link.set("url", newUrl);
                            }
                        }
                    }
                }
            }
            vo.setValue(bottomConfigValue);
            list.add(vo);
        }
        // 将 'app' 放到第一个位置（对齐 PHP 逻辑）
        list.sort((a, b) -> {
            if ("app".equals(a.getKey())) return -1;
            if ("app".equals(b.getKey())) return 1;
            return 0;
        });
        return list;
    }

    @Override
    public List<BottomConfigVo> getBottomList() {
        return buildBottomListFromJson(getBottomJson(), null);
    }

    @Override
    public List<BottomConfigVo> getSiteBottomList(Integer siteId) {
        return buildBottomListFromJson(getSiteBottomJson(), siteId);
    }

    @Override
    public BottomConfigVo getBottomConfig(Integer siteId, String key) {
        List<BottomConfigVo> result = getBottomConfigList(siteId, List.of(key));
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<BottomConfigVo> getBottomConfigList(Integer siteId, List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return new ArrayList<>();
        }
        List<BottomConfigVo> list = siteId == 0 ? getBottomList() : getSiteBottomList(siteId);
        Map<String, BottomConfigVo> configMap = list.stream()
                .filter(vo -> keys.contains(vo.getKey()))
                .collect(Collectors.toMap(BottomConfigVo::getKey, vo -> vo, (a, b) -> a));

        List<BottomConfigVo> result = new ArrayList<>();
        for (String key : keys) {
            BottomConfigVo config = configMap.get(key);
            if (config == null && !"app".equals(key)) {
                config = configMap.get("app");
            }
            if (config == null) {
                continue;
            }
            JSONObject jsonObject = coreConfigService.getConfigValue(siteId, "diy_bottom_" + key);
            if (ObjectUtil.isNotEmpty(jsonObject)) {
                BottomConfigValue bottomConfigValue = JSONUtil.toBean(jsonObject, BottomConfigValue.class);
                config.setValue(bottomConfigValue);
            }
            result.add(config);
        }
        if (siteId != null && siteId > 0) {
            for (BottomConfigVo config : result) {
                appendSiteIdToBottomLinks(config.getValue(), siteId);
            }
        }
        return result;
    }

    private void appendSiteIdToBottomLinks(BottomConfigValue value, Integer siteId) {
        if (value == null || value.getList() == null) {
            return;
        }
        JSONArray tabList = value.getList();
        for (int i = 0; i < tabList.size(); i++) {
            JSONObject item = tabList.getJSONObject(i);
            if (item == null) {
                continue;
            }
            JSONObject link = item.getJSONObject("link");
            if (link == null) {
                continue;
            }
            String url = link.getStr("url", "");
            if (StrUtil.isBlank(url) || url.startsWith("http://") || url.startsWith("https://")) {
                continue;
            }
            if (!url.startsWith("/") || url.contains("site_id=")) {
                continue;
            }
            link.set("url", url + (url.contains("?") ? "&" : "?") + "site_id=" + siteId);
        }
    }

    /**
     * 设置底部导航
     */
    @Override
    public void setBottomConfig(Integer siteId, BottomConfigValue bottomConfigValue, String key) {
        coreConfigService.setConfig(siteId, "diy_bottom_" + key, JSONUtil.parseObj(bottomConfigValue));
    }

    /**
     * 启动页配置
     */
    @Override
    public void setStartUpPageConfig(Integer siteId, StartUpPageConfigParam value, String name) {
        coreConfigService.setConfig(siteId, "START_UP_PAGE_" + name.toUpperCase(), JSONUtil.parseObj(value));
    }

    /**
     * 获取启动页配置
     */
    public StartUpPageConfigVo getStartUpPageConfig(Integer siteId, String name) {
        JSONObject config = coreConfigService.getConfigValue(siteId, "START_UP_PAGE_" + name.toUpperCase());
        if (config.isEmpty()) return null;
        return JSONUtil.toBean(config, StartUpPageConfigVo.class);
    }

    @Override
    public Map<String, Object> getSiteQrcodeConfig(Integer siteId, String key) {
        Map<String, Object> defaultConfig = new HashMap<>();
        defaultConfig.put("is_hidden_plat_index", 0);
        defaultConfig.put("is_show", 1);
        JSONObject info = coreConfigService.getConfigValue(siteId, "diy_site_qrcode_" + key);
        if (info == null || info.isEmpty()) {
            return defaultConfig;
        }
        return info;
    }


    @Override
    public void setSiteQrcodeConfig(Integer siteId, Map<String, Object> data, String type) {
        coreConfigService.setConfig(siteId, "diy_site_qrcode_" + type, JSONUtil.parseObj(data));
    }

}
