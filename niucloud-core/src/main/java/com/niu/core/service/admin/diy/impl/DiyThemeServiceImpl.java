package com.niu.core.service.admin.diy.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.diy.DiyTheme;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.diy.DiyThemeMapper;
import com.niu.core.service.admin.diy.IDiyThemeService;
import com.niu.core.service.admin.diy.param.DiyThemeColorParam;
import com.niu.core.service.admin.diy.param.DiyThemeParam;
import com.niu.core.service.admin.diy.param.DiyThemeSetParam;
import com.niu.core.service.admin.diy.param.DiyThemeTitleParam;
import com.niu.core.service.admin.diy.vo.DiyThemeInfoVo;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import com.niu.core.service.core.diy.ICoreDiyService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DiyThemeServiceImpl implements IDiyThemeService {

    @Resource
    ICoreAddonService coreAddonService;

    @Resource
    ICoreDiyService coreDiyService;

    @Resource
    DiyThemeMapper diyThemeMapper;

    @Resource
    AddonMapper addonMapper;

    @Resource
    Cached cached;

    @Override
    public JSONObject getDiyTheme() {
        Map<String, InstallAddonListVo> addonList = coreAddonService.getInstallAddonList();
        
        // 调试信息：检查插件列表
        System.out.println("=== DEBUG: getDiyTheme ===");
        System.out.println("addonList size: " + addonList.size());
        for (Map.Entry<String, InstallAddonListVo> entry : addonList.entrySet()) {
            System.out.println("addon: " + entry.getKey() + ", type: " + entry.getValue().getType() + ", title: " + entry.getValue().getTitle());
        }

        List<DiyTheme> themeDataList = diyThemeMapper.selectList(
                new QueryWrapper<DiyTheme>()
                        .eq("site_id", RequestUtils.siteId())
                        .eq("is_selected", 1)
        );
        

        Map<String, DiyTheme> themeData = new HashMap<>();
        for (DiyTheme theme : themeDataList) {
            themeData.put(theme.getAddon(), theme);
        }


        JSONObject systemTheme = coreDiyService.getDefaultThemeColor("app");
        

        System.out.println("systemTheme: " + systemTheme);

        JSONObject appTheme = new JSONObject();
        JSONObject appThemeObj = new JSONObject();

        String appId = themeData.containsKey("app") ?
                (themeData.get("app").getId() != null ? themeData.get("app").getId().toString() : "") : "";
        String appTitle = themeData.containsKey("app") ?
                (themeData.get("app").getTitle() != null ? themeData.get("app").getTitle() : "") : "";
        String appThemeValue = themeData.containsKey("app") ?
                (themeData.get("app").getTheme() != null ? themeData.get("app").getTheme() : "") : "";

        if (appTitle.isEmpty() && !systemTheme.isEmpty() && systemTheme.containsKey("theme_color") && systemTheme.getJSONArray("theme_color").size() > 0) {
            String title = systemTheme.getJSONArray("theme_color").getJSONObject(0).getStr("title");
            appTitle = title != null ? title : "";
        }
        if (appThemeValue.isEmpty() && !systemTheme.isEmpty() && systemTheme.containsKey("theme_color") && systemTheme.getJSONArray("theme_color").size() > 0) {
            String theme = systemTheme.getJSONArray("theme_color").getJSONObject(0).getStr("theme");
            appThemeValue = theme != null ? theme : "";
        }

        appThemeObj.set("id", appId);
        appThemeObj.set("icon", "");
        appThemeObj.set("addon_title", "系统");
        appThemeObj.set("title", appTitle);
        appThemeObj.set("theme", parseThemeToObject(appThemeValue));
        appTheme.put("app", appThemeObj);


        JSONObject data = new JSONObject();
        for (Map.Entry<String, InstallAddonListVo> entry : addonList.entrySet()) {
            String key = entry.getKey();
            InstallAddonListVo value = entry.getValue();
            JSONObject addonTheme = coreDiyService.getDefaultThemeColor(key);

            if (!addonTheme.isEmpty() && addonTheme.containsKey("theme_color")) {
                JSONObject addonData = new JSONObject();

                String addonId = themeData.containsKey(key) ?
                        (themeData.get(key).getId() != null ? themeData.get(key).getId().toString() : "") : "";
                String addonIcon = value.getIcon() != null ? value.getIcon() : "";
                String addonTitle = value.getTitle() != null ? value.getTitle() : "";
                String addonThemeTitle = themeData.containsKey(key) ?
                        (themeData.get(key).getTitle() != null ? themeData.get(key).getTitle() : "") : "";
                String addonThemeValue = themeData.containsKey(key) ?
                        (themeData.get(key).getTheme() != null ? themeData.get(key).getTheme() : "") : "";
                if (addonThemeTitle.isEmpty() && addonTheme.containsKey("theme_color") && addonTheme.getJSONArray("theme_color").size() > 0) {
                    String title = addonTheme.getJSONArray("theme_color").getJSONObject(0).getStr("title");
                    addonThemeTitle = title != null ? title : "";
                }
                if (addonThemeValue.isEmpty() && addonTheme.containsKey("theme_color") && addonTheme.getJSONArray("theme_color").size() > 0) {
                    String theme = addonTheme.getJSONArray("theme_color").getJSONObject(0).getStr("theme");
                    addonThemeValue = theme != null ? theme : "";
                }

                addonData.set("id", addonId);
                addonData.set("icon", addonIcon);
                addonData.set("addon_title", addonTitle);
                addonData.set("title", addonThemeTitle);
                addonData.set("theme", parseThemeToObject(addonThemeValue));
                data.put(key, addonData);
            }
        }

        List<InstallAddonListVo> appList = new ArrayList<>();
        for (InstallAddonListVo addon : addonList.values()) {
            appList.add(addon);
        }
        System.out.println("data.isEmpty(): " + data.isEmpty());
        System.out.println("appList.size(): " + appList.size());
        
        if (data.isEmpty() || appList.size() > 1) {
            // 使用putAll模拟PHP的array_merge功能
            JSONObject mergedData = new JSONObject();
            mergedData.putAll(appTheme);
            mergedData.putAll(data);
            System.out.println("returning mergedData: " + mergedData);
            return mergedData;
        }

        System.out.println("returning data: " + data);
        return data;
    }

    private String convertToString(Object obj) {
        if (obj == null) {
            return "";
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof JSONObject) {
            return ((JSONObject) obj).toString();
        }
        return obj.toString();
    }

    /**
     * 将主题字符串解析为JSON对象
     */
    private Object parseThemeToObject(String themeStr) {
        if (themeStr == null || themeStr.isEmpty()) {
            return new JSONObject();
        }
        
        try {
            // 尝试解析为JSON对象
            JSONObject parsed = JSONUtil.parseObj(themeStr);
            // 如果解析成功但对象为空，返回空对象
            if (parsed.isEmpty()) {
                return new JSONObject();
            }
            return parsed;
        } catch (Exception e) {
            // 如果解析失败，尝试直接返回字符串（可能是CSS变量格式）
            if (themeStr.contains("--") && themeStr.contains(":")) {
                // 可能是CSS变量格式，尝试手动解析
                return parseCSSVariables(themeStr);
            }
            // 其他情况返回空对象
            return new JSONObject();
        }
    }

    /**
     * 解析CSS变量格式的主题字符串
     */
    private JSONObject parseCSSVariables(String cssStr) {
        JSONObject result = new JSONObject();
        try {
            // 简单的CSS变量解析
            String[] lines = cssStr.split(";");
            for (String line : lines) {
                if (line.contains(":")) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        if (key.startsWith("--")) {
                            result.set(key, value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 解析失败返回空对象
        }
        return result;
    }

    @Override
    @Transactional
    public void setDiyTheme(DiyThemeSetParam data) {
        Integer siteId = RequestUtils.siteId();


        Long diyThemeCount = diyThemeMapper.selectCount(
                new QueryWrapper<DiyTheme>()
                        .eq("id", data.getId())
                        .eq("site_id", siteId)
        );
        if (diyThemeCount == 0) {
            throw new RuntimeException("DIY_THEME_COLOR_NOT_EXIST");
        }

        List<Addon> addonData = addonMapper.selectList(
                new QueryWrapper<Addon>()
                        .select("`key`")
                        .eq("support_app", data.getAddon())
        );

        List<DiyTheme> addonSaveData = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(addonData)) {
            for (Addon addon : addonData) {
                DiyTheme saveData = new DiyTheme();
                saveData.setSiteId(siteId);
                saveData.setType("addon");
                saveData.setAddon(addon.getKey());
                saveData.setTitle(data.getTitle());
                saveData.setTheme(convertToString(data.getTheme()));
                saveData.setNewTheme(convertToString(data.getNewTheme()));
                saveData.setIsSelected(1);
                saveData.setUpdateTime(System.currentTimeMillis() / 1000);
                addonSaveData.add(saveData);
            }
        }

        try {

            if (ObjectUtil.isNotEmpty(data.getId())) {

                diyThemeMapper.update(null,
                        new UpdateWrapper<DiyTheme>()
                                .eq("addon", data.getAddon())
                                .eq("is_selected", 1)
                                .eq("site_id", siteId)
                                .set("is_selected", 0)
                );


                DiyTheme updateData = new DiyTheme();
                updateData.setIsSelected(1);
                updateData.setUpdateTime(System.currentTimeMillis() / 1000);
                diyThemeMapper.update(updateData,
                        new UpdateWrapper<DiyTheme>()
                                .eq("id", data.getId())
                                .eq("site_id", siteId)
                );


                if (ObjectUtil.isNotEmpty(addonSaveData)) {
                    for (DiyTheme saveData : addonSaveData) {

                        diyThemeMapper.update(null,
                                new UpdateWrapper<DiyTheme>()
                                        .eq("addon", saveData.getAddon())
                                        .eq("is_selected", 1)
                                        .eq("site_id", siteId)
                                        .set("is_selected", 0)
                        );


                        diyThemeMapper.update(saveData,
                                new UpdateWrapper<DiyTheme>()
                                        .eq("addon", saveData.getAddon())
                                        .eq("title", data.getTitle())
                                        .eq("site_id", siteId)
                        );
                    }
                }
            }


        } catch (Exception e) {

            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    public List<DiyThemeInfoVo> getDefaultThemeColor(DiyThemeColorParam data) {
        String addon = data.getAddon();
       String cacheName = "diy_theme_color_" + addon;
        List<DiyTheme> themeList = diyThemeMapper.selectList(
                new QueryWrapper<DiyTheme>()
                        .select("id", "title", "addon", "default_theme", "theme", "new_theme", "theme_type")
                        .eq("site_id", RequestUtils.siteId())
                        .eq("addon", addon)
        );

        List<DiyThemeInfoVo> voList = new ArrayList<>();
        for (DiyTheme theme : themeList) {
            DiyThemeInfoVo vo = new DiyThemeInfoVo();
            vo.setId(theme.getId());
            vo.setTitle(theme.getTitle());
            vo.setAddon(theme.getAddon());
            vo.setThemeType(theme.getThemeType());


            String defaultTheme = theme.getDefaultTheme();
            vo.setDefaultTheme(defaultTheme != null && !defaultTheme.isEmpty() ? defaultTheme : "{}");

            String themeValue = theme.getTheme();
            vo.setTheme(themeValue != null && !themeValue.isEmpty() ? themeValue : "{}");

            String newTheme = theme.getNewTheme();
            vo.setNewTheme(newTheme != null && !newTheme.isEmpty() ? newTheme : "[]");

            voList.add(vo);
        }

        for (DiyThemeInfoVo vo : voList) {
            JSONObject addonTheme = coreDiyService.getDefaultThemeColor(vo.getAddon());
            if (!addonTheme.isEmpty() && addonTheme.containsKey("theme_field")) {
                vo.setThemeField(addonTheme.getJSONArray("theme_field"));
            }
        }

        return voList;
    }

    @Override
    @Transactional
    public void addDiyTheme(DiyThemeParam data) {
        Integer siteId = RequestUtils.defaultSiteId();
        String type = "";
        if (data.getAddon().equals("app")){
            type = "app";
        }else{
            Addon addonData = addonMapper.selectOne(new QueryWrapper<Addon>().eq("key", data.getAddon()).last("limit 1"));
            if(ObjectUtil.isEmpty(addonData)){
                throw new AdminException("插件不存在");
            }
            type = addonData.getType();
        }

//        List<Addon> addonData = addonMapper.selectList(new QueryWrapper<Addon>().eq("support_app", data.getAddon()));
//        List<DiyTheme> addonSaveData = new ArrayList<>();
//        if (!addonData.isEmpty()) {
//            for (Addon addon : addonData) {
//
//                DiyTheme diyTheme = new DiyTheme();
//                diyTheme.setSiteId(siteId);
//                diyTheme.setType("addon");
//                diyTheme.setAddon(addon.getKey());
//                diyTheme.setTitle(data.getTitle());
//                diyTheme.setTheme(data.getTheme() != null ? data.getTheme().toString() : "");
//                diyTheme.setNewTheme(data.getNewTheme() != null ? data.getNewTheme().toString() : "");
//                diyTheme.setDefaultTheme(data.getDefaultTheme() != null ? data.getDefaultTheme().toString() : "");
//                diyTheme.setThemeType("diy");
//                diyTheme.setCreateTime(System.currentTimeMillis() / 1000);
//                addonSaveData.add(diyTheme);
//            }
//        }

        DiyTheme model = new DiyTheme();
        model.setSiteId(siteId);
        model.setType(type);
        model.setAddon(data.getAddon());
        model.setTitle(data.getTitle());
        model.setDefaultTheme(data.getDefaultTheme() != null ? data.getDefaultTheme().toString() : "");
        model.setTheme(data.getTheme() != null ? data.getTheme().toString() : "");
        model.setNewTheme(data.getNewTheme() != null ? data.getNewTheme().toString() : "");
        model.setThemeType(ObjectUtil.defaultIfEmpty(data.getThemeType(), "diy"));
        model.setCreateTime(System.currentTimeMillis() / 1000);
        diyThemeMapper.insert(model);
    }

    @Override
    @Transactional
    public void editDiyTheme(Integer id, DiyThemeParam data) {

        DiyTheme diyThemeInfo = diyThemeMapper.selectById(id);
        if (ObjectUtil.isEmpty(diyThemeInfo)) {
            throw new RuntimeException("主题色不存在");
        }
//        List<DiyTheme> addonData = diyThemeMapper.selectList(new QueryWrapper<DiyTheme>().eq("type", "addon").eq("title", diyThemeInfo.getTitle()));
//        List<DiyTheme> addonSaveData = new ArrayList<>();
//        if (ObjectUtil.isNotEmpty(addonData)) {
//            for (DiyTheme diyTheme : addonData) {
//                DiyTheme saveData = new DiyTheme();
//                saveData.setId(diyTheme.getId());
//                saveData.setSiteId(diyTheme.getSiteId());
//                saveData.setTitle(diyTheme.getTitle());
//                saveData.setTheme(data.getTheme().toString());
//                saveData.setNewTheme(data.getNewTheme().toString());
//                saveData.setUpdateTime(System.currentTimeMillis() / 1000);
//
//                addonSaveData.add(saveData);
//            }
//        }
        diyThemeInfo.setAddon(data.getAddon());
        diyThemeInfo.setTitle(data.getTitle());
        diyThemeInfo.setDefaultTheme(data.getDefaultTheme() != null ? data.getDefaultTheme().toString() : "");
        diyThemeInfo.setTheme(data.getTheme() != null ? data.getTheme().toString() : "");
        diyThemeInfo.setNewTheme(data.getNewTheme() != null ? data.getNewTheme().toString() : "");
        diyThemeInfo.setUpdateTime(System.currentTimeMillis() / 1000);
        diyThemeInfo.setThemeType(ObjectUtil.defaultIfEmpty(data.getThemeType(), "diy"));
        diyThemeMapper.updateById(diyThemeInfo);

//        if (ObjectUtil.isNotEmpty(addonSaveData)) {
//            for (DiyTheme diyTheme : addonSaveData) {
//                diyThemeMapper.updateById(diyTheme);
//            }
//        }
    }

    @Override
    public void delDiyTheme(Integer id) {
        DiyTheme diyThemeInfo = diyThemeMapper.selectById(id);
        if (ObjectUtil.isEmpty(diyThemeInfo)) {
            throw new CommonException("主题色不存在");
        }
        if (diyThemeInfo.getThemeType().equals("default")) {
            throw new CommonException("默认主题色不能删除");
        }
        diyThemeMapper.deleteById(diyThemeInfo);
    }

    @Override
    public Boolean checkDiyThemeTitleUnique(DiyThemeTitleParam data) {
        QueryWrapper<DiyTheme> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title", data.getTitle());
        queryWrapper.eq("addon", data.getAddon());
        if (ObjectUtil.isNotEmpty(data.getId())) {
            queryWrapper.ne("id", data.getId());
        }
        return diyThemeMapper.selectCount(queryWrapper) == 0;
    }
}