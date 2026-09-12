package com.niu.core.service.admin.site.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.shop.SiteSupply;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.entity.sys.SysMenu;
import com.niu.core.entity.sys.SysUserRole;
import com.niu.core.enums.addon.AddonStatusEnum;
import com.niu.core.enums.addon.AddonTypeEnum;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.enums.sys.AppTypeEnum;
import com.niu.core.enums.sys.CacheTagEnum;
import com.niu.core.event.site.GetCircleListEventDefiner;
import com.niu.core.event.site.SiteAddAfterEvent;
import com.niu.core.event.site.SiteEditAfterEvent;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.shop.SiteSupplyMapper;
import com.niu.core.mapper.site.SiteGroupMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.mapper.sys.SysUserRoleMapper;
import com.niu.core.service.admin.auth.IAuthService;
import com.niu.core.service.admin.shop.ISiteShopService;
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
import com.niu.core.service.core.site.ICoreSiteCategoryService;
import com.niu.core.service.core.site.ICoreSiteService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;


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
    SiteSupplyMapper siteSupplyMapper;

    @Resource
    AddonMapper addonMapper;

    @Resource
    SysUserRoleMapper sysUserRoleMapper;

    @Resource
    SiteGroupMapper siteGroupMapper;

    @Resource
    ICoreSiteService coreSiteService;

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
    @Lazy
    ISiteShopService siteShopService;

    @Resource
    ICoreSiteCategoryService siteCategoryService;

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
            queryWrapper.like("site_name", searchParam.getKeywords());
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
     * 站点列表（不分页），对齐 PHP SiteService::getList
     */
    @Override
    public List<SiteListVo> getList(SiteSearchParam searchParam) {
        QueryWrapper<Site> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("site_id", RequestUtils.defaultSiteId());
        applySiteListSearch(queryWrapper, searchParam);
        queryWrapper.orderByDesc("create_time");

        List<Site> sites = siteMapper.selectList(queryWrapper);
        List<Integer> groupIds = sites.stream().map(Site::getGroupId).distinct().toList();
        List<SiteGroup> siteGroups = new ArrayList<>();
        if (!groupIds.isEmpty()) {
            siteGroups = siteGroupMapper.selectByIds(groupIds);
        }

        List<SiteListVo> resultList = new LinkedList<>();
        for (Site item : sites) {
            SiteListVo vo = new SiteListVo();
            BeanUtils.copyProperties(item, vo);
            siteGroups.stream()
                    .filter(group -> group.getGroupId().equals(item.getGroupId()))
                    .findFirst()
                    .ifPresent(group -> vo.setGroupName(group.getGroupName()));
            resultList.add(vo);
        }
        return resultList;
    }

    private void applySiteListSearch(QueryWrapper<Site> queryWrapper, SiteSearchParam searchParam) {
        if (ObjectUtil.isNotEmpty(searchParam.getKeywords())) {
            queryWrapper.like("site_name", searchParam.getKeywords());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) {
            queryWrapper.eq("status", searchParam.getStatus());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getGroupId())) {
            queryWrapper.eq("group_id", searchParam.getGroupId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getApp())) {
            queryWrapper.like("app", "\"" + searchParam.getApp() + "\"");
        }
        if (ObjectUtil.isNotEmpty(searchParam.getSiteDomain())) {
            queryWrapper.like("site_domain", searchParam.getSiteDomain());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {
            applyTimeRange(queryWrapper, "create_time", searchParam.getCreateTime());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getExpireTime())) {
            applyTimeRange(queryWrapper, "expire_time", searchParam.getExpireTime());
        }
    }

    private void applyTimeRange(QueryWrapper<Site> queryWrapper, String column, String[] timeRange) {
        Long startTime = (timeRange[0] == null) ? 0 : DateUtils.StringToTimestamp(timeRange[0]);
        Long endTime = (timeRange[1] == null) ? 0 : DateUtils.StringToTimestamp(timeRange[1]);
        if (startTime > 0 && endTime > 0) {
            queryWrapper.between(column, startTime, endTime);
        } else if (startTime > 0) {
            queryWrapper.ge(column, startTime);
        } else if (endTime > 0) {
            queryWrapper.le(column, endTime);
        }
    }

    /**
     * 站点详情
     *
     * @param id 主键
     * @return SiteInfoVo
     */
    @Override
    public SiteInfoVo info(Integer id) {
        SiteInfoVo siteInfoVo = coreSiteService.getSiteCache(id);
        return siteInfoVo;
    }

    /**
     * 站点添加
     *
     * @param addParam 参数
     * @return
     */
    @Transactional
    @Override
    public Integer add(SiteAddParam addParam) {
        SiteGroup siteGroup;

        // 如果提供了行业类目ID，通过类目服务获取分组信息
        if (ObjectUtil.isNotEmpty(addParam.getCategoryId())) {
            siteGroup = siteCategoryService.getGroupInfoByCategoryId(addParam.getCategoryId());
        } else {
            siteGroup = siteGroupService.info(addParam.getGroupId());
        }

        if (ObjectUtil.isNull(siteGroup) || ObjectUtil.isEmpty(siteGroup)) {
            throw new CommonException("套餐不存在");
        }

        Site model = new Site();
        model.setSiteName(addParam.getSiteName());
        model.setGroupId(siteGroup.getGroupId());
        model.setAppType(AppTypeEnum.SITE.getName());
        model.setCreateTime(DateUtils.currTime());
        String expireTimeStr = addParam.getExpireTime();
        if (expireTimeStr == null || expireTimeStr.isEmpty() || "0".equals(expireTimeStr)) {
            model.setExpireTime(0L);
        } else {
            model.setExpireTime(DateUtils.StringToTimestamp(expireTimeStr));
        }
        model.setApp(siteGroup.getApp());
        model.setAddons(siteGroup.getAddon() != null ? siteGroup.getAddon() : "");
        model.setSiteDomain(addParam.getSiteDomain());

        // 设置行业类目ID集合
        if (ObjectUtil.isNotEmpty(addParam.getCategoryId())) {
            model.setCategoryIds(JSONUtil.toJsonStr(List.of(addParam.getCategoryId())));
        }

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
        return siteId;
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
            throw new CommonException("套餐不存在");
        }
        initallJsonArray.addAll(JSONUtil.parseArray(siteGroup.getApp()));
        initallJsonArray.addAll(JSONUtil.parseArray(siteGroup.getAddon()));

        model.setInitalledAddon(JSONUtil.toJsonStr(initallJsonArray));
        model.setSiteDomain(editParam.getSiteDomain());
        if(ObjectUtil.isNotEmpty(editParam.getExpireTime())){
            model.setExpireTime(new DateTime(editParam.getExpireTime()).getTime() / 1000);
        }else{
            model.setExpireTime(0L);
        }
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
        siteShopService.closeShop(siteId);
    }

    @Override
    public void openSite(Integer siteId) {
        siteShopService.openShop(siteId);
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
            List<String> addonKeys = coreSiteService.getAddonKeysBySiteId(siteId);
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
        List<String> addonKeys = listInstalledAddonKeys(siteAddonKeys, param);
        return coreSiteService.getShowAppTools(addonKeys, AppTypeEnum.SITE.getName(), RequestUtils.siteId());
    }

    @Override
    public Map<String, ShowAppListVo> getAdminAppTools(SiteAppToolsParam param) {
        List<String> addonKeys = listInstalledAddonKeys(null, param);
        return coreSiteService.getShowAppTools(addonKeys, AppTypeEnum.ADMIN.getName(), 0);
    }

    private List<String> listInstalledAddonKeys(List<String> siteAddonKeys, SiteAppToolsParam param) {
        LambdaQueryWrapper<Addon> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Addon::getType, AddonTypeEnum.ADDON.getType())
                .eq(Addon::getStatus, AddonStatusEnum.ON.getCode())
                .select(Addon::getKey);
        if (siteAddonKeys != null && !siteAddonKeys.isEmpty()) {
            wrapper.in(Addon::getKey, siteAddonKeys);
        } else if (siteAddonKeys != null) {
            return List.of();
        }
        if (param != null && ObjectUtil.isNotEmpty(param.getTitle())) {
            wrapper.like(Addon::getTitle, param.getTitle());
        }
        return addonMapper.selectList(wrapper).stream()
                .map(Addon::getKey)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<SiteNameListVo> getSiteListByAppName(String appName, String order, String sort, String keyword, PageParam pageParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<SiteSupply> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("sp")
                .leftJoin("?_site s ON sp.site_id = s.site_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("sp.site_id", "s.site_name", "s.logo", "s.icon", "s.front_end_name", "s.front_end_logo", "sp.supply_follow_num");
        queryWrapper.eq("s.status", SiteStatusEnum.ON.getCode());
        if (StrUtil.isNotBlank(appName)) {
            queryWrapper.like("s.app", "\"" + appName + "\"");
        }
        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.and(w -> w.like("s.site_name", keyword).or().like("sp.keywords", keyword));
        }
        if (ObjectUtil.isNotEmpty(order) && ObjectUtil.isNotEmpty(sort)) {
            queryWrapper.orderBy(true, sort.equals("asc"), order);
        } else {
            queryWrapper.orderByDesc("sp.supply_follow_num");
            queryWrapper.orderByDesc("sp.supply_up_num");
            queryWrapper.orderByDesc("sp.supply_sale_num");
        }
        IPage<SiteNameListVo> list = siteSupplyMapper.selectJoinPage(new Page<>(page, limit), SiteNameListVo.class, queryWrapper);
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
            List<SysMenu> menuList = sysMenuService.getAdminAllMenuList(appType, status, isButton == 1, 0);
            JSONArray jsonArray = JSONUtil.parseArray(JacksonUtils.toSnakeCaseJSONString(menuList));
            return isTree ? TreeUtils.listToTree(jsonArray, "menu_key", "parent_key", "children") : jsonArray;
        } else {
            String userRoleIds = authRole.getRoleIds();
            strings.add(userRoleIds);
            List<String> menuKeys = sysRoleService.getMenuIdsByRoleIds(siteId, strings);
            List<SysMenu> adminMenuListByMenuKeys = sysMenuService.getAdminMenuListByMenuKeys(RequestUtils.appType(), siteId, status, isButton, menuKeys, addon, 0);
            JSONArray jsonArray = JSONUtil.parseArray(JacksonUtils.toSnakeCaseJSONString(adminMenuListByMenuKeys));
            return isTree ? TreeUtils.listToTree(jsonArray, "menu_key", "parent_key", "children") : jsonArray;
        }

    }

    @Override
    public Map<String, Map<String, Object>> getSitePromotionTools(SiteAppToolsParam param) {
        // 1. 获取当前站点已购买的插件 key 列表
        List<String> siteAddonKeys = coreSiteService.getAddonKeysBySiteId(RequestUtils.siteId());
        if (siteAddonKeys == null || siteAddonKeys.isEmpty()) {
            return coreSiteService.getShowPromotionTools(List.of(), AppTypeEnum.SITE.getName(), RequestUtils.siteId());
        }

        // 2. 查询已安装且启用的插件（类型为 addon，状态为启用，且 key 在站点购买列表中）
        LambdaQueryWrapper<Addon> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Addon::getType, AddonTypeEnum.ADDON.getType())
                .eq(Addon::getStatus, AddonStatusEnum.ON.getCode())
                .in(Addon::getKey, siteAddonKeys);
        // 动态添加搜索条件（如 title 模糊查询）
        if (ObjectUtil.isNotEmpty(param.getTitle())) {
            wrapper.like(Addon::getTitle, param.getTitle());
        }
        List<String> addonKeys = addonMapper.selectList(wrapper)
                .stream().map(Addon::getKey).collect(Collectors.toList());

        return coreSiteService.getShowPromotionTools(addonKeys, AppTypeEnum.SITE.getName(), RequestUtils.siteId());
    }

    @Override
    public Map<String, Map<String, Object>> getAdminPromotionTools(SiteAppToolsParam param) {
        // 1. 查询所有已安装且启用的插件（类型为 addon，状态为启用）
        LambdaQueryWrapper<Addon> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Addon::getType, AddonTypeEnum.ADDON.getType())
                .eq(Addon::getStatus, AddonStatusEnum.ON.getCode());
        if (ObjectUtil.isNotEmpty(param.getTitle())) {
            wrapper.like(Addon::getTitle, param.getTitle());
        }
        List<String> addonKeys = addonMapper.selectList(wrapper)
                .stream().map(Addon::getKey).toList();

        return coreSiteService.getShowPromotionTools(addonKeys, AppTypeEnum.ADMIN.getName(), null);
    }

    /**
     * 获取商圈列表（不分页），对齐 PHP SiteService::getCircleList
     */
    @Override
    public List<?> getCircleList(SiteCircleListParam param) {
        GetCircleListEventDefiner.GetCircleListEvent event = new GetCircleListEventDefiner.GetCircleListEvent();
        if (param != null && param.getUid() != null && param.getUid() > 0) {
            event.setUid(param.getUid());
        }
        return publishGetCircleList(event);
    }

    /**
     * 发布 GetCircleList 事件并合并各插件返回的商圈树
     */
    public List<Object> publishGetCircleList(GetCircleListEventDefiner.GetCircleListEvent event) {
        List<GetCircleListEventDefiner.GetCircleListResult> resultList = EventAndSubscribeOfPublisher.publishAndCallback(event);
        List<Object> list = new ArrayList<>();
        if (resultList == null || resultList.isEmpty()) {
            return list;
        }
        for (GetCircleListEventDefiner.GetCircleListResult result : resultList) {
            if (result == null || result.getList() == null) {
                continue;
            }
            list.addAll(result.getList());
        }
        return list;
    }

    @Override
    public void editBusinessStatus(Integer businessStatus) {
        coreSiteService.editSiteBusinessStatus(RequestUtils.siteId(), businessStatus);
    }

    @Override
    public List<SysMenu> getShowMenuList(Integer siteId) {
        List<String> menuKeys = getMenuIdsBySiteId(siteId, 1);
        return sysMenuService.getShowMenuListByMenuKeys(menuKeys);
    }

    private List<String> getMenuIdsBySiteId(Integer siteId, Integer status) {
        SiteInfoVo siteInfo = coreSiteService.getSiteCache(siteId);
        if (siteInfo == null) {
            return List.of();
        }
        if (AppTypeEnum.ADMIN.getName().equals(siteInfo.getAppType())) {
            return sysMenuService.getAllMenuKeysByAppType(siteInfo.getAppType(), status);
        }
        List<String> addonKeys = coreSiteService.getAddonKeysBySiteId(siteId);
        return sysMenuService.getMenuKeysBySystem(siteInfo.getAppType(), addonKeys);
    }

}
