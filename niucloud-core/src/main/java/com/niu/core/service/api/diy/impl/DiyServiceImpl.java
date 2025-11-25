package com.niu.core.service.api.diy.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONPath;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.diy.DiyPage;
import com.niu.core.entity.diy.DiyRoute;
import com.niu.core.entity.diy.DiyTheme;
import com.niu.core.enums.diy.PagesEnum;
import com.niu.core.enums.diy.TemplateEnum;
import com.niu.core.mapper.diy.DiyPageMapper;
import com.niu.core.mapper.diy.DiyRouteMapper;
import com.niu.core.mapper.diy.DiyThemeMapper;
import com.niu.core.service.admin.diy.param.TemplateParam;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.api.diy.IDiyService;
import com.niu.core.service.api.diy.param.DiyInfoParam;
import com.niu.core.service.api.diy.param.DiyShareParam;
import com.niu.core.service.api.diy.param.DiyTabbarListParam;
import com.niu.core.service.api.diy.param.DiyTabbarParam;
import com.niu.core.service.api.diy.vo.DiyInfoVo;
import com.niu.core.service.api.diy.vo.DiyShareVo;
import com.niu.core.service.api.diy.vo.DiyTabbarInfoVo;
import com.niu.core.service.api.diy.vo.DiyTabbarVo;
import com.niu.core.service.api.sys.ISysConfigService;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import com.niu.core.service.core.diy.ICoreDiyConfigService;
import com.niu.core.service.core.diy.ICoreDiyService;
import com.niu.core.service.core.diy.vo.BottomConfigVo;
import com.niu.core.service.core.diy.vo.StartUpPageConfigVo;
import com.niu.core.service.core.site.ICoreSiteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DiyServiceImpl implements IDiyService {

    @Autowired
    private DiyPageMapper diyPageMapper;

    @Autowired
    private DiyRouteMapper diyRouteMapper;

    @Resource
    private ICoreDiyConfigService coreDiyConfigService;

    @Resource
    private ICoreSiteService coreSiteService;

    @Resource
    private ICoreAddonService coreAddonService;

    @Resource
    private ICoreDiyService coreDiyService;

    @Resource
    private DiyThemeMapper diyThemeMapper;



    @Override
    public Object info(DiyInfoParam param) {
        Integer id = ObjectUtil.defaultIfNull(param.getId(), 0);
        String name = ObjectUtil.defaultIfNull(param.getName(), "");

        log.info("id: {}, name: {}", id, name);

        StartUpPageConfigVo startUpPage = null;
        JSONObject template = null;

        if (!StrUtil.isEmpty(name)) {
            // 查询启动页
            startUpPage = coreDiyConfigService.getStartUpPageConfig(RequestUtils.defaultSiteId(), name);
            log.info("startUpPage: {}", JSONUtil.toJsonStr(startUpPage));

            TemplateParam templateParam = new TemplateParam();
            String[] key = { name };
            templateParam.setKey(key);
            template = TemplateEnum.getTemplate(templateParam).getJSONObject(name);
        }

        if (id == 0 && startUpPage != null && template != null && !startUpPage.getPage().equals(template.getStr("page"))) {
            log.info("enter if, template: {}", template.toString());
            return startUpPage;
        } else {
            QueryWrapper<DiyPage> queryWrapper = new QueryWrapper();
            queryWrapper.eq("site_id", param.getSiteId());
            log.info("site_id: {}",param.getSiteId() );

            DiyPage info = null;

            log.info("id: {}, name: {}", id, name);
            if (id > 0) {
                queryWrapper.eq("id", id);
                info = diyPageMapper.selectOne(queryWrapper);
            } else if (!name.isEmpty()) {
                queryWrapper.eq("name", name);
                queryWrapper.eq("is_default", 1);
                queryWrapper.last("limit 1");
                info = diyPageMapper.selectOne(queryWrapper);
            }

            log.info("info: {}", JSONUtil.toJsonStr(info));

            if (info == null) {
                if (!name.isEmpty()) {
                    JSONObject pageData = getFirstPageData(name, "");
                    if (pageData != null) {
                        String mode = "diy";
                        String value = "";
                        info = JSONUtil.toBean(pageData, DiyPage.class);
                        mode = JSONPath.eval(pageData, "$.mode") + "";
                        value = JSONPath.eval(pageData, "$.data") + "";
                        info.setMode(mode);
                        info.setValue(value);
                        info.setIsDefault(1);
                        info.setId(param.getSiteId());
                    }
                }
            }

            log.info("info: {}", JSONUtil.toJsonStr(info));
            if (info == null)  return null;

            DiyInfoVo vo = new DiyInfoVo();
            BeanUtils.copyProperties(info, vo);
            log.info("vo: {}", JSONUtil.toJsonStr(vo));
            return vo;
        }
    }

    /**
     * 获取默认页面数据
     * @param type
     * @param addon
     * @return
     */
    public JSONObject getFirstPageData(String type, String addon) {
        JSONObject pages = PagesEnum.getPagesByAddon(type, addon);
        if (pages == null || pages.keySet().size() == 0) return null;


        String template = pages.keySet().iterator().next();
        JSONObject data = pages.getJSONObject(template);
        data.set("type", type);
        data.set("template", template);

        return data;
    }


    @Override
    public DiyTabbarVo tabbar(DiyTabbarParam param) {
        DiyTabbarVo diyTabbarVo = null;
        return diyTabbarVo;
    }

    @Override
    public List<BottomConfigVo> tabbarList(DiyTabbarListParam param) {
        List<BottomConfigVo> list = coreDiyConfigService.getSiteBottomList();
        SiteInfoVo siteInfoVo = coreSiteService.getSiteCache(param.getSiteId());

        List<Addon> apps = siteInfoVo.getApps();

        // 提取底部导航列表中的key
        List<String> bottomListKeys = list.stream()
                .map(BottomConfigVo::getKey)
                .collect(Collectors.toList());

        List<String> appKeys = apps.stream().map(Addon::getKey).collect(Collectors.toList());

        // 过滤掉没有底部导航的应用
        List<String> filteredAppKeys = appKeys.stream()
                .filter(bottomListKeys::contains)
                .collect(Collectors.toList());

        // 单应用，排除系统底部导航设置
        if (list.size() > 1 && filteredAppKeys.size() == 1) {
            list = list.stream()
                    .filter(item -> !"app".equals(item.getKey()))
                    .collect(Collectors.toList());
        }

        return list;
    }

    @Override
    public List<BottomConfigVo> getBottomList() {
        List<BottomConfigVo> list =  coreDiyConfigService.getBottomList();
        SiteInfoVo siteInfoVo = coreSiteService.getSiteCache(RequestUtils.siteId());

        List<Addon> apps = siteInfoVo.getApps();

        // 提取底部导航列表中的key
        List<String> bottomListKeys = list.stream()
                .map(BottomConfigVo::getKey)
                .collect(Collectors.toList());

        List<String> appKeys = apps.stream().map(Addon::getKey).collect(Collectors.toList());

        // 过滤掉没有底部导航的应用
        List<String> filteredAppKeys = appKeys.stream()
                .filter(bottomListKeys::contains)
                .collect(Collectors.toList());

        // 单应用，排除系统底部导航设置
        if (list.size() > 1 && filteredAppKeys.size() == 1) {
            list = list.stream()
                    .filter(item -> !"app".equals(item.getKey()))
                    .collect(Collectors.toList());
        }

        List<BottomConfigVo> tabbarList = new LinkedList<>();
        for (BottomConfigVo item : list) {
            BottomConfigVo config = coreDiyConfigService.getBottomConfig(RequestUtils.siteId(), item.getKey());
            tabbarList.add(config);
        }
        return tabbarList;
    }

    @Override
    public JSONObject share(DiyShareParam param) {
        QueryWrapper<DiyRoute> queryWrapper = new QueryWrapper();
        queryWrapper.select("id,title,name,page,share,is_share");
        queryWrapper.eq("page", param.getRoute());
        queryWrapper.eq("site_id", param.siteId());

        DiyRoute diyRoute = diyRouteMapper.selectOne(queryWrapper);
        if (diyRoute == null || diyRoute.getShare().isEmpty()) return new JSONObject();

        JSONObject share = JSONUtil.parseObj(diyRoute.getShare());
        share.set("route", diyRoute.getPage());
        share.set("query", "");

        JSONObject query = new JSONObject();

        if (param.getParams() != null && !param.getParams().isEmpty()) {
            query = JSONUtil.parseObj(param.getParams());
        }

        if (param.memberId() != null && param.memberId() > 0) {
            query.set("mid", param.memberId());
        }

        String queryStr = "";
        if (query.keySet().size() > 0) {
            for (String key: query.keySet()) {
                queryStr += queryStr.isEmpty() ? key + "=" + query.getStr(key) : "&" + key + "=" + query.getStr(key);
            }
        }
        share.set("url", share.getStr("route") + (queryStr.isEmpty() ? "" : "?" + queryStr));

        return share;
    }

    @Override
    public JSONObject getDiyTheme() {
        Map<String, InstallAddonListVo> siteAddon =  coreAddonService.getInstallAddonList();
        List<DiyTheme> themeDataList = diyThemeMapper.selectList(new QueryWrapper<DiyTheme>().eq("type", "app").eq("is_selected", 1));
        Map<String, DiyTheme> themeData = new HashMap<>();
        for (DiyTheme theme : themeDataList) {
            themeData.put(theme.getAddon(), theme);
        }
        JSONObject systemTheme = coreDiyService.getDefaultThemeColor("app");
        JSONObject appTheme = new JSONObject();
        JSONObject appThemeObj = new JSONObject();
        appThemeObj.set("addon_name", "app");
        appThemeObj.set("id", ObjectUtil.isNotEmpty(themeData.get("app")) ? themeData.get("app").getId() : "");
        appThemeObj.set("icon", "");
        appThemeObj.set("addon_title", "系统");
        appThemeObj.set("title", ObjectUtil.isNotEmpty(themeData.get("app")) ? themeData.get("app").getTitle() : (!systemTheme.isEmpty() ? systemTheme.getJSONArray("theme_color").getJSONObject(0).get("title") : ""));
        appThemeObj.set("theme", JSONUtil.parseObj(ObjectUtil.isNotEmpty(themeData.get("app")) ? themeData.get("app").getTheme() : (!systemTheme.isEmpty() ? systemTheme.getJSONArray("theme_color").getJSONObject(0).get("theme") : "{}")));
        appThemeObj.set("new_theme", JSONUtil.parseArray(ObjectUtil.isNotEmpty(themeData.get("app")) ? themeData.get("app").getNewTheme() : "[]"));
        appTheme.putOpt("app", appThemeObj);

        JSONObject data = new JSONObject();
        for (Map.Entry<String, InstallAddonListVo> entry : siteAddon.entrySet()) {
            InstallAddonListVo value = entry.getValue();
            JSONObject addonTheme = coreDiyService.getDefaultThemeColor(value.getKey());
            if (!addonTheme.isEmpty() && addonTheme.containsKey("theme_color")) {
                JSONObject addonData = new JSONObject();
                addonData.set("id", ObjectUtil.isNotEmpty(themeData.get(value.getKey())) ? themeData.get(value.getKey()).getId() : "");
                addonData.set("icon", value.getIcon() != null ? value.getIcon() : "");
                addonData.set("addon_title", value.getTitle() != null ? value.getTitle() : "");
                addonData.set("title", ObjectUtil.isNotEmpty(themeData.get(value.getKey())) ? themeData.get(value.getKey()).getTitle() : addonTheme.getJSONArray("theme_color").getJSONObject(0).get("title"));
                addonData.set("theme", JSONUtil.parseObj(ObjectUtil.isNotEmpty(themeData.get(value.getKey())) ? themeData.get(value.getKey()).getTheme() : addonTheme.getJSONArray("theme_color").getJSONObject(0).get("theme")));
                addonData.set("new_theme", JSONUtil.parseArray(ObjectUtil.isNotEmpty(themeData.get(value.getKey())) && ObjectUtil.isNotEmpty(themeData.get(value.getKey()).getNewTheme()) ? themeData.get(value.getKey()).getNewTheme() : "[]"));
                data.putOpt(value.getKey(), addonData);
            }
        }
        if (data.isEmpty() || siteAddon.size() > 1) {
            data.putAll(appTheme);
        }

        return data;
    }

}
