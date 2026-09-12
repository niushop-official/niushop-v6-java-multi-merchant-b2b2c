package com.niu.core.service.admin.shop.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.google.gson.JsonArray;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.shop.*;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteApply;
import com.niu.core.entity.site.SiteCategory;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.entity.site.SiteSettAccount;
import com.niu.core.enums.site.SiteSettAccountTypeEnum;
import com.niu.core.entity.sys.SysUser;
import com.niu.core.entity.sys.SysUserRole;
import com.niu.core.enums.addon.AddonActionEnum;
import com.niu.core.enums.shop.ShopStatusEnum;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.enums.sys.AppTypeEnum;
import com.niu.core.event.site.SiteAddAfterEvent;
import com.niu.core.event.site.SiteCloseAfterEvent;
import com.niu.core.event.site.SiteDelAfterEvent;
import com.niu.core.event.site.SiteEditAfterEvent;
import com.niu.core.event.site.SiteOpenAfterEvent;
import com.niu.core.mapper.shop.*;
import com.niu.core.mapper.site.SiteApplyMapper;
import com.niu.core.mapper.site.SiteCategoryMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.mapper.site.SiteSettAccountMapper;
import com.niu.core.mapper.sys.SysUserMapper;
import com.niu.core.mapper.sys.SysUserRoleMapper;
import com.niu.core.service.admin.addon.IAddonService;
import com.niu.core.service.admin.shop.IShopSiteGroupService;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.shop.ISiteShopService;
import com.niu.core.service.admin.shop.param.*;
import com.niu.core.service.admin.shop.vo.*;
import com.niu.core.service.admin.site.ISiteGroupService;
import com.niu.core.service.admin.site.param.SiteUserParam;
import com.niu.core.service.admin.site.vo.SiteAdminVo;
import com.niu.core.service.admin.sys.ISysMenuService;
import com.niu.core.service.admin.sys.ISysUserRoleService;
import com.niu.core.service.admin.sys.ISysUserService;
import com.niu.core.service.admin.sys.param.SysUserRoleParam;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.site.ICoreSiteCategoryService;
import com.niu.core.service.core.site.ICoreSiteService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 店铺扩展实现
 */
@Slf4j
@Service
public class SiteShopServiceImpl implements ISiteShopService {

    @Resource
    SiteMapper siteMapper;

    @Resource
    SiteShopMapper siteShopMapper;

    @Resource
    SiteCategoryMapper siteCategoryMapper;

    @Resource
    SiteApplyMapper siteApplyMapper;

    @Resource
    SiteSettAccountMapper siteSettAccountMapper;

    @Resource
    SysUserMapper sysUserMapper;

    @Resource
    SysUserRoleMapper sysUserRoleMapper;

    @Resource
    ICoreSiteService coreSiteService;

    @Resource
    IAddonService addonService;

    @Resource
    ISysMenuService sysMenuService;

    @Resource
    ISiteGroupService siteGroupService;

    @Resource
    ISysUserService sysUserService;

    @Resource
    ISysUserRoleService userRoleService;

    @Resource
    SiteShopAccountLogMapper siteShopAccountLogMapper;

    @Resource
    SiteShopCashOutMapper siteShopCashOutMapper;
    @Resource
    IShopSiteGroupService shopSiteGroupService;

    @Resource
    ISiteService siteService;

    @Resource
    SiteSupplyMapper siteSupplyMapper;

    @Resource
    SiteO2oMapper siteO2oMapper;

    @Resource
    private Cached cached;

    @Resource
    ICoreSiteCategoryService siteCategoryService;

    private static final String SITE_SHOP_JOIN_SELECT =
            "ns.site_id, ns.site_name, ns.group_id, ns.app_type, ns.logo, ns.`desc`, ns.status, ns.latitude, ns.longitude, ns.province_id, ns.city_id, ns.district_id, ns.address, ns.full_address, ns.phone, ns.business_hours, ns.business_state, ns.create_time, ns.expire_time, ns.front_end_name, ns.front_end_logo, ns.icon, ns.member_no, " +
            "ns.app, ns.addons, ns.initalled_addon, ns.site_domain, ns.isinit, ns.contact_name, ns.contact_mobile, ns.money, ns.money_get, ns.money_cash_outing, ns.business_license, nsg.group_name, " +
            "nss.id, nssc.name as category_name, nss.is_recommend, nss.is_self, nss.service_rate, nss.category_id, nss.circle_id, nss.child_category_id, nss.keywords, nss.shop_deposit, nss.follow_number, nss.business_status, nss.mall_up_num, nss.mall_sale_num, nss.quality_score, nss.service_score, nss.logistics_score, nss.evaluate_num, nss.background_h5, " +
            "nsp.service_rate as supply_service_rate, nsp.supply_sale_num, nsp.supply_up_num, nsp.supply_follow_num, nsp.supply_goods_num";

    @Override
    public PageResult<SiteShopListVo> page(PageParam pageParam, SiteShopSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<Site> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.select(SITE_SHOP_JOIN_SELECT)
                .setAlias("ns")
                .leftJoin("?_site_shop nss ON ns.site_id = nss.site_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_group nsg ON ns.group_id = nsg.group_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category nssc ON nss.category_id = nssc.category_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_supply nsp ON ns.site_id = nsp.site_id".replace("?_", GlobalConfig.tablePrefix));

        queryWrapper.ne("ns.app_type", "admin");
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "ns.create_time", searchParam.getCreateTime());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getExpireTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "ns.expire_time", searchParam.getExpireTime());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getGroupId()) && !"all".equals(String.valueOf(searchParam.getGroupId()))) {
            queryWrapper.eq("nsg.group_id", searchParam.getGroupId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getCategoryId()) && !"all".equals(String.valueOf(searchParam.getCategoryId()))) {
            queryWrapper.eq("nss.category_id", searchParam.getCategoryId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getStatus()) && !"all".equals(String.valueOf(searchParam.getStatus()))) {
            queryWrapper.eq("ns.status", searchParam.getStatus());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getIsSelf()) && !"all".equals(String.valueOf(searchParam.getIsSelf()))) {
            queryWrapper.eq("nss.is_self", searchParam.getIsSelf());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) {
            queryWrapper.and(i -> i.or(j -> j.like("ns.site_name", searchParam.getKeyword()))
                    .or(j -> j.like("nss.keywords", searchParam.getKeyword()))
            );
        }
        if (ObjectUtil.isNotEmpty(searchParam.getApp())) {
            queryWrapper.like("ns.app", searchParam.getApp());
        }
        queryWrapper.orderByDesc("ns.create_time");

        IPage<SiteShopListVo> iPage = siteMapper.selectJoinPage(new Page<>(page, limit), SiteShopListVo.class, queryWrapper);
        for (SiteShopListVo item : iPage.getRecords()) {
            MPJQueryWrapper<SysUserRole> userRoleMPJQueryWrapper = new MPJQueryWrapper<>();
            userRoleMPJQueryWrapper.select("nsu.uid, nsu.username, nsu.head_img, nsu.real_name, nsu.last_ip, nsu.last_time, nsu.create_time, nsu.login_count")
                    .setAlias("nsur")
                    .leftJoin("?_sys_user nsu ON nsur.uid = nsu.uid".replace("?_", GlobalConfig.tablePrefix));
            userRoleMPJQueryWrapper.eq("nsur.is_admin", 1);
            userRoleMPJQueryWrapper.eq("nsur.site_id", item.getSiteId());
            SiteAdminVo user = sysUserRoleMapper.selectJoinOne(SiteAdminVo.class, userRoleMPJQueryWrapper);
            if (ObjectUtil.isEmpty(user)) {
                user = new SiteAdminVo();
                user.setUsername("--");
            }
            item.setAdmin(user);
            // 设置应用名称
            if (ObjectUtil.isNotEmpty(item.getApp())) {
                String appName = addonService.getTitleListByKey(item.getApp());
                item.setAppName(appName);
            }
            // 格式化百分比字段
            formatPercentageFields(item);
            item.setStatusName(SiteStatusEnum.getNameByCode(item.getStatus()));
            item.setBusinessStatusName(ShopStatusEnum.getNameByKey(item.getBusinessStatus()));
            SiteShop shop = new SiteShop();
            BeanUtils.copyProperties(item, shop);
            item.setShop(shop);
        }
        return PageResult.build(iPage);
    }

    @Override
    public List<SiteShopListVo> list(SiteShopSearchParam searchParam) {

        MPJQueryWrapper<Site> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.select(SITE_SHOP_JOIN_SELECT)
                .setAlias("ns")
                .leftJoin("?_site_shop nss ON ns.site_id = nss.site_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_group nsg ON ns.group_id = nsg.group_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category nssc ON nss.category_id = nssc.category_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_supply nsp ON ns.site_id = nsp.site_id".replace("?_", GlobalConfig.tablePrefix));

        queryWrapper.ne("ns.app_type", "admin");
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "ns.create_time", searchParam.getCreateTime());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getExpireTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "ns.expire_time", searchParam.getExpireTime());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getGroupId()) && !"all".equals(String.valueOf(searchParam.getGroupId()))) {
            queryWrapper.eq("ns.group_id", searchParam.getGroupId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getCategoryId()) && !"all".equals(String.valueOf(searchParam.getCategoryId()))) {
            queryWrapper.eq("nss.category_id", searchParam.getCategoryId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getStatus()) && !"all".equals(String.valueOf(searchParam.getStatus()))) {
            queryWrapper.eq("ns.status", searchParam.getStatus());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getIsSelf()) && !"all".equals(String.valueOf(searchParam.getIsSelf()))) {
            queryWrapper.eq("nss.is_self", searchParam.getIsSelf());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) {
            queryWrapper.and(i -> i.or(j -> j.like("ns.site_name", searchParam.getKeyword()))
                    .or(j -> j.like("nss.keywords", searchParam.getKeyword()))
            );
        }
        queryWrapper.orderByDesc("ns.create_time");
        List<SiteShopListVo> list = siteMapper.selectJoinList(SiteShopListVo.class, queryWrapper);
        for (SiteShopListVo item : list) {
            MPJQueryWrapper<SysUserRole> userRoleMPJQueryWrapper = new MPJQueryWrapper<>();
            userRoleMPJQueryWrapper.select("nsu.uid, nsu.username, nsu.head_img, nsu.real_name, nsu.last_ip, nsu.last_time, nsu.create_time, nsu.login_count")
                    .setAlias("nsur")
                    .leftJoin("?_sys_user nsu ON nsur.uid = nsu.uid".replace("?_", GlobalConfig.tablePrefix));
            userRoleMPJQueryWrapper.eq("nsur.is_admin", 1);
            userRoleMPJQueryWrapper.eq("nsur.site_id", item.getSiteId());
            SiteAdminVo user = sysUserRoleMapper.selectJoinOne(SiteAdminVo.class, userRoleMPJQueryWrapper);
            if (ObjectUtil.isEmpty(user)) {
                user = new SiteAdminVo();
                user.setUsername("--");
            }
            item.setAdmin(user);
            item.setStatusName(SiteStatusEnum.getNameByCode(item.getStatus()));
            item.setBusinessStatusName(ShopStatusEnum.getNameByKey(item.getBusinessStatus()));
            SiteShop shop = new SiteShop();
            BeanUtils.copyProperties(item, shop);
            item.setShop(shop);
        }
        return list;

    }

    @Override
    public SiteShopInfoVo info(Integer id) {
        MPJQueryWrapper<Site> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.select(SITE_SHOP_JOIN_SELECT)
                .setAlias("ns")
                .leftJoin("?_site_shop nss ON ns.site_id = nss.site_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_group nsg ON ns.group_id = nsg.group_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category nssc ON nss.category_id = nssc.category_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_supply nsp ON ns.site_id = nsp.site_id".replace("?_", GlobalConfig.tablePrefix));

        queryWrapper.eq("ns.site_id", id);
        SiteShopInfoVo model = siteMapper.selectJoinOne(SiteShopInfoVo.class, queryWrapper);
        if (ObjectUtil.isNull(model)) {
            throw new AdminException("店铺扩展不存在");
        }
        if (model.getServiceRate() != null) {
            model.setCommissionRate(model.getServiceRate());
        }
        fillSettAccountFields(model, id);
        SiteShopInfoVo.SiteShop shop = new SiteShopInfoVo.SiteShop();
        BeanUtils.copyProperties(model, shop);
        if (model.getServiceRate() != null) {
            shop.setCommissionRate(model.getServiceRate());
        }
        model.setShop(shop);
        List<String> addonKeys = coreSiteService.getAddonKeysBySiteId(model.getSiteId());
        if (addonKeys.size() != 0) {
            model.setSiteAddons(addonService.getAddonListByKeys(addonKeys, AddonActionEnum.ADDON.getCode()));
        } else {
            model.setSiteAddons(new ArrayList<>());
        }
        BigDecimal moneyGet = ObjectUtil.defaultIfNull(model.getMoneyGet(), BigDecimal.ZERO);
        BigDecimal money = ObjectUtil.defaultIfNull(model.getMoney(), BigDecimal.ZERO);
        BigDecimal moneyCashOuting = ObjectUtil.defaultIfNull(model.getMoneyCashOuting(), BigDecimal.ZERO);
        model.setWithdrawalMoney(moneyGet.subtract(money).subtract(moneyCashOuting));
        if (model.getCategoryId() != null && model.getCategoryId() > 0) {
            model.setChildCategoryList(siteCategoryService.getChildList(model.getCategoryId()));
        }
        return model;
    }

    @Override
    @Transactional
    public void add(SiteShopParam addParam) {
        if ((ObjectUtil.isNull(addParam.getUid()) || addParam.getUid() == 0)
                && StringUtils.isNotBlank(addParam.getUsername())) {
            QueryWrapper<SysUser> userQuery = new QueryWrapper<>();
            userQuery.eq("username", addParam.getUsername());
            SysUser existingUser = sysUserMapper.selectOne(userQuery);
            if (ObjectUtil.isNotEmpty(existingUser)) {
                throw new AdminException("用户名重复");
            }
        }
        List<String> appList = new ArrayList<>();
        List<String> addonsList = new ArrayList<>();
        Integer groupId = addParam.getGroupId();

        // 注释说明：原逻辑需要前端传入 selectType 和 group_id，否则报错"请选择套餐"
        // 现参考 PHP 版本，改为通过 category_id 自动获取关联的套餐信息，前端无需传入 group_id
        /* 原代码开始
        if ("group".equals(addParam.getSelectType())) {
            if (ObjectUtil.isEmpty(addParam.getGroupId())) {
                throw new AdminException("请选择套餐");
            }
            SiteGroup siteGroup = siteGroupService.info(addParam.getGroupId());
            if (ObjectUtil.isNull(siteGroup) || ObjectUtil.isEmpty(siteGroup)) {
                throw new CommonException("店铺分类信息不存在");
            }
            appList = JSONUtil.toList(siteGroup.getApp(), String.class);
            addonsList = JSONUtil.toList(siteGroup.getAddon(), String.class);
        } else {
            appList = addParam.getAppList();
            if (ObjectUtil.isEmpty(appList)) {
                throw new AdminException("请选择应用");
            }
            addonsList = addParam.getAddonsList();
            groupId = 0;
        }
        原代码结束 */

        // 新代码：参考 PHP 版本 CoreSiteService.php 的 add 方法，通过 category_id 自动获取套餐信息
        SiteGroup siteGroup = siteCategoryService.getGroupInfoByCategoryId(addParam.getCategoryId());
        if (ObjectUtil.isNotNull(siteGroup)) {
            groupId = siteGroup.getGroupId();
            appList = JSONUtil.toList(siteGroup.getApp(), String.class);
            addonsList = JSONUtil.toList(siteGroup.getAddon(), String.class);
        }

        Site model = new Site();
        model.setSiteName(addParam.getSiteName());
        model.setGroupId(groupId);
        model.setAppType(AppTypeEnum.SITE.getName());
        model.setCreateTime(DateUtils.currTime());
        String expireTimeStr = addParam.getExpireTime();
        if (StringUtils.isBlank(expireTimeStr) || "0".equals(expireTimeStr)) {
            model.setExpireTime(0L);
        } else {
            model.setExpireTime(DateUtils.StringToTimestamp(expireTimeStr));
        }
        model.setApp(JSONUtil.toJsonStr(appList));
        model.setAddons(JSONUtil.toJsonStr(addonsList));
        model.setSiteDomain(addParam.getSiteDomain());
        SiteShop siteShop = new SiteShop();
        String contactMobile = null;
        String phone = null;
        if (ObjectUtil.isNotEmpty(addParam.getApplyId())) {
            SiteApply siteApply = siteApplyMapper.selectById(addParam.getApplyId());
            if (ObjectUtil.isEmpty(siteApply)) {
                throw new CommonException("店铺分类信息不存在");
            }
            String userMobile = siteApply.getUserMobile();
            contactMobile = userMobile;
            phone = userMobile;
        }
        contactMobile = addParam.getPhone();
        phone = addParam.getPhone();
        if (ObjectUtil.isNotEmpty(addParam.getPhone())) {
            model.setContactMobile(contactMobile);
            model.setPhone(phone);
        }
        siteMapper.insert(model);
        Integer siteId = model.getSiteId();

        siteShop.setSiteId(siteId);
        siteShop.setCategoryId(addParam.getCategoryId());
        siteShop.setIsSelf(addParam.getIsSelf());
        if (ObjectUtil.isNotEmpty(addParam.getBusinessLicense())) {
            model.setBusinessLicense(addParam.getBusinessLicense());
            siteMapper.updateById(model);
        }
        // 保存子分类id
        if (ObjectUtil.isNotEmpty(addParam.getChildCategoryId())) {
            siteShop.setChildCategoryId(addParam.getChildCategoryId());
        }
        // 保存商圈id
        if (ObjectUtil.isNotEmpty(addParam.getCircleId())) {
            siteShop.setCircleId(addParam.getCircleId());
        }
        //查询佣金比率
        SiteCategory siteCategory = siteCategoryMapper.selectById(addParam.getCategoryId());
        if (ObjectUtil.isEmpty(siteCategory)) {
            throw new AdminException("店铺分类信息不存在");
        }
        if (ObjectUtil.isNotNull(siteCategory.getServiceRate())) {
            siteShop.setServiceRate(siteCategory.getServiceRate());
        }
        siteShopMapper.insert(siteShop);

        // 更新店铺申请状态
        if (ObjectUtil.isNotEmpty(addParam.getApplyId())) {
            siteApplyMapper.update(null, Wrappers.<SiteApply>lambdaUpdate()
                    .eq(SiteApply::getApplyId, addParam.getApplyId())
                    .set(SiteApply::getOpenStatus, 2)
                    .set(SiteApply::getSiteId, siteId));
        }
        if (ObjectUtil.isNull(addParam.getUid()) || addParam.getUid() == 0) {
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
        SiteGroup eventSiteGroup = siteGroupService.info(groupId);
        event.setSiteGroup(eventSiteGroup);
        event.setMainApp(appList);
        event.setSiteAddons(addonsList);
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    @Override
    @Transactional
    public void edit(Integer id, SiteShopInfoParam editParam) {
        MPJQueryWrapper<Site> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.select("site.*, nsg.group_id as site_group_group_id, nsg.group_name as site_group_group_name, nsg.app as site_group_app, nsg.addon as site_group_addon")
                .setAlias("site")
                .eq("site.site_id", id)
                .leftJoin("?_site_group nsg ON site.group_id = nsg.group_id".replace("?_", GlobalConfig.tablePrefix));

        Site site = siteMapper.selectJoinOne(Site.class, queryWrapper);
        if (ObjectUtil.isNull(site)) {
            throw new AdminException("店铺不存在");
        }

        if ("group".equals(editParam.getSelectType())) {
            if (ObjectUtil.isEmpty(editParam.getGroupId())) {
                throw new AdminException("请选择套餐");
            }
            if (!editParam.getGroupId().equals(site.getGroupId())) {
                SiteGroup siteGroup = siteGroupService.info(editParam.getGroupId());
                if (ObjectUtil.isNull(siteGroup) || ObjectUtil.isEmpty(siteGroup)) {
                    throw new AdminException("套餐不存在");
                }
                // 从分组获取app和addons，直接设置为Object类型
                if (ObjectUtil.isNotEmpty(siteGroup.getApp())) {
                    try {
                        JSONArray appArray = JSONUtil.parseArray(siteGroup.getApp());
                        editParam.setApp(appArray.toList(String.class));
                    } catch (Exception e) {
                        editParam.setApp(siteGroup.getApp());
                    }
                }

                if (ObjectUtil.isNotEmpty(siteGroup.getAddon())) {
                    try {
                        JSONArray addonArray = JSONUtil.parseArray(siteGroup.getAddon());
                        editParam.setAddons(addonArray.toList(String.class));
                    } catch (Exception e) {
                        editParam.setAddons(siteGroup.getAddon());
                    }
                }
            }
        } else {
            List<String> appList = editParam.getAppList();
            if (ObjectUtil.isEmpty(appList) || appList.isEmpty()) {
                throw new AdminException("APP_NOT_SELECT");
            }
            editParam.setGroupId(0);
        }
        // 查询店铺分类的手续费比率
        SiteCategory shopCategory = siteCategoryMapper.selectById(editParam.getCategoryId());
        if (ObjectUtil.isNull(shopCategory) || ObjectUtil.isEmpty(shopCategory)) {
            throw new AdminException("SHOP_CATEGORY_NOT_EXIST");
        }
        try {
            // 获取旧的站点组信息
            SiteGroup oldSiteGroup = null;
            if (site.getGroupId() != null && site.getGroupId() > 0) {
                oldSiteGroup = siteGroupService.info(site.getGroupId());
            }

            // 如果initalled_addon为空，合并app和addon
            if (ObjectUtil.isEmpty(site.getInitalledAddon())) {
                List<String> mergedList = new ArrayList<>();
                if (oldSiteGroup != null) {
                    // 处理app数据
                    if (ObjectUtil.isNotEmpty(oldSiteGroup.getApp())) {
                        try {
                            JSONArray appArray = JSONUtil.parseArray(oldSiteGroup.getApp());
                            for (Object item : appArray) {
                                mergedList.add(item.toString());
                            }
                        } catch (Exception e) {
                            mergedList.add(oldSiteGroup.getApp());
                        }
                    }
                    // 处理addon数据
                    if (ObjectUtil.isNotEmpty(oldSiteGroup.getAddon())) {
                        try {
                            JSONArray addonArray = JSONUtil.parseArray(oldSiteGroup.getAddon());
                            for (Object item : addonArray) {
                                mergedList.add(item.toString());
                            }
                        } catch (Exception e) {
                            mergedList.add(oldSiteGroup.getAddon());
                        }
                    }
                }
                site.setInitalledAddon(JSONUtil.toJsonStr(mergedList));
            }

            // 获取当前已安装的插件列表
            List<String> currentInitalled = new ArrayList<>();
            if (ObjectUtil.isNotEmpty(site.getInitalledAddon())) {
                try {
                    JSONArray currentArray = JSONUtil.parseArray(site.getInitalledAddon());
                    for (Object item : currentArray) {
                        currentInitalled.add(item.toString());
                    }
                } catch (Exception e) {
                    currentInitalled = new ArrayList<>();
                }
            }

            // 计算新增的应用
            List<String> mainApp = new ArrayList<>();
            List<String> newAppList = editParam.getAppList();
            if (!newAppList.isEmpty()) {

                for (String app : newAppList) {
                    if (ObjectUtil.isNotEmpty(app) && !currentInitalled.contains(app)) {
                        mainApp.add(app);
                    }
                }
            }
            // 计算新增的插件
            List<String> siteAddons = new ArrayList<>();
            List<String> newAddonsList = editParam.getAddonsList();
            if (!newAddonsList.isEmpty()) {

                for (String addon : newAddonsList) {
                    if (ObjectUtil.isNotEmpty(addon) && !currentInitalled.contains(addon)) {
                        siteAddons.add(addon);
                    }
                }
            }

            // 触发站点编辑后事件
            if (!mainApp.isEmpty() || !siteAddons.isEmpty()) {
                SiteEditAfterEvent event = new SiteEditAfterEvent();
                event.setSiteId(id);
                event.setName("SiteEditAfterEvent");
                event.setSite(site);
                event.setSiteGroup(oldSiteGroup);
                EventAndSubscribeOfPublisher.publishAll(event);
            }

            // 更新initalled_addon
            List<String> allAddons = new ArrayList<>(currentInitalled);
            allAddons.addAll(editParam.getAppList());
            allAddons.addAll(editParam.getAddonsList());

            // 去重并设置已安装插件
            Set<String> uniqueSet = new HashSet<>();
            List<String> uniqueList = new ArrayList<>();
            for (String item : allAddons) {
                if (ObjectUtil.isNotEmpty(item) && !uniqueSet.contains(item)) {
                    uniqueSet.add(item);
                    uniqueList.add(item);
                }
            }
            site.setInitalledAddon(JSONUtil.toJsonStr(uniqueList));

            // 更新店铺信息
            SiteShop dataShop = new SiteShop();
            dataShop.setIsSelf(editParam.getIsSelf());
            dataShop.setCategoryId(editParam.getCategoryId());
            if (ObjectUtil.isNotEmpty(editParam.getBusinessLicense())) {
                site.setBusinessLicense(editParam.getBusinessLicense());
            }
            // 保存子分类id
            if (ObjectUtil.isNotEmpty(editParam.getChildCategoryId())) {
                dataShop.setChildCategoryId(editParam.getChildCategoryId());
            }
            // 保存商圈id
            if (ObjectUtil.isNotEmpty(editParam.getCircleId())) {
                dataShop.setCircleId(editParam.getCircleId());
            }
            if (ObjectUtil.isNotNull(shopCategory.getServiceRate())) {
                dataShop.setServiceRate(shopCategory.getServiceRate());
            }
            // 更新店铺扩展信息
            if (ObjectUtil.isNotEmpty(dataShop)) {
                siteShopMapper.update(dataShop, new QueryWrapper<SiteShop>().eq("site_id", id));
            }

            // 处理过期时间状态
            if (ObjectUtil.isNotEmpty(editParam.getExpireTime())) {
                try {
                    long expireTime = Long.parseLong(editParam.getExpireTime());
                    if (expireTime > DateUtils.currTime()) {
                        site.setStatus(SiteStatusEnum.ON.getCode());
                    }
                } catch (NumberFormatException e) {
                    // 忽略无效的过期时间
                }
            }

            // 更新站点基本信息
            site.setSiteName(editParam.getSiteName());
            site.setGroupId(editParam.getGroupId());

            // 处理 app 和 addons 的存储格式
            String normApp = JSONUtil.toJsonStr(editParam.getAppList());
            if (ObjectUtil.isEmpty(normApp) || "{}".equals(normApp.trim())) {
                normApp = "";
            }

            String normAddons = JSONUtil.toJsonStr(editParam.getAddonsList());
            if (ObjectUtil.isEmpty(normAddons) || "{}".equals(normAddons.trim())) {
                normAddons = "";
            }

            site.setApp(normApp);
            site.setAddons(normAddons);
            site.setSiteDomain(editParam.getSiteDomain());
            site.setPhone(editParam.getPhone());
            site.setProvinceId(editParam.getProvinceId());
            site.setCityId(editParam.getCityId());
            site.setDistrictId(editParam.getDistrictId());
            site.setAddress(editParam.getAddress());
            site.setFullAddress(editParam.getFullAddress());
            site.setLatitude(editParam.getLatitude());
            site.setLongitude(editParam.getLongitude());

            if (editParam.getExpireTime() != null) {
                try {
                    site.setExpireTime(Long.valueOf(editParam.getExpireTime()));
                } catch (NumberFormatException e) {
                    // 忽略无效的过期时间
                }
            }

            siteMapper.updateById(site);

            // 清除缓存
            coreSiteService.clearSiteCache(id);

        } catch (Exception e) {
            throw new AdminException(e.getMessage() + " " + e.getStackTrace()[0].getFileName() + ":" + e.getStackTrace()[0].getLineNumber());
        }
    }


    @Override
    public void editBasicInfo(Integer id, SiteShopBasicInfoParam editParam) {
        if (id == null || id <= 0) {
            throw new AdminException("站点ID无效，请检查登录状态");
        }

        Site model = siteMapper.selectOne(
                new QueryWrapper<Site>()
                        .eq("site_id", id)
                        .last("limit 1"));

        if (ObjectUtil.isNull(model)) {
            throw new AdminException("站点不存在，site_id: " + id);
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

        SiteGroup siteGroup = siteGroupService.info(model.getGroupId());

        SiteShop siteShop = siteShopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", model.getSiteId()).last("Limit 1"));
        if (ObjectUtil.isNotEmpty(siteShop)) {
            siteShop.setBusinessStatus(editParam.getBusinessStatus());
            siteShop.setBackgroundH5(editParam.getBackgroundH5());
            siteShop.setKeywords(editParam.getKeywords());
            siteShopMapper.updateById(siteShop);
        }

        SiteEditAfterEvent event = new SiteEditAfterEvent();
        event.setSiteId(model.getSiteId());
        event.addAppSign("core");
        event.setName("SiteEditAfterEvent");
        event.setSite(model);
        event.setSiteGroup(siteGroup);
        EventAndSubscribeOfPublisher.publishAll(event);

        coreSiteService.clearSiteCache(id);
    }

    @Override
    public void del(Integer id) {
        siteService.del(id);
        Map<String, Object> logData = new HashMap<>();
        logData.put("site_id", id);
        SiteDelAfterEvent event = new SiteDelAfterEvent(
                this,
                id,
                "app_shop_del",
                "system",
                RequestUtils.uid(),
                JSONUtil.toJsonStr(logData)
        );
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    @Override
    public void closeShop(Integer siteId) {
        Site site = siteMapper.selectById(siteId);
        if (ObjectUtil.isNull(site)) {
            throw new AdminException("店铺不存在");
        }
        if (SiteStatusEnum.EXPIRE.getCode().equals(site.getStatus())) {
            throw new AdminException("店铺已过期");
        }
        if (SiteStatusEnum.CLOSE.getCode().equals(site.getStatus())) {
            throw new AdminException("店铺已停止");
        }
        Site model = new Site();
        model.setSiteId(siteId);
        model.setStatus(SiteStatusEnum.CLOSE.getCode());
        siteMapper.updateById(model);
        coreSiteService.clearSiteCache(siteId);

        Map<String, Object> logData = new HashMap<>();
        logData.put("site_id", siteId);
        SiteCloseAfterEvent event = new SiteCloseAfterEvent(
                this,
                siteId,
                "app_shop_close",
                "system",
                RequestUtils.uid(),
                JSONUtil.toJsonStr(logData)
        );
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    @Override
    public PageResult<SiteShopAccountLogListVo> getShopAccountLog(PageParam pageParam, Integer siteId) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<SiteShopAccountLog> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("site_id", siteId);
        queryWrapper.orderByDesc("id");

        IPage<SiteShopAccountLog> iPage = siteShopAccountLogMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<SiteShopAccountLogListVo> list = new LinkedList<>();
        for (SiteShopAccountLog item : iPage.getRecords()) {
            SiteShopAccountLogListVo vo = new SiteShopAccountLogListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public PageResult<SiteShopCashOutListVo> getShopCashOutlog(PageParam pageParam, Integer siteId) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();


        MPJQueryWrapper<SiteShopCashOut> queryWrapper = new MPJQueryWrapper<>();

        //sql语句
        queryWrapper.select("nssco.id, nssco.site_id, nssco.cash_out_no, nssco.account_type, nssco.transfer_type, nssco.transfer_realname, nssco.transfer_mobile, nssco.transfer_bank, nssco.transfer_account, nssco.transfer_payee, nssco.transfer_payment_code, nssco.transfer_fail_reason, nssco.transfer_status, nssco.transfer_time, nssco.apply_money, nssco.rate, nssco.service_money, nssco.money, nssco.audit_time, nssco.status, nssco.remark, nssco.create_time, nssco.refuse_reason, nssco.update_time, nssco.transfer_no, nssco.transfer_voucher, nssco.cancel_time, nssco.final_transfer_type, ns.site_name, ns.logo, ns.icon, ns.front_end_name, ns.front_end_logo")
                .setAlias("nssco")
                .leftJoin("?_site ns ON nssco.site_id = ns.site_id".replace("?_", GlobalConfig.tablePrefix));

        queryWrapper.eq("nssco.site_id", siteId);

        queryWrapper.orderByDesc("nssco.create_time");

        IPage<SiteShopCashOutListVo> iPage = siteShopCashOutMapper.selectJoinPage(new Page<>(page, limit), SiteShopCashOutListVo.class, queryWrapper);
        for (SiteShopCashOutListVo item : iPage.getRecords()) {
            SiteShopCashOutListVo.SiteInfo site = new SiteShopCashOutListVo.SiteInfo();
            site.setSiteId(item.getSiteId());
            site.setSiteName(item.getSiteName());
            site.setIcon(item.getIcon());
            site.setFrontEndName(item.getFrontEndName());
            site.setFrontEndLogo(item.getFrontEndLogo());
            item.setSiteInfo(site);
        }
        return PageResult.build(iPage);
    }

    @Override
    public SiteShopInShopVo getShopAccountInfo(Integer siteId) {
        Site site = siteMapper.selectById(siteId);
        if (ObjectUtil.isEmpty(site)) {
            throw new AdminException("店铺信息不存在");
        }

        SiteShopInShopVo vo = new SiteShopInShopVo();
        vo.setSiteId(siteId);

        SiteShop shop = siteShopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", siteId));
        if (ObjectUtil.isNotEmpty(shop)) {
            BeanUtils.copyProperties(shop, vo);
            vo.setShopDeposit(shop.getShopDeposit() != null ? shop.getShopDeposit().setScale(2, RoundingMode.HALF_UP).toString() : "0.00");
            if (shop.getServiceRate() != null) {
                vo.setCommissionRate(shop.getServiceRate());
            }
        } else {
            SiteSupply supply = siteSupplyMapper.selectOne(new QueryWrapper<SiteSupply>().eq("site_id", siteId));
            if (ObjectUtil.isNotEmpty(supply)) {
                vo.setCategoryId(supply.getCategoryId());
                if (supply.getServiceRate() != null) {
                    vo.setCommissionRate(supply.getServiceRate());
                    vo.setSupplyServiceRate(supply.getServiceRate().toPlainString());
                }
            } else {
                SiteO2o o2oShop = siteO2oMapper.selectOne(new QueryWrapper<SiteO2o>().eq("site_id", siteId));
                if (ObjectUtil.isNotEmpty(o2oShop)) {
                    vo.setCategoryId(o2oShop.getCategoryId());
                    if (o2oShop.getServiceRate() != null) {
                        vo.setCommissionRate(o2oShop.getServiceRate());
                        vo.setSupplyServiceRate(o2oShop.getServiceRate().toPlainString());
                    }
                }
            }
            vo.setShopDeposit("0.00");
        }

        BigDecimal money = site.getMoney() != null ? site.getMoney() : BigDecimal.ZERO;
        BigDecimal moneyGet = site.getMoneyGet() != null ? site.getMoneyGet() : BigDecimal.ZERO;
        BigDecimal moneyCashOuting = site.getMoneyCashOuting() != null ? site.getMoneyCashOuting() : BigDecimal.ZERO;
        vo.setMoney(money.setScale(2, RoundingMode.HALF_UP).toString());
        vo.setMoneyGet(moneyGet.setScale(2, RoundingMode.HALF_UP).toString());
        vo.setMoneyCashOuting(moneyCashOuting.setScale(2, RoundingMode.HALF_UP).toString());
        vo.setWithdrawalMoney((moneyGet.subtract(money).subtract(moneyCashOuting)).setScale(2, RoundingMode.HALF_UP).toString());
        vo.setBusinessLicense(site.getBusinessLicense());
        return vo;
    }

    @Override
    @Transactional
    public void editShopAccount(Integer siteId, SiteShopAccountInfoParam param) {
        if (ObjectUtil.isEmpty(param) || ObjectUtil.isEmpty(param.getBankType())) {
            throw new AdminException("请选择账户类型");
        }
        upsertSettAccount(siteId, param);
    }

    private void upsertSettAccount(Integer siteId, SiteShopAccountInfoParam param) {
        SiteSettAccount account = siteSettAccountMapper.selectOne(new QueryWrapper<SiteSettAccount>()
                .eq("main_id", siteId)
                .eq("main_type", "site")
                .eq("type", param.getBankType())
                .last("limit 1"));
        if (account == null) {
            account = new SiteSettAccount();
            account.setMainId(siteId);
            account.setMainType("site");
            account.setType(param.getBankType());
            account.setCreateTime(System.currentTimeMillis() / 1000);
        }
        applySettAccountParam(account, param);
        account.setUpdateTime(System.currentTimeMillis() / 1000);
        if (account.getId() == null) {
            siteSettAccountMapper.insert(account);
        } else {
            siteSettAccountMapper.updateById(account);
        }
    }

    private void applySettAccountParam(SiteSettAccount account, SiteShopAccountInfoParam param) {
        Integer type = param.getBankType();
        if (SiteSettAccountTypeEnum.BANK.getType().equals(type)) {
            account.setName(param.getBankAccountName());
            account.setAccountNo(param.getBankAccountNo());
            account.setBankName(param.getBankName());
            account.setBankAddress(param.getBankAddress());
            account.setAccountImg("");
        } else if (SiteSettAccountTypeEnum.ALIPAY.getType().equals(type)) {
            account.setName(param.getAlipayName());
            account.setAccountNo(param.getAlipayAccountNo());
            account.setAccountImg(param.getAlipayPaymentCode());
            account.setBankName("");
            account.setBankAddress("");
        } else if (SiteSettAccountTypeEnum.WECHAT.getType().equals(type)) {
            account.setName(param.getWechatName());
            account.setAccountNo(param.getWechatAccountNo());
            account.setAccountImg(param.getWechatPaymentCode());
            account.setBankName("");
            account.setBankAddress("");
        }
    }

    private void fillSettAccountFields(SiteShopInfoVo vo, Integer siteId) {
        List<SiteSettAccount> accounts = siteSettAccountMapper.selectList(new QueryWrapper<SiteSettAccount>()
                .eq("main_id", siteId)
                .eq("main_type", "site"));
        for (SiteSettAccount account : accounts) {
            if (SiteSettAccountTypeEnum.BANK.getType().equals(account.getType())) {
                vo.setBankType(account.getType());
                vo.setBankAccountName(account.getName());
                vo.setBankAccountNo(account.getAccountNo());
                vo.setBankName(account.getBankName());
                vo.setBankAddress(account.getBankAddress());
            } else if (SiteSettAccountTypeEnum.ALIPAY.getType().equals(account.getType())) {
                vo.setAlipayName(account.getName());
                vo.setAlipayAccountNo(account.getAccountNo());
                vo.setAlipayPaymentCode(account.getAccountImg());
            } else if (SiteSettAccountTypeEnum.WECHAT.getType().equals(account.getType())) {
                vo.setWechatName(account.getName());
                vo.setWechatAccountNo(account.getAccountNo());
                vo.setWechatPaymentCode(account.getAccountImg());
            }
        }
    }

    @Override
    public SiteShopSelectPage getSelectPage(PageParam pageParam, SiteShopSelectParam siteShopSelectParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();


        siteShopSelectParam.setStatus(SiteStatusEnum.ON.getCode());
        List<Integer> verifySiteIds = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(siteShopSelectParam.getVerifySiteIds())) {
            List<Integer> verifySiteIdList = Arrays.asList(siteShopSelectParam.getVerifySiteIds());
            if (!verifySiteIdList.isEmpty()) {
                MPJQueryWrapper<SiteShop> verifyWrapper = new MPJQueryWrapper<>();
                verifyWrapper.setAlias("nss")
                        .select("ns.site_id")
                        .leftJoin("?_site ns ON nss.site_id = ns.site_id".replace("?_", GlobalConfig.tablePrefix))
                        .in("ns.site_id", verifySiteIdList)
                        .eq("ns.status", SiteStatusEnum.ON.getCode())
                        .eq("nss.business_status", ShopStatusEnum.ON.getCode());
                verifySiteIds = siteShopMapper.selectJoinList(Integer.class, verifyWrapper);
            }
        }

        MPJQueryWrapper<Site> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.select("ns.site_id, ns.site_name, ns.logo, ns.status, ns.create_time, ns.expire_time, ns.group_id, nss.is_recommend, nss.is_self, nss.category_id,nssc.name as category_name")
                .setAlias("ns")
                .leftJoin("?_site_shop nss ON ns.site_id = nss.site_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_group nsg ON ns.group_id = nsg.group_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category nssc ON nss.category_id = nssc.category_id".replace("?_", GlobalConfig.tablePrefix));

        queryWrapper.ne("ns.app_type", "admin");
        queryWrapper.like("ns.app", "\"mall\"");
        queryWrapper.eq("ns.status", SiteStatusEnum.ON.getCode());
        queryWrapper.eq("nss.business_status", ShopStatusEnum.ON.getCode());

        if (ObjectUtil.isNotEmpty(siteShopSelectParam.getSiteIds())) {
            if (siteShopSelectParam.getSiteIds() instanceof Integer) {
                queryWrapper.eq("ns.site_id", siteShopSelectParam.getSiteIds());
            } else if (siteShopSelectParam.getSiteIds() instanceof ArrayList) {
                queryWrapper.in("ns.site_id", (ArrayList<?>) siteShopSelectParam.getSiteIds());
            }
        }

        if (ObjectUtil.isNotEmpty(siteShopSelectParam.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "ns.create_time", siteShopSelectParam.getCreateTime());
        }

        if (ObjectUtil.isNotEmpty(siteShopSelectParam.getExpireTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "ns.expire_time", siteShopSelectParam.getExpireTime());
        }

        if (ObjectUtil.isNotEmpty(siteShopSelectParam.getGroupId()) && !"all".equals(String.valueOf(siteShopSelectParam.getGroupId()))) {
            queryWrapper.eq("ns.group_id", siteShopSelectParam.getGroupId());
        }

        if (ObjectUtil.isNotEmpty(siteShopSelectParam.getCategoryId()) && !"all".equals(String.valueOf(siteShopSelectParam.getCategoryId()))) {
            queryWrapper.eq("nss.category_id", siteShopSelectParam.getCategoryId());
        }

        if (ObjectUtil.isNotEmpty(siteShopSelectParam.getIsSelf()) && !"all".equals(String.valueOf(siteShopSelectParam.getIsSelf()))) {
            queryWrapper.eq("nss.is_self", siteShopSelectParam.getIsSelf());
        }

        if (ObjectUtil.isNotEmpty(siteShopSelectParam.getKeyword())) {
            queryWrapper.and(i -> i.or(j -> j.like("ns.site_name", siteShopSelectParam.getKeyword()))
                    .or(j -> j.like("nss.keywords", siteShopSelectParam.getKeyword())));
        }

        queryWrapper.orderByDesc("ns.create_time");

        IPage<SiteShopListVo> iPage = siteMapper.selectJoinPage(new Page<>(page, limit), SiteShopListVo.class, queryWrapper);

        for (SiteShopListVo item : iPage.getRecords()) {
            item.setStatusName(SiteStatusEnum.getNameByCode(item.getStatus()));
        }

        PageResult<SiteShopListVo> pageRes = PageResult.build(iPage);
        SiteShopSelectPage vo = new SiteShopSelectPage();
        BeanUtils.copyProperties(pageRes, vo);
        vo.setVerifySiteIds(verifySiteIds);
        return vo;
    }


    @Override
    public Map<String, BigDecimal> getShopStatMoney() {

        QueryWrapper<Site> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("SUM(money) AS money, SUM(money_get) AS money_get, SUM(money_cash_outing) AS money_cash_outing");
        queryWrapper.ne("site_id", 0);
        List<Map<String, Object>> shopStatMap = siteMapper.selectMaps(queryWrapper);
        Map<String, Object> map = ObjectUtil.defaultIfNull(shopStatMap.get(0), new HashMap<>());
        Map<String, BigDecimal> statMap = new HashMap<>();

        statMap.put("shop_balance", ObjectUtil.isNotEmpty(map.get("money")) ? new BigDecimal(map.get("money").toString()) : BigDecimal.ZERO);
        statMap.put("shop_settlement", ObjectUtil.isNotEmpty(map.get("money_get")) ? new BigDecimal(map.get("money_get").toString()) : BigDecimal.ZERO);
        statMap.put("shop_cash_out", ObjectUtil.isNotEmpty(map.get("money_cash_outing")) ? new BigDecimal(map.get("money_cash_outing").toString()) : BigDecimal.ZERO);
        return statMap;
    }

    @Override
    public Map<String, BigDecimal> getAdminStatMoney() {
        QueryWrapper<Site> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("SUM(money_get) AS money_get");
        queryWrapper.eq("site_id", 0);
        List<Map<String, Object>> shopStatMap = siteMapper.selectMaps(queryWrapper);
        Map<String, Object> map = ObjectUtil.defaultIfNull(shopStatMap.get(0), new HashMap<>());
        Map<String, BigDecimal> statMap = new HashMap<>();

        statMap.put("admin_settlement", ObjectUtil.isNotEmpty(map.get("money_get")) ? new BigDecimal(map.get("money_get").toString()) : BigDecimal.ZERO);
        return statMap;
    }

    @Override
    public void openShop(Integer siteId) {
        Site site = siteMapper.selectById(siteId);
        if (ObjectUtil.isNull(site)) {
            throw new AdminException("店铺不存在");
        }
        if (SiteStatusEnum.EXPIRE.getCode().equals(site.getStatus())) {
            throw new AdminException("店铺已过期");
        }
        if (SiteStatusEnum.ON.getCode().equals(site.getStatus())) {
            throw new AdminException("店铺已开启");
        }
        Site model = new Site();
        model.setSiteId(siteId);
        model.setStatus(SiteStatusEnum.ON.getCode());
        siteMapper.updateById(model);
        coreSiteService.clearSiteCache(siteId);

        Map<String, Object> logData = new HashMap<>();
        logData.put("site_id", siteId);
        SiteOpenAfterEvent event = new SiteOpenAfterEvent(
                this,
                siteId,
                "app_shop_open",
                "system",
                RequestUtils.uid(),
                JSONUtil.toJsonStr(logData)
        );
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    /**
     * 检查店铺收款账户信息是否已经完成配置
     *
     * @return boolean true：已完成配置，false：未完成配置
     */
    @Override
    public boolean check() {
        SiteShop shopInfo = siteShopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", RequestUtils.siteId()));
        return ObjectUtil.isNotEmpty(shopInfo);
    }

    /**
     * 格式化百分比字段
     *
     * @param item 店铺列表项
     */
    private void formatPercentageFields(SiteShopListVo item) {
        if (item.getServiceRate() == null) {
            item.setServiceRate(new BigDecimal("0.00"));
        }
        item.setCommissionRate(item.getServiceRate());
    }

}
