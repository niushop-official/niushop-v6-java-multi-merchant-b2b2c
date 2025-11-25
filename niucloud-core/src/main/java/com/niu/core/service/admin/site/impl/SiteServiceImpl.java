package com.niu.core.service.admin.site.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.google.gson.JsonArray;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.TreeUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.json.JacksonUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.entity.sys.SysMenu;
import com.niu.core.entity.sys.SysUserRole;
import com.niu.core.enums.addon.AddonTypeEnum;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.enums.sys.AppTypeEnum;
import com.niu.core.enums.sys.CacheTagEnum;
import com.niu.core.event.site.SiteAddAfterEvent;
import com.niu.core.event.site.SiteEditAfterEvent;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.site.SiteGroupMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.mapper.sys.SysMenuMapper;
import com.niu.core.mapper.sys.SysUserRoleMapper;
import com.niu.core.service.admin.auth.IAuthService;
import com.niu.core.service.admin.site.ISiteGroupService;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.param.*;
import com.niu.core.service.admin.site.vo.*;
import com.niu.core.service.admin.sys.ISysMenuService;
import com.niu.core.service.admin.sys.ISysRoleService;
import com.niu.core.service.admin.sys.ISysUserRoleService;
import com.niu.core.service.admin.sys.ISysUserService;
import com.niu.core.service.admin.sys.param.SysUserRoleParam;
import com.niu.core.service.admin.sys.vo.SysUserRoleInfoVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.site.ICoreSiteService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;


/**
 * 站点实现
 */
@Service
public class SiteServiceImpl implements ISiteService {

    /**
     * 站点tag
     */
    public static String cacheTagName = CacheTagEnum.SITE_CACHE.getTagName();

    private static final String[] LOGOS = {
            "/static/resource/images/site/back01.jpg",
            "/static/resource/images/site/back02.jpg",
            "/static/resource/images/site/back03.jpg",
            "/static/resource/images/site/back04.jpg",
    };

    /**
     * 是否使用缓存
     */
    public static final boolean useCache = true;

    @Resource
    SiteMapper siteMapper;

    @Resource
    AddonMapper addonMapper;

    @Resource
    SysUserRoleMapper sysUserRoleMapper;

    @Resource
    SiteGroupMapper siteGroupMapper;

    @Resource
    SysMenuMapper sysMenuMapper;

    @Resource
    ICoreSiteService iCoreSiteService;

    @Resource
    ISysMenuService sysMenuService;

    @Resource
    ISiteGroupService siteGroupService;

    @Resource
    ISysUserService sysUserService;

    @Resource
    ISysUserRoleService userRoleService;

    @Resource
    ISysRoleService sysRoleService;

    @Resource
    @Lazy
    IAuthService authService;

    @Resource
    ICoreSiteService coreSiteService;

    /**
     * 站点列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SiteListVo>
     */
    @Override
    public PageResult<SiteListVo> list(PageParam pageParam, SiteSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<Site> queryWrapper = new QueryWrapper<>();
        //查询条件判断组装
        if (ObjectUtil.isNotEmpty(searchParam.getKeywords())) {
            queryWrapper.like("site_name", searchParam.getKeywords()).or().like("keywords", searchParam.getKeywords());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) {
            queryWrapper.eq("status", searchParam.getStatus());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getGroupId())) {
            queryWrapper.eq("group_id", searchParam.getGroupId());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getApp())) {
            queryWrapper.like("app", searchParam.getApp());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getSiteDomain())) {
            queryWrapper.like("site_domain", searchParam.getSiteDomain());
        }
        queryWrapper.ne("app_type", "admin");
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {

            String[] createTime = searchParam.getCreateTime();
            Long startTime = (createTime[0] == null) ? 0 : DateUtils.StringToTimestamp(createTime[0]);
            System.out.println(startTime);
            Long endTime = (createTime[1] == null) ? 0 : DateUtils.StringToTimestamp(createTime[1]);
            if (startTime > 0 && endTime > 0) {
                queryWrapper.between("create_time", startTime, endTime);
            } else if (startTime > 0 && endTime == 0) {
                queryWrapper.ge("create_time", startTime);
            } else if (startTime == 0 && endTime > 0) {
                queryWrapper.le("create_time", startTime);
            }
        }

        if (ObjectUtil.isNotEmpty(searchParam.getExpireTime())) {

            String[] expireTime = searchParam.getExpireTime();
            Long startTime = (expireTime[0] == null) ? 0 : DateUtils.StringToTimestamp(expireTime[0]);
            System.out.println(startTime);
            Long endTime = (expireTime[1] == null) ? 0 : DateUtils.StringToTimestamp(expireTime[1]);
            if (startTime > 0 && endTime > 0) {
                queryWrapper.between("expire_time", startTime, endTime);
            } else if (startTime > 0 && endTime == 0) {
                queryWrapper.ge("expire_time", startTime);
            } else if (startTime == 0 && endTime > 0) {
                queryWrapper.le("expire_time", startTime);
            }
        }


        queryWrapper.orderByDesc("create_time");

        IPage<Site> iPage = siteMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<SiteListVo> list = new LinkedList<>();
        for (Site item : iPage.getRecords()) {
            SiteListVo vo = new SiteListVo();
            BeanUtils.copyProperties(item, vo);
            MPJQueryWrapper<SysUserRole> userRoleMPJQueryWrapper = new MPJQueryWrapper<>();
            userRoleMPJQueryWrapper.select("nsu.uid, nsu.username, nsu.head_img, nsu.real_name, nsu.last_ip, nsu.last_time, nsu.create_time, nsu.login_count")
                    .setAlias("nsur")
                    .leftJoin("?_sys_user nsu ON nsur.uid = nsu.uid".replace("?_", GlobalConfig.tablePrefix));
            userRoleMPJQueryWrapper.eq("nsur.is_admin", 1);
            userRoleMPJQueryWrapper.eq("nsur.site_id", item.getSiteId());
            vo.setAdmin(sysUserRoleMapper.selectJoinOne(SiteAdminVo.class, userRoleMPJQueryWrapper));
            SiteGroup siteGroup = siteGroupMapper.selectById(item.getGroupId());
            vo.setGroupName(siteGroup.getGroupName());
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 站点详情
     *
     * @param id 主键
     * @return SiteInfoVo
     */
    @Override
    public SiteInfoVo info(Integer id) {
        SiteInfoVo siteInfoVo = iCoreSiteService.getSiteCache(id);
        return siteInfoVo;
    }

    /**
     * 站点添加
     *
     * @param addParam 参数
     */
    @Transactional
    @Override
    public void add(SiteAddParam addParam) {
        SiteGroup siteGroup = siteGroupService.info(addParam.getGroupId());
        if (ObjectUtil.isNull(siteGroup) || ObjectUtil.isEmpty(siteGroup)) {
            throw new CommonException("SITE_GROUP_NOT_EXIST");
        }
        Site model = new Site();
        model.setSiteName(addParam.getSiteName());
        model.setGroupId(addParam.getGroupId());
        model.setAppType(AppTypeEnum.SITE.getName());
        model.setCreateTime(DateUtils.currTime());
        model.setExpireTime(DateUtils.StringToTimestamp(addParam.getExpireTime()));
        model.setApp(siteGroup.getApp());
        model.setAddons("");
        model.setSiteDomain(addParam.getSiteDomain());
        siteMapper.insert(model);
        Integer siteId = model.getSiteId();
        if (ObjectUtil.isNull(addParam.getUid()) || addParam.getUid() == 0) {
            //添加用户
            SiteUserParam siteUserParam = new SiteUserParam();
            siteUserParam.setUsername(addParam.getUsername());
            siteUserParam.setHeadImg("");
            siteUserParam.setStatus(1);
            siteUserParam.setRealName(addParam.getRealName());
            siteUserParam.setPassword(addParam.getPassword());
            siteUserParam.setIsAdmin(1);
            sysUserService.addSiteUser(siteUserParam, siteId);
        } else {
            SysUserRoleParam sysUserRoleParam = new SysUserRoleParam();
            sysUserRoleParam.setUid(addParam.getUid());
            sysUserRoleParam.setRoleIds(new JsonArray().toString());
            sysUserRoleParam.setIsAdmin(1);
            sysUserRoleParam.setSiteId(siteId);
            userRoleService.add(sysUserRoleParam);
        }

        SiteAddAfterEvent event = new SiteAddAfterEvent();
        event.setSiteId(siteId);
        event.addAppSign("core");
        event.setName("SiteAddAfterEvent");
        event.setSite(model);
        event.setSiteGroup(siteGroup);
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    /**
     * 站点编辑
     *
     * @param editParam 参数
     */
    @Transactional
    @Override
    public void edit(Integer id, SiteEditParam editParam) {
        Site model = siteMapper.selectOne(
                new QueryWrapper<Site>()
                        .eq("site_id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        if (ObjectUtil.isNotNull(editParam.getGroupId()) && ObjectUtil.isNotEmpty(editParam.getGroupId()) && editParam.getGroupId() != model.getGroupId()) {
            model.setGroupId(editParam.getGroupId());
        }
        if (ObjectUtil.isNotNull(editParam.getSiteName()) && ObjectUtil.isNotEmpty(editParam.getSiteName())) {
            model.setSiteName(editParam.getSiteName());
        }

        SiteGroup oldSiteGroup = siteGroupService.info(model.getGroupId());

        JSONArray initallJsonArray = new JSONArray();
        if (ObjectUtil.isNull(model.getInitalledAddon()) || ObjectUtil.isEmpty(model.getInitalledAddon())) {
            initallJsonArray.addAll(JSONUtil.parseArray(oldSiteGroup.getApp()));
            initallJsonArray.addAll(JSONUtil.parseArray(oldSiteGroup.getAddon()));
        }

        SiteGroup siteGroup = siteGroupService.info(editParam.getGroupId());
        if (ObjectUtil.isNull(siteGroup) || ObjectUtil.isEmpty(siteGroup)) {
            throw new CommonException("SITE_GROUP_NOT_EXIST");
        }
        initallJsonArray.addAll(JSONUtil.parseArray(siteGroup.getApp()));
        initallJsonArray.addAll(JSONUtil.parseArray(siteGroup.getAddon()));

        model.setInitalledAddon(JSONUtil.toJsonStr(initallJsonArray));
        model.setSiteDomain(editParam.getSiteDomain());
        model.setExpireTime(new DateTime(editParam.getExpireTime()).getTime() / 1000);
        model.setStatus(model.getExpireTime() > DateUtils.currTime() ? SiteStatusEnum.ON.getCode() : SiteStatusEnum.EXPIRE.getCode());
        siteMapper.updateById(model);

        coreSiteService.clearSiteCache(id);

        SiteEditAfterEvent event = new SiteEditAfterEvent();
        event.setSiteId(model.getSiteId());
        event.addAppSign("core");
        event.setName("SiteEditAfterEvent");
        event.setSite(model);
        event.setSiteGroup(siteGroup);
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    /**
     * 站点删除
     *
     * @param id 主键ID
     */
    @Transactional
    @Override
    public void del(Integer id) {
        Site model = siteMapper.selectOne(
                new QueryWrapper<Site>()
                        .eq("site_id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setStatus(SiteStatusEnum.DELETE.getCode());
        coreSiteService.clearSiteCache(id);
        siteMapper.updateById(model);
    }

    @Override
    public void closeSite(Integer siteId) {
        Site model = new Site();
        model.setSiteId(siteId);
        model.setStatus(SiteStatusEnum.CLOSE.getCode());
        siteMapper.updateById(model);
        coreSiteService.clearSiteCache(siteId);
    }

    @Override
    public void openSite(Integer siteId) {
        Site model = new Site();
        model.setSiteId(siteId);
        model.setStatus(SiteStatusEnum.ON.getCode());
        siteMapper.updateById(model);
        coreSiteService.clearSiteCache(siteId);
    }

    /**
     * 获取站点的菜单列表
     *
     * @param siteId
     * @param status
     * @return
     */
    @Override
    public Map<String, List<String>> getSiteApiList(Integer siteId, Integer status) {
        SiteInfoVo siteInfoVo = this.info(siteId);
        Map<String, List<String>> siteApi = new HashMap<>();
        if (ObjectUtil.isNull(siteInfoVo)) {
            return siteApi;
        }
        String appType = siteInfoVo.getAppType();
        if (appType.equals(AppTypeEnum.ADMIN.getName())) {
            return sysMenuService.getAllApiList(appType, status);
        } else {
            List<String> addonKeys = iCoreSiteService.getAddonKeysBySiteId(siteId);
            return sysMenuService.getApiListBySystem(appType, addonKeys);
        }
    }

    /**
     * 通过条件检索 站点数量
     *
     * @param siteSearchParam
     * @return
     */
    @Override
    public Integer getSiteCountByCondition(SiteSearchParam siteSearchParam) {
        QueryWrapper<Site> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(siteSearchParam.getCreateTime())) {

            String[] createTime = siteSearchParam.getCreateTime();
            Long startTime = (createTime[0] == null) ? 0 : DateUtils.StringToTimestamp(createTime[0]);
            Long endTime = (createTime[1] == null) ? 0 : DateUtils.StringToTimestamp(createTime[1]);
            if (startTime > 0 && endTime > 0) {
                queryWrapper.between("create_time", startTime, endTime);
            } else if (startTime > 0 && endTime == 0) {
                queryWrapper.ge("create_time", startTime);
            } else if (startTime == 0 && endTime > 0) {
                queryWrapper.le("create_time", startTime);
            }
        }

        if (ObjectUtil.isNotEmpty(siteSearchParam.getStatus())) {
            queryWrapper.eq("status", siteSearchParam.getStatus());
        }

        if (ObjectUtil.isNotEmpty(siteSearchParam.getGroupId())) {
            queryWrapper.eq("group_id", siteSearchParam.getGroupId());
        }

        if (ObjectUtil.isNotEmpty(siteSearchParam.getAppType())) {
            queryWrapper.eq("app_type", siteSearchParam.getAppType());
        }

        if (ObjectUtil.isNotEmpty(siteSearchParam.getExpireTime())) {

            String[] expireTime = siteSearchParam.getExpireTime();
            Long startTime = (expireTime[0] == null) ? 0 : DateUtils.StringToTimestamp(expireTime[0]);
            Long endTime = (expireTime[1] == null) ? 0 : DateUtils.StringToTimestamp(expireTime[1]);
            if (startTime > 0 && endTime > 0) {
                queryWrapper.between("expire_time", startTime, endTime);
            } else if (startTime > 0 && endTime == 0) {
                queryWrapper.ge("expire_time", startTime);
            } else if (startTime == 0 && endTime > 0) {
                queryWrapper.le("expire_time", startTime);
            }
        }
        Long siteCount = siteMapper.selectCount(queryWrapper);
        return siteCount.intValue();
    }

    /**
     * 获取站点的插件
     *
     * @return
     */
    public List<Addon> getSiteAddons() {
        return addonMapper.selectList(new QueryWrapper<Addon>()
                .eq("type", AddonTypeEnum.ADDON.getType())
                .eq("status", 1)
                .in("`key`", coreSiteService.getAddonKeysBySiteId(RequestUtils.siteId())));
    }

    /**
     * 站点变更
     *
     * @param site
     * @param siteGroup
     */
    @Override
    @Async
    public void siteAddonChange(Site site, SiteGroup siteGroup) {
        SiteEditAfterEvent event = new SiteEditAfterEvent();
        event.setSiteId(site.getSiteId());
        event.addAppSign("core");
        event.setName("SiteEditAfterEvent");
        event.setSite(site);
        event.setSiteGroup(siteGroup);
        coreSiteService.clearSiteCache(site.getSiteId());
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    @Override
    public Map<String, ShowAppListVo> getSiteAppTools(SiteAppToolsParam param) {
        List<String> siteAddonKeys = coreSiteService.getAddonKeysBySiteId(RequestUtils.siteId());

        QueryWrapper<Addon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", "addon")
                .eq("status", 1)
                .in("`key`", siteAddonKeys)
                .select("title", "icon", "`key`", "`desc`", "status", "type", "support_app");
        
        if (param != null && ObjectUtil.isNotEmpty(param.getTitle())) {
            queryWrapper.like("title", param.getTitle());
        }
        
        List<Addon> addons = addonMapper.selectList(queryWrapper);
        
        return getShowAppToolsWithAddons(addons, "site");
    }


    @Override
    public Map<String, ShowAppListVo> getAdminAppTools(SiteAppToolsParam param) {
        QueryWrapper<Addon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", "addon")
                .eq("status", 1)
                .select("title", "icon", "`key`", "`desc`", "status", "type", "support_app");

        if (param != null && ObjectUtil.isNotEmpty(param.getTitle())) {
            queryWrapper.like("title", param.getTitle());
        }
        
        List<Addon> addons = addonMapper.selectList(queryWrapper);
        return getShowAppToolsWithAddons(addons, "admin");
    }

    private Map<String, ShowAppListVo> getShowAppToolsWithAddons(List<Addon> addons, String appType) {
        Map<String, ShowAppListVo> map = new HashMap<>();
        ShowAppListVo addonVo = new ShowAppListVo();
        addonVo.setTitle("运营工具");
        addonVo.setList(new ArrayList<>());
        map.put("addon", addonVo);
        ShowAppListVo toolsVo = new ShowAppListVo();
        toolsVo.setTitle("系统工具");
        toolsVo.setList(new ArrayList<>());
        map.put("tool", toolsVo);
        
        List<String> filterKeys = new ArrayList<>();
        filterKeys.add("b2b2c_dada");
        if ("site".equals(appType)) {
            filterKeys.add("points_mall");
        }
        
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONObject appConfig = new JSONObject();
        if (appType.equals("admin")) {
            appConfig = jsonModuleLoader.mergeResultElement("sys/show_admin_app.json");
        } else {
            appConfig = jsonModuleLoader.mergeResultElement("sys/show_site_app.json");
        }


        List<String> appKeys = new ArrayList<>(filterKeys);
        if (ObjectUtil.isNotEmpty(appConfig)) {
            for (String key : appConfig.keySet()) {
                JSONArray jsonArray = appConfig.getJSONArray(key);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String appKey = jsonObject.getStr("key");
                    
                    // 过滤特定插件
                    if (filterKeys.contains(appKey)) {
                        continue;
                    }
                    
                    ShowAppListVo.AppInfo appInfo = new ShowAppListVo.AppInfo();
                    appInfo.setTitle(jsonObject.getStr("title"));
                    appInfo.setKey(appKey);
                    appInfo.setDesc(jsonObject.getStr("desc"));
                    appInfo.setIcon(jsonObject.getStr("icon"));
                    appInfo.setUrl(jsonObject.getStr("url"));
                    
                    if (key.equals("addon")) {
                        addonVo.getList().add(appInfo);
                    } else if (key.equals("tool")) {
                        toolsVo.getList().add(appInfo);
                    }
                    appKeys.add(appKey);
                }
            }
        }
        
        if (ObjectUtil.isNotEmpty(addons)) {
            // 查询插件的菜单URL
            List<String> addonKeyList = addons.stream()
                    .map(Addon::getKey)
                    .filter(key -> !appKeys.contains(key))
                    .toList();
            
            Map<String, String> addonUrlMap = new HashMap<>();
            if (!addonKeyList.isEmpty()) {
                QueryWrapper<SysMenu> menuQueryWrapper = new QueryWrapper<>();
                menuQueryWrapper.select("addon", "router_path")
                        .in("addon", addonKeyList)
                        .eq("is_show", 1)
                        .eq("menu_type", 1)
                        .eq("app_type", appType)
                        .orderByAsc("id")
                        .groupBy("addon");
                
                List<SysMenu> menus = sysMenuMapper.selectList(menuQueryWrapper);
                for (SysMenu menu : menus) {
                    addonUrlMap.put(menu.getAddon(), menu.getRouterPath());
                }
            }
            
            for (Addon addon : addons) {
                if (!appKeys.contains(addon.getKey())) {
                    String url = addonUrlMap.getOrDefault(addon.getKey(), "");
                    
                    ShowAppListVo.AppInfo appInfo = new ShowAppListVo.AppInfo();
                    appInfo.setTitle(addon.getTitle());
                    appInfo.setKey(addon.getKey());
                    appInfo.setDesc(addon.getDesc());
                    appInfo.setIcon(addon.getIcon());
                    appInfo.setUrl(ObjectUtil.isNotEmpty(url) ? "/" + url : "");
                    
                    addonVo.getList().add(appInfo);
                }
            }
        }
        
        return map;
    }

    @Override
    public Map<String, ShowAppListVo> getShowAppTools(String appType) {
        Map<String, ShowAppListVo> map = new HashMap<>();
        ShowAppListVo addonVo = new ShowAppListVo();
        addonVo.setTitle("运营工具");
        addonVo.setList(new ArrayList<>());
        map.put("addon", addonVo);
        ShowAppListVo toolsVo = new ShowAppListVo();
        toolsVo.setTitle("系统工具");
        toolsVo.setList(new ArrayList<>());
        map.put("tool", toolsVo);
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONObject appConfig = new JSONObject();
        if (appType.equals("admin")) {
            appConfig = jsonModuleLoader.mergeResultElement("sys/show_admin_app.json");
        } else {
            appConfig = jsonModuleLoader.mergeResultElement("sys/show_site_app.json");
        }


        List<String> appKeys = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(appConfig)) {
            for (String key : appConfig.keySet()) {
                JSONArray jsonArray = appConfig.getJSONArray(key);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ShowAppListVo.AppInfo appInfo = new ShowAppListVo.AppInfo();
                    appInfo.setTitle(jsonObject.getStr("title"));
                    appInfo.setKey(jsonObject.getStr("key"));
                    appInfo.setDesc(jsonObject.getStr("desc"));
                    appInfo.setIcon(jsonObject.getStr("icon"));
                    appInfo.setUrl(jsonObject.getStr("url"));
                    if (key.equals("addon")) {
                        addonVo.getList().add(appInfo);
                    } else if (key.equals("tool")) {
                        toolsVo.getList().add(appInfo);
                    }
                    appKeys.add(appInfo.getKey());
                }

            }
        }
        List<Addon> siteAddons;
        if (appType.equals("admin")) {
            siteAddons = addonMapper.selectList(new QueryWrapper<Addon>().eq("type", "addon"));
        } else {
            siteAddons = this.getSiteAddons();
        }

        for (Addon addon : siteAddons) {
            if (!appKeys.contains(addon.getKey())) {
                ShowAppListVo.AppInfo appInfo = new ShowAppListVo.AppInfo();
                appInfo.setTitle(addon.getTitle());
                appInfo.setKey(addon.getKey());
                appInfo.setDesc(addon.getDesc());
                appInfo.setIcon(addon.getIcon());
                addonVo.getList().add(appInfo);
            }
        }
        return map;
    }

    @Override
    public PageResult<SiteNameListVo> getSiteListByAppName(String appName, String order, String sort, String keyword,PageParam pageParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<Site> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("s")
                .innerJoin("?_site_group sg ON sg.group_id = s.group_id".replace("?_", GlobalConfig.tablePrefix))
                .innerJoin("?_site_shop p ON s.site_id = p.site_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("s.site_id", "s.site_name", "s.logo", "s.icon", "s.front_end_name", "s.front_end_logo");
        queryWrapper.select("p.supply_follow_num");
        if (ObjectUtil.isNotEmpty(order) && ObjectUtil.isNotEmpty(sort)) {
            queryWrapper.orderBy(true, sort.equals("asc"), order);
        } else {
            queryWrapper.orderByDesc("supply_follow_num");
            queryWrapper.orderByDesc("supply_up_num");
            queryWrapper.orderByDesc("supply_sale_num");
        }
        queryWrapper.eq("s.status", SiteStatusEnum.ON.getCode());
        queryWrapper.like("sg.app", appName);
        //查询条件判断组装
        if (ObjectUtil.isNotEmpty(keyword)) {
            queryWrapper.like("site_name", keyword).or().like("keywords", keyword);
        }
        IPage<SiteNameListVo> list = siteMapper.selectJoinPage(new Page<>(page, limit),SiteNameListVo.class, queryWrapper);
        if (!list.getRecords().isEmpty()) {
            Random rand = new Random();
            list.getRecords().forEach(item -> {
                int index = rand.nextInt(LOGOS.length);
                item.setLogo(ObjectUtil.isNotEmpty(item.getLogo()) ? item.getLogo() : LOGOS[index]);
            });
        }
        return PageResult.build(page, limit, list.getTotal()).setData(list.getRecords());
    }

    @Override
    public JSONArray getAdminMenuList(Integer siteId, Boolean isTree, Integer status, String addon, Integer isButton) {
        SiteInfoVo siteInfo = info(siteId);
        Integer isAdmin = 0;
        SysUserRoleInfoVo authRole = null;
        ArrayList<String> strings = new ArrayList<>();
        if (ObjectUtil.isNull(siteInfo)) {
            return new JSONArray();
        }
        String appType = siteInfo.getAppType();
        if (authService.isSuperAdmin()) {
            isAdmin = 1;
        } else {
            authRole = authService.getAuthRole(siteId);
            if (ObjectUtil.isNull(authRole)) {
                return new JSONArray();
            }
            isAdmin = authRole.getIsAdmin();
        }
        if (isAdmin > 0) {
            // 超级管理员获取所有菜单
            List<SysMenu> menuList = sysMenuService.getAdminAllMenuList(appType, status, isButton == 1);
            JSONArray jsonArray = JSONUtil.parseArray(JacksonUtils.toSnakeCaseJSONString(menuList));
            return isTree ? TreeUtils.listToTree(jsonArray, "menu_key", "parent_key", "children") : jsonArray;
        } else {
            String userRoleIds = authRole.getRoleIds();
            strings.add(userRoleIds);
            List<String> menuKeys = sysRoleService.getMenuIdsByRoleIds(siteId, strings);
            List<SysMenu> adminMenuListByMenuKeys = sysMenuService.getAdminMenuListByMenuKeys(RequestUtils.appType(), siteId, status, isButton, menuKeys, addon);
            JSONArray jsonArray = JSONUtil.parseArray(JacksonUtils.toSnakeCaseJSONString(adminMenuListByMenuKeys));
            return isTree ? TreeUtils.listToTree(jsonArray, "menu_key", "parent_key", "children") : jsonArray;
        }

    }

}
