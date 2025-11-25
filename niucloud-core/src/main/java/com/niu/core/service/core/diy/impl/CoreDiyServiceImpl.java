package com.niu.core.service.core.diy.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.diy.DiyTheme;
import com.niu.core.enums.diy.ThemeColorEnum;
import com.niu.core.mapper.diy.DiyThemeMapper;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import com.niu.core.service.core.diy.ICoreDiyService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CoreDiyServiceImpl implements ICoreDiyService {

    @Resource
    ICoreAddonService coreAddonService;

    @Resource
    DiyThemeMapper diyThemeMapper;

    @Override
    public JSONArray getThemeColorDict() {
        return ThemeColorEnum.getThemeColor();
    }

    @Override
    public JSONObject getDefaultThemeColor(String addon) {
        return ThemeColorEnum.getAddonThemeColor(addon);
    }

    @Override
    public void initDefaultDiyTheme() {
        Integer siteId = RequestUtils.defaultSiteId();
        Map<String, InstallAddonListVo> addonList =  coreAddonService.getInstallAddonList();
        JSONArray themeColorList = ThemeColorEnum.getThemeColor();

        List<DiyTheme> diyThemeList = new ArrayList<>();
        for (Object value : themeColorList)
        {

            JSONObject themeColor = JSONUtil.parseObj(value);
            if(themeColor.getStr("theme_type").equals("app_theme"))
            {
                for (int i = 0; i < themeColor.getJSONArray("theme_color").size(); i++)
                {
                    JSONObject themeObj = JSONUtil.parseObj(themeColor.getJSONArray("theme_color").get(i));
                    DiyTheme appDiyTheme = new DiyTheme();
                    appDiyTheme.setType("app");
                    appDiyTheme.setAddon("app");
                    appDiyTheme.setSiteId(siteId);
                    appDiyTheme.setTitle(themeObj.getStr("title"));
                    appDiyTheme.setTheme(themeObj.getStr("theme"));
                    appDiyTheme.setDefaultTheme(themeObj.getStr("theme"));
                    appDiyTheme.setThemeType("default");
                    appDiyTheme.setIsSelected(i == 0 ? 1 : 0);
                    appDiyTheme.setCreateTime(System.currentTimeMillis() / 1000);
                    diyThemeList.add(appDiyTheme);
                }
                continue;

            }

            for (Map.Entry<String, InstallAddonListVo> entry : addonList.entrySet())
            {
                if(entry.getValue().getType().equals("app"))
                {
                    if(themeColor.getStr("theme_type").equals(entry.getValue().getKey() + "_theme"))
                    {
                        for (int i = 0; i < themeColor.getJSONArray("theme_color").size(); i++)
                        {
                            JSONObject themeObj = JSONUtil.parseObj(themeColor.getJSONArray("theme_color").get(i));
                            DiyTheme appDiyTheme = new DiyTheme();
                            appDiyTheme.setType("app");
                            appDiyTheme.setAddon(entry.getValue().getKey());
                            appDiyTheme.setSiteId(siteId);
                            appDiyTheme.setTitle(themeObj.getStr("title"));
                            appDiyTheme.setTheme(themeObj.getStr("theme"));
                            appDiyTheme.setDefaultTheme(themeObj.getStr("theme"));
                            appDiyTheme.setThemeType("default");
                            appDiyTheme.setIsSelected(i == 0 ? 1 : 0);
                            appDiyTheme.setCreateTime(System.currentTimeMillis() / 1000);
                            diyThemeList.add(appDiyTheme);
                        }
                        for (Map.Entry<String, InstallAddonListVo> entryAddon : addonList.entrySet())
                        {
                            if(entryAddon.getValue().getSupportApp().equals(entry.getValue().getKey()))
                            {
                                for (int i = 0; i < themeColor.getJSONArray("theme_color").size(); i++)
                                {
                                    JSONObject themeObj = JSONUtil.parseObj(themeColor.getJSONArray("theme_color").get(i));
                                    DiyTheme appDiyTheme = new DiyTheme();
                                    appDiyTheme.setType("addon");
                                    appDiyTheme.setAddon(entryAddon.getValue().getKey());
                                    appDiyTheme.setSiteId(siteId);
                                    appDiyTheme.setTitle(themeObj.getStr("title"));
                                    appDiyTheme.setTheme(themeObj.getStr("theme"));
                                    appDiyTheme.setDefaultTheme(themeObj.getStr("theme"));
                                    appDiyTheme.setThemeType("default");
                                    appDiyTheme.setIsSelected(i == 0 ? 1 : 0);
                                    appDiyTheme.setCreateTime(System.currentTimeMillis() / 1000);
                                    diyThemeList.add(appDiyTheme);
                                }
                            }
                        }

                    }
                }

            }
        }

        if(ObjectUtil.isNotEmpty(diyThemeList)){
            for (DiyTheme appDiyTheme : diyThemeList) {

                DiyTheme model = diyThemeMapper.selectOne(new QueryWrapper<DiyTheme>().eq("site_id", siteId).eq("addon", appDiyTheme.getAddon()).eq("title", appDiyTheme.getTitle()));
                if(ObjectUtil.isEmpty(model)){
                    diyThemeMapper.insert(appDiyTheme);
                }
            }
        }


    }
}
