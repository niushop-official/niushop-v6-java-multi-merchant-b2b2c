package com.niu.core.service.core.site.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.component.context.cache.CachedServiceImpl;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteAddonInitRecord;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.entity.sys.SysUser;
import com.niu.core.enums.addon.AddonActionEnum;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.enums.sys.CacheTagEnum;
import com.niu.core.event.site.GetSiteBusinessStatusEventDefiner;
import com.niu.core.event.site.EditSiteBusinessStatusEventDefiner;
import com.niu.core.event.site.SiteAddAfterEvent;
import com.niu.core.event.site.SiteEditAfterEvent;
import com.niu.core.event.site.SiteExpireCloseAfterEvent;
import com.niu.core.event.sys.ShowAppEventDefiner;
import com.niu.core.event.sys.ShowPromotionEventDefiner;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.site.SiteAddonInitRecordMapper;
import com.niu.core.mapper.site.SiteGroupMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.mapper.sys.SysUserMapper;
import com.niu.core.service.admin.addon.IAddonService;
import com.niu.core.service.admin.shop.param.SiteShopBasicInfoParam;
import com.niu.core.service.admin.site.param.SiteAddParam;
import com.niu.core.service.admin.site.param.SiteEditParam;
import com.niu.core.service.admin.site.param.SiteUserParam;
import com.niu.core.service.admin.site.vo.ShowAppListVo;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.admin.sys.ISysUserService;
import com.niu.core.service.admin.sys.param.SysUserRoleParam;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.site.ICoreSiteCategoryService;
import com.niu.core.service.core.site.ICoreSiteService;
import com.niu.core.service.core.site.vo.SiteInfoCacheVo;
import com.niu.core.service.core.user.ICoreUserRoleService;
import com.niu.core.service.core.user.ICoreUserService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 站点服务层
 */
@Service
public class CoreSiteServiceImpl extends CachedServiceImpl<SiteMapper, Site> implements ICoreSiteService {

    /**
     * 站点tag
     */
    public static String cacheTagName = CacheTagEnum.SITE_CACHE.getTagName();

    /**
     * 是否使用缓存
     */
    public static final boolean useCache = true;

    @Resource
    private Cached cached;

    @Resource
    SiteMapper siteMapper;

    @Resource
    AddonMapper addonMapper;

    @Resource
    IAddonService addonService;

    @Resource
    SiteGroupMapper siteGroupMapper;

    @Resource
    SiteAddonInitRecordMapper siteAddonInitRecordMapper;

    @Resource
    ICoreUserService coreUserService;

    @Resource
    ISysUserService sysUserService;

    @Resource
    ICoreSiteCategoryService coreSiteCategoryService;

    @Resource
    ICoreUserRoleService coreUserRoleService;

    @Lazy
    @Resource
    SysUserMapper sysUserMapper;

    /**
     * 获取店铺业务类型（站点 app 字段），对齐 PHP CoreSiteService::getSiteTradeType
     */
    @Override
    public List<String> getSiteTradeType(Integer siteId) {
        if (siteId == null || siteId <= 0) {
            return new ArrayList<>();
        }
        // Keep trade-type decisions in sync with the current ns_site.app value.
        Site siteInfo = siteMapper.selectById(siteId);
        if (siteInfo == null || ObjectUtil.isEmpty(siteInfo.getApp())) {
            return new ArrayList<>();
        }
        try {
            JSONArray appJson = JSONUtil.parseArray(siteInfo.getApp());
            return JSONUtil.toList(appJson, String.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 通过站点id获取支持的应用插件
     *
     * @param siteId
     */
    @Override
    public List<String> getAddonKeysBySiteId(Integer siteId) {

        return cached.rememberObject(useCache, cacheTagName + "_" + siteId, Arrays.asList("getAddonKeysBySiteId", siteId), uniqueKey -> {

            Site siteInfo = siteMapper.selectById(siteId);
            if (ObjectUtil.isNull(siteInfo)) {
                return new ArrayList<String>();
            }
            //查询站点分组
            SiteGroup siteGroup = siteGroupMapper.selectById(siteInfo.getGroupId());
            JSONArray siteAddonJson = new JSONArray();
            if (!siteInfo.getAddons().equals("")) {
                JSONArray addonJson = JSONUtil.parseArray(siteInfo.getAddons());
                siteAddonJson.addAll(addonJson);
            }
            if (!siteInfo.getApp().equals("")) {
                JSONArray appJson = JSONUtil.parseArray(siteInfo.getApp());
                siteAddonJson.addAll(appJson);
            }
            if (ObjectUtil.isNotNull(siteGroup)) {
                if (!siteGroup.getApp().equals("")) {
                    JSONArray groupAppJson = JSONUtil.parseArray(siteGroup.getApp());
                    siteAddonJson.addAll(groupAppJson);
                }
                if (!siteGroup.getAddon().equals("")) {
                    JSONArray groupAddonJson = JSONUtil.parseArray(siteGroup.getAddon());
                    siteAddonJson.addAll(groupAddonJson);
                }
            }
            //去重
            List<String> list = JSONUtil.toList(siteAddonJson, String.class);
            return CollectionUtil.distinct(list);
        });


    }

    /**
     * 站点详情
     *
     * @param siteId 主键ID
     * @return SiteInfoVo
     */
    @Override
    public SiteInfoVo getSiteCache(Integer siteId) {

        SiteInfoCacheVo siteCache = cached.rememberObject(useCache, cacheTagName + "_" + siteId, Arrays.asList("getSiteCache", siteId), uniqueKey -> {
            MPJQueryWrapper<Site> siteMPJQueryWrapper = new MPJQueryWrapper<>();
            siteMPJQueryWrapper.select("ns.site_id, ns.site_name, ns.group_id, ns.app_type, ns.logo, ns.`desc`, ns.status, ns.latitude, ns.longitude, ns.province_id, ns.city_id, ns.district_id, ns.address, ns.full_address, ns.phone, ns.business_hours, ns.business_state, ns.create_time, ns.expire_time, ns.front_end_name, ns.front_end_logo, ns.icon, ns.member_no, ns.app, ns.addons, ns.initalled_addon, ns.site_domain, nsg.group_name")
                    .setAlias("ns")
                    .leftJoin("?_site_group nsg ON ns.group_id = nsg.group_id".replace("?_", GlobalConfig.tablePrefix));
            siteMPJQueryWrapper.eq("ns.site_id", siteId);

            return siteMapper.selectJoinOne(SiteInfoCacheVo.class, siteMPJQueryWrapper);

        });
        SiteInfoVo siteInfoVo = new SiteInfoVo();
        if (ObjectUtil.isNotEmpty(siteCache)) {
            BeanUtil.copyProperties(siteCache, siteInfoVo);
            siteInfoVo.setBusinessStatus(getSiteBusinessStatus(siteId));
            SiteGroup siteGroup = new SiteGroup();
            siteGroup.setGroupId(siteInfoVo.getGroupId());
            siteGroup.setGroupName(siteInfoVo.getGroupName());
            siteInfoVo.setSiteGroup(siteGroup);
            siteInfoVo.setAddonKeys(getAddonKeysBySiteId(siteInfoVo.getSiteId()));
            if (siteInfoVo.getAddonKeys().size() != 0) {
                siteInfoVo.setSiteAddons(addonService.getAddonListByKeys(siteInfoVo.getAddonKeys(), AddonActionEnum.ADDON.getCode()));
                siteInfoVo.setApps(addonService.getAddonListByKeys(siteInfoVo.getAddonKeys(), AddonActionEnum.APP.getCode()));
            } else {
                siteInfoVo.setSiteAddons(new ArrayList<>());
                siteInfoVo.setApps(new ArrayList<>());
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("version", GlobalConfig.version);
        siteInfoVo.setVersion(jsonObject.toString());
        return siteInfoVo;
    }

    @Override
    public List<Addon> getSiteAddons(Integer siteId) {
        return addonMapper.selectList(new QueryWrapper<Addon>().in("`key`", getAddonKeysBySiteId(siteId)));
    }

    /**
     * 站点插件是否已执行初始化
     *
     * @param siteId
     * @param addon
     * @return
     */
    @Override
    public boolean siteAddonIsInit(Integer siteId, String addon) {
        Long count = siteAddonInitRecordMapper.selectCount(new QueryWrapper<SiteAddonInitRecord>().eq("site_id", siteId).eq("addon", addon));
        return count == 1 ? true : false;
    }

    /**
     * 添加站点插件初始化记录
     *
     * @param siteId
     * @param addon
     */
    @Override
    public void addSiteAddonInitRecord(Integer siteId, String addon) {
        SiteAddonInitRecord model = new SiteAddonInitRecord();
        model.setSiteId(siteId);
        model.setAddon(addon);
        siteAddonInitRecordMapper.insert(model);
    }

    /**
     * 站点到期关闭
     */
    @Override
    public void siteExpireClose() {
        long now = System.currentTimeMillis() / 1000;
        List<Site> expireSites = siteMapper.selectList(new QueryWrapper<Site>()
                .gt("expire_time", 0)
                .lt("expire_time", now)
                .ne("status", SiteStatusEnum.EXPIRE.getCode())
                .select("site_id"));

        Site model = new Site();
        model.setStatus(SiteStatusEnum.EXPIRE.getCode());
        siteMapper.update(model, new QueryWrapper<Site>()
                .gt("expire_time", 0)
                .lt("expire_time", now)
                .ne("status", SiteStatusEnum.EXPIRE.getCode())
        );

        for (Site site : expireSites) {
            SiteExpireCloseAfterEvent event = new SiteExpireCloseAfterEvent();
            event.setSiteId(site.getSiteId());
            event.setName("SiteExpireCloseAfterEvent");
            EventAndSubscribeOfPublisher.publishAll(event);
        }
    }

    /**
     * 清除站点缓存
     */
    @Override
    public void clearSiteCache(Integer siteId) {
        cached.tag(cacheTagName + "_" + siteId).clear();
    }

    @Override
    public SiteInfoVo getShopBasicInfo(Integer siteId) {

        String field = "ns.site_id, ns.site_name, ns.status, ns.create_time, ns.expire_time, ns.group_id";

        MPJQueryWrapper<Site> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.select(field)
                .setAlias("ns")
                .leftJoin("?_site_group nsg ON ns.group_id = nsg.group_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_shop nss ON ns.site_id = nss.site_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.eq("ns.site_id", siteId);

        SiteInfoCacheVo siteInfoCacheVo = siteMapper.selectJoinOne(SiteInfoCacheVo.class, queryWrapper);

        if (ObjectUtil.isNotEmpty(siteInfoCacheVo)) {
            SiteInfoVo siteInfoVo = new SiteInfoVo();
            BeanUtil.copyProperties(siteInfoCacheVo, siteInfoVo);
            siteInfoVo.setStatusName(SiteStatusEnum.getNameByCode(siteInfoVo.getStatus()));

            return siteInfoVo;
        }

        return null;
    }

    /**
     * 查询应用列表（对齐 PHP CoreSiteService::getShowAppTools）
     */
    @Override
    public Map<String, ShowAppListVo> getShowAppTools(List<String> addons, String appType, Integer siteId) {
        Map<String, ShowAppListVo> result = new LinkedHashMap<>();
        ShowAppListVo operationGroup = new ShowAppListVo();
        operationGroup.setTitle("运营工具");
        operationGroup.setList(new ArrayList<>());
        result.put("operation", operationGroup);

        ShowAppListVo systemGroup = new ShowAppListVo();
        systemGroup.setTitle("系统工具");
        systemGroup.setList(new ArrayList<>());
        result.put("system", systemGroup);

        Set<String> installedAddonKeys = addons == null ? Set.of() : new HashSet<>(addons);
        for (Map<String, String> app : collectShowAppItems(appType, siteId)) {
            String addonKey = app.get("addon_key");
            if (StringUtils.hasText(addonKey) && !installedAddonKeys.contains(addonKey)) {
                continue;
            }
            String groupKey = app.get("group_key");
            if (!result.containsKey(groupKey)) {
                continue;
            }
            result.get(groupKey).getList().add(toAppInfo(app));
        }
        return result;
    }

    /**
     * 查询营销工具和活动列表
     *
     * @param addons  已安装的插件 key 集合
     * @param appType 应用类型
     * @param siteId  站点 ID
     * @return 分组的营销项
     */
    @Override
    public Map<String, Map<String, Object>> getShowPromotionTools(List<String> addons, String appType, Integer siteId) {
        Map<String, Map<String, Object>> result = new LinkedHashMap<>();
        Map<String, Object> toolGroup = new LinkedHashMap<>();
        toolGroup.put("title", "营销工具");
        toolGroup.put("list", new ArrayList<Map<String, String>>());
        result.put("tool", toolGroup);

        Map<String, Object> activityGroup = new LinkedHashMap<>();
        activityGroup.put("title", "营销活动");
        activityGroup.put("list", new ArrayList<Map<String, String>>());
        result.put("activity", activityGroup);

        Set<String> installedAddonKeys = addons == null ? Set.of() : new HashSet<>(addons);
        for (Map<String, String> promotion : collectShowPromotionItems(appType, siteId)) {
            String addonKey = promotion.get("addon_key");
            if (StringUtils.hasText(addonKey) && !installedAddonKeys.contains(addonKey)) {
                continue;
            }
            String groupKey = promotion.get("group_key");
            if (result.containsKey(groupKey)) {
                @SuppressWarnings("unchecked")
                List<Map<String, String>> list = (List<Map<String, String>>) result.get(groupKey).get("list");
                list.add(promotion);
            }
        }
        return result;
    }

    private List<Map<String, String>> collectShowAppItems(String appType, Integer siteId) {
        ShowAppEventDefiner.ShowAppEvent event = new ShowAppEventDefiner.ShowAppEvent();
        event.setAppType(appType);
        if (siteId != null && siteId > 0) {
            event.setSiteId(siteId);
        }
        event.setAuthority(false);
        return mergeEventList(EventAndSubscribeOfPublisher.publishAndCallback(event));
    }

    private List<Map<String, String>> collectShowPromotionItems(String appType, Integer siteId) {
        ShowPromotionEventDefiner.ShowPromotionEvent event = new ShowPromotionEventDefiner.ShowPromotionEvent();
        event.setAppType(appType);
        if (siteId != null && siteId > 0) {
            event.setSiteId(siteId);
        }
        event.setAuthority(false);
        return mergeEventList(EventAndSubscribeOfPublisher.publishAndCallback(event));
    }

    private List<Map<String, String>> mergeEventList(List<?> results) {
        if (results == null || results.isEmpty()) {
            return List.of();
        }
        List<Map<String, String>> merged = new ArrayList<>();
        for (Object result : results) {
            if (result instanceof ShowAppEventDefiner.ShowAppResult showAppResult && showAppResult.getList() != null) {
                merged.addAll(showAppResult.getList());
            } else if (result instanceof ShowPromotionEventDefiner.ShowPromotionResult showPromotionResult
                    && showPromotionResult.getList() != null) {
                merged.addAll(showPromotionResult.getList());
            }
        }
        return merged;
    }

    private ShowAppListVo.AppInfo toAppInfo(Map<String, String> item) {
        ShowAppListVo.AppInfo appInfo = new ShowAppListVo.AppInfo();
        appInfo.setTitle(item.get("title"));
        appInfo.setDesc(item.get("desc"));
        appInfo.setIcon(item.get("icon"));
        appInfo.setKey(item.get("key"));
        appInfo.setUrl(item.get("url"));
        return appInfo;
    }

    @Override
    public void editBasicInfo(Integer siteId, Map<String, Object> data) {
        Site site = siteMapper.selectById(siteId);
        if (site == null) throw new CommonException("站点不存在");
        BeanUtil.fillBeanWithMap(data, site, false);
        siteMapper.updateById(site);
        // 清除缓存
        clearSiteCache(siteId);
    }

    @Override
    public void updateSiteBasicFields(Integer siteId, SiteShopBasicInfoParam editParam) {
        Site model = siteMapper.selectOne(
                new QueryWrapper<Site>()
                        .eq("site_id", siteId)
                        .last("limit 1"));
        if (ObjectUtil.isNull(model)) {
            throw new CommonException("站点不存在");
        }
        model.setSiteName(editParam.getSiteName());
        model.setPhone(editParam.getPhone());
        model.setLogo(editParam.getLogo());
        model.setIcon(editParam.getIcon());
        model.setDesc(editParam.getDesc());
        model.setFrontEndName(editParam.getFrontEndName());
        model.setFrontEndLogo(editParam.getFrontEndLogo());
        model.setLatitude(editParam.getLatitude());
        model.setLongitude(editParam.getLongitude());
        model.setProvinceId(editParam.getProvinceId());
        model.setCityId(editParam.getCityId());
        model.setDistrictId(editParam.getDistrictId());
        model.setAddress(editParam.getAddress());
        model.setFullAddress(editParam.getFullAddress());
        model.setBusinessHours(editParam.getBusinessHours());
        model.setContactName(editParam.getContactName());
        siteMapper.updateById(model);
    }

    @Override
    public void publishSiteEditAfter(Integer siteId) {
        Site model = siteMapper.selectById(siteId);
        if (ObjectUtil.isNull(model)) {
            throw new CommonException("站点不存在");
        }
        SiteGroup siteGroup = siteGroupMapper.selectById(model.getGroupId());
        SiteEditAfterEvent event = new SiteEditAfterEvent();
        event.setSiteId(siteId);
        event.addAppSign("core");
        event.setName("SiteEditAfterEvent");
        event.setSite(model);
        event.setSiteGroup(siteGroup);
        EventAndSubscribeOfPublisher.publishAll(event);
        clearSiteCache(siteId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer add(SiteAddParam addParam) {
        // 1. 校验用户名是否重复
        if (StringUtils.hasText(addParam.getUsername()) && coreUserService.checkUsername(addParam.getUsername())) {
            throw new CommonException("USERNAME_REPEAT");
        }

        // 2. 根据分类ID获取关联的站点分组信息
        SiteGroup siteGroup = coreSiteCategoryService.getGroupInfoByCategoryId(addParam.getCategoryId());
        if (siteGroup == null) {
            throw new CommonException("套餐不存在");
        }

        // 3. 构建站点数据（对应 PHP 的 data_site）
        Site site = new Site();
        site.setSiteName(addParam.getSiteName());
        site.setAppType("site");
        site.setGroupId(siteGroup.getGroupId());
        site.setCreateTime(System.currentTimeMillis() / 1000);
        site.setPhone(StringUtils.hasText(addParam.getPhone()) ? addParam.getPhone() : "");
        site.setApp(siteGroup.getApp());          // JSON 数组
        site.setAddons(siteGroup.getAddon());     // JSON 数组
        site.setFrontEndLogo(StringUtils.hasText(addParam.getFrontEndLogo()) ? addParam.getFrontEndLogo() : "");
        site.setLatitude(StringUtils.hasText(addParam.getLatitude()) ? addParam.getLatitude() : "");
        site.setLongitude(StringUtils.hasText(addParam.getLongitude()) ? addParam.getLongitude() : "");
        site.setProvinceId(addParam.getProvinceId() != null ? addParam.getProvinceId() : 0);
        site.setCityId(addParam.getCityId() != null ? addParam.getCityId() : 0);
        site.setDistrictId(addParam.getDistrictId() != null ? addParam.getDistrictId() : 0);
        site.setAddress(StringUtils.hasText(addParam.getAddress()) ? addParam.getAddress() : "");
        site.setFullAddress(StringUtils.hasText(addParam.getFullAddress()) ? addParam.getFullAddress() : "");
        site.setContactName(StringUtils.hasText(addParam.getContactName()) ? addParam.getContactName() : "");
        site.setBusinessLicense(StringUtils.hasText(addParam.getBusinessLicense()) ? addParam.getBusinessLicense() : "");
        site.setCategoryIds(JSONUtil.toJsonStr(List.of(String.valueOf(addParam.getCategoryId()))));

        if (addParam.getSiteId() != null) {
            site.setSiteId(addParam.getSiteId());
        }

        // 4. 事务：保存站点
        siteMapper.insert(site);
        Integer siteId = site.getSiteId();

        // 5. 处理用户和角色
        Integer uid = addParam.getUid();
        if (uid != null && uid > 0) {
            // 已有用户，直接添加管理员角色
            SysUserRoleParam param = new SysUserRoleParam();
            param.setUid(uid);
            param.setRoleIds("");
            param.setIsAdmin(1);
            param.setSiteId(siteId);
            coreUserRoleService.add(param);
            if (StringUtils.hasText(addParam.getMobile())) {
                SysUser user = sysUserMapper.selectById(uid);
                if (user != null && !StringUtils.hasText(user.getMobile())) {
                    user.setMobile(addParam.getMobile());
                    user.setUpdateTime(System.currentTimeMillis() / 1000);
                    sysUserMapper.updateById(user);
                }
            }
        } else {
            // 创建新用户
            SiteUserParam userParam = new SiteUserParam();
            userParam.setUsername(addParam.getUsername());
            userParam.setHeadImg(StringUtils.hasText(addParam.getHeadImg()) ? addParam.getHeadImg() : "");
            userParam.setStatus(addParam.getStatus() != null ? addParam.getStatus() : 1);
            userParam.setRealName(StringUtils.hasText(addParam.getRealName()) ? addParam.getRealName() : "");
            userParam.setPassword(addParam.getPassword());
            userParam.setRoleIds(new ArrayList<>());
            userParam.setMobile(StringUtils.hasText(addParam.getMobile()) ? addParam.getMobile() : "");
            userParam.setIsAdmin(1);
            uid = coreUserService.addSiteUser(userParam, siteId);
        }

        // 6. 发布站点添加成功事件
        SiteAddAfterEvent event = new SiteAddAfterEvent();
        event.setSiteId(siteId);
        event.addAppSign("core");
        event.setName("SiteAddAfterEvent");
        event.setSite(site);
        event.setSiteGroup(siteGroup);
        EventAndSubscribeOfPublisher.publishAll(event);

        // 7. 清除用户角色缓存（对应 PHP 的 Cache::delete('user_role_list_' . $uid)）
        cached.remove("user_role_list_" + uid);

        return siteId;
    }

    @Override
    @Transactional
    public void edit(Integer id, SiteEditParam editParam) {
        Site model = siteMapper.selectOne(
                new QueryWrapper<Site>()
                        .eq("site_id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        if (ObjectUtil.isNotNull(editParam.getGroupId()) && ObjectUtil.isNotEmpty(editParam.getGroupId()) && !editParam.getGroupId().equals(model.getGroupId())) {
            model.setGroupId(editParam.getGroupId());
        }
        if (ObjectUtil.isNotNull(editParam.getSiteName()) && ObjectUtil.isNotEmpty(editParam.getSiteName())) {
            model.setSiteName(editParam.getSiteName());
        }
        if (editParam.getPhone() != null) {
            model.setPhone(editParam.getPhone());
        }
        if (editParam.getFrontEndLogo() != null) {
            model.setFrontEndLogo(editParam.getFrontEndLogo());
        }
        if (editParam.getBusinessLicense() != null) {
            model.setBusinessLicense(editParam.getBusinessLicense());
        }
        if (editParam.getContactName() != null) {
            model.setContactName(editParam.getContactName());
        }
        if (editParam.getLatitude() != null) {
            model.setLatitude(editParam.getLatitude());
        }
        if (editParam.getLongitude() != null) {
            model.setLongitude(editParam.getLongitude());
        }
        if (editParam.getProvinceId() != null) {
            model.setProvinceId(editParam.getProvinceId());
        }
        if (editParam.getCityId() != null) {
            model.setCityId(editParam.getCityId());
        }
        if (editParam.getDistrictId() != null) {
            model.setDistrictId(editParam.getDistrictId());
        }
        if (editParam.getAddress() != null) {
            model.setAddress(editParam.getAddress());
        }
        if (editParam.getFullAddress() != null) {
            model.setFullAddress(editParam.getFullAddress());
        }
        if (editParam.getSiteDomain() != null) {
            model.setSiteDomain(editParam.getSiteDomain());
        }
        if (ObjectUtil.isNotEmpty(editParam.getExpireTime())) {
            model.setExpireTime(new DateTime(editParam.getExpireTime()).getTime() / 1000);
            model.setStatus(model.getExpireTime() > DateUtils.currTime() ? SiteStatusEnum.ON.getCode() : SiteStatusEnum.EXPIRE.getCode());
        }

        siteMapper.updateById(model);
        clearSiteCache(id);

        SiteGroup siteGroup = null;
        if (model.getGroupId() != null && model.getGroupId() > 0) {
            siteGroup = siteGroupMapper.selectById(model.getGroupId());
        }

        SiteEditAfterEvent event = new SiteEditAfterEvent();
        event.setSiteId(model.getSiteId());
        event.addAppSign("core");
        event.setName("SiteEditAfterEvent");
        event.setSite(model);
        event.setSiteGroup(siteGroup);
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    @Override
    public Integer getSiteBusinessStatus(Integer siteId) {
        GetSiteBusinessStatusEventDefiner.GetSiteBusinessStatusEvent event = new GetSiteBusinessStatusEventDefiner.GetSiteBusinessStatusEvent();
        event.setSiteId(siteId);
        event.setAuthority(true);
        List<GetSiteBusinessStatusEventDefiner.GetSiteBusinessStatusResult> resultList =
                EventAndSubscribeOfPublisher.publishAndCallbackBySite(event);
        int businessStatus = 0;
        if (resultList != null) {
            for (GetSiteBusinessStatusEventDefiner.GetSiteBusinessStatusResult result : resultList) {
                if (result != null && result.getBusinessStatus() != null && result.getBusinessStatus() != 0) {
                    businessStatus = 1;
                    break;
                }
            }
        }
        return businessStatus;
    }

    @Override
    public void editSiteBusinessStatus(Integer siteId, Integer businessStatus) {
        EditSiteBusinessStatusEventDefiner.EditSiteBusinessStatusEvent event =
                new EditSiteBusinessStatusEventDefiner.EditSiteBusinessStatusEvent();
        event.setSiteId(siteId);
        event.setBusinessStatus(ObjectUtil.defaultIfNull(businessStatus, 0));
        event.setAuthority(true);
        EventAndSubscribeOfPublisher.publishAndCallbackBySite(event);
        clearSiteCache(siteId);
    }


}
