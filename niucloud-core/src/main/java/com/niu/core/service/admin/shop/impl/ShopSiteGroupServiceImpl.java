package com.niu.core.service.admin.shop.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.enums.addon.AddonActionEnum;
import com.niu.core.enums.addon.AddonStatusEnum;
import com.niu.core.enums.shop.ShopGroupCreateTypeEnum;
import com.niu.core.enums.sys.CacheTagEnum;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.site.SiteGroupMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.admin.shop.IShopSiteGroupService;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.param.SiteGroupAddParam;
import com.niu.core.service.admin.site.param.SiteGroupParam;
import com.niu.core.service.admin.site.param.SiteGroupSearchParam;
import com.niu.core.service.admin.site.vo.SiteGroupListVo;
import com.niu.core.service.admin.sys.ISysMenuService;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import com.niu.core.service.core.site.ICoreSiteService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 店铺分组（分组权限）实现（对齐 PHP ShopSiteGroupService）
 */
@Service
public class ShopSiteGroupServiceImpl implements IShopSiteGroupService {

    /**
     * 站点套餐tag
     */
    public static String cacheTagName = CacheTagEnum.SITE_GROUP_CACHE.getTagName();

    /**
     * 是否使用缓存
     */
    public static final boolean useCache = true;

    /**
     * 套餐菜单缓存前缀（对齐 PHP site_group_menu_ids）
     */
    public static final String cacheName = "site_group_menu_ids";

    @Resource
    private Cached cached;
    @Resource
    SiteGroupMapper siteGroupMapper;
    @Resource
    SiteMapper siteMapper;
    @Resource
    AddonMapper addonMapper;
    @Resource
    ICoreAddonService coreAddonService;

    ISiteService siteService;

    @Resource
    ICoreSiteService coreSiteService;
    @Resource
    ISysMenuService sysMenuService;

    @Resource
    private void setSiteService(@Lazy ISiteService siteService) {
        this.siteService = siteService;
    }

    /**
     * 店铺分组（分组权限）列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SiteGroupListVo>
     */
    @Override
    public PageResult<SiteGroupListVo> list(PageParam pageParam, SiteGroupSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<SiteGroup> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(searchParam.getKeywords())) {
            queryWrapper.like("group_name", searchParam.getKeywords());
        }
        queryWrapper.orderByDesc("create_time");
        IPage<SiteGroup> iPage = siteGroupMapper.selectPage(new Page<>(page, limit), queryWrapper);
        // 获取所有的 addon
        List<Addon> addonList = addonMapper.selectList(new QueryWrapper<Addon>().orderByDesc("id"));
        List<SiteGroupListVo> list = new LinkedList<>();
        for (SiteGroup item : iPage.getRecords()) {
            SiteGroupListVo vo = new SiteGroupListVo();
            vo.setGroupId(item.getGroupId());
            vo.setGroupName(item.getGroupName());
            vo.setGroupDesc(item.getGroupDesc());
            vo.setCreateTime(item.getCreateTime());
            vo.setCreateBy(item.getCreateBy());
            vo.setUpdateTime(item.getUpdateTime());
            vo.setCreateByName(ShopGroupCreateTypeEnum.getNameByStatus(item.getCreateBy()));
            JSONArray addonJson = JSONUtil.parseArray(item.getAddon());
            vo.setAddon(addonJson);
            JSONArray appJson = JSONUtil.parseArray(item.getApp());
            vo.setApp(appJson);
            List<String> addonStr = new ArrayList<>();
            List<String> appStr = new ArrayList<>();
            for (Addon addon : addonList) {
                if (addonJson.contains(addon.getKey())) {
                    addonStr.add(addon.getTitle());
                }
                if (appJson.contains(addon.getKey())) {
                    appStr.add(addon.getTitle());
                }
            }
            vo.setAddonName(addonStr);
            vo.setAppName(appStr);
            list.add(vo);
        }
        return PageResult.build(iPage.getCurrent(), iPage.getSize(), iPage.getTotal()).setData(list);
    }

    /**
     * 获取所有分组（对齐 PHP getAll，可选 app 过滤）
     */
    @Override
    public List<SiteGroup> getAll(String app) {
        QueryWrapper<SiteGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(
                "group_id", "group_name", "group_desc", "app", "addon",
                "create_time", "update_time", "create_by"
        );
        if (ObjectUtil.isNotEmpty(app)) {
            queryWrapper.eq("app", JSONUtil.toJsonStr(List.of(app)));
        }
        return siteGroupMapper.selectList(queryWrapper);
    }

    /**
     * C 端入驻可选套餐（对齐 PHP ShopSiteGroupService.getAll：group_id + group_name，create_time desc）
     */
    @Override
    public List<SiteGroup> getApplyList() {
        QueryWrapper<SiteGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("group_id", "group_name");
        queryWrapper.orderByDesc("create_time");
        return siteGroupMapper.selectList(queryWrapper);
    }

    /**
     * 店铺分组（分组权限）详情
     *
     * @param id 主键
     * @return SiteGroup
     */
    @Override
    public SiteGroup info(Integer id) {
        SiteGroup model = siteGroupMapper.selectOne(
                new QueryWrapper<SiteGroup>()
                        .eq("group_id", id)
                        .last("limit 1"));
        if (model == null) {
            throw new AdminException("套餐不存在");
        }
        return model;
    }

    /**
     * 店铺分组（分组权限）添加
     *
     * @param addParam 参数
     * @return 新建套餐 group_id（对齐 PHP add 返回值）
     */
    @Override
    public Integer add(SiteGroupAddParam addParam) {
        validateGroupNameUnique(addParam.getGroupName(), null);

        // 设置默认创建人类型
        if (addParam.getCreateBy() == null || addParam.getCreateBy().isEmpty()) {
            addParam.setCreateBy(ShopGroupCreateTypeEnum.USER.getStatus());
        }

        // 如果是用户创建且没有选择应用，则抛出异常
        if (ShopGroupCreateTypeEnum.USER.getStatus().equals(addParam.getCreateBy())
                && (addParam.getApp() == null || addParam.getApp().isEmpty())) {
            throw new AdminException("请选择应用");
        }

        // 获取已安装的应用列表
        List<String> appKeys = getInstallAppKeys();

        // 如果没有选择应用，则使用所有已安装的应用
        if (addParam.getApp() == null || addParam.getApp().isEmpty()) {
            addParam.setApp(JSONUtil.parseArray(appKeys));
        }

        // 验证应用和插件是否都是有效的已安装应用
        JSONArray groupRoles = new JSONArray();
        if (addParam.getAddon() != null) {
            groupRoles.addAll(addParam.getAddon());
        }
        groupRoles.addAll(addParam.getApp());
        checkAddon(groupRoles);

        // 创建站点分组
        SiteGroup model = new SiteGroup();
        model.setGroupName(addParam.getGroupName());
        model.setGroupDesc(addParam.getGroupDesc());
        model.setApp(JSONUtil.toJsonStr(addParam.getApp()));
        model.setAddon(JSONUtil.toJsonStr(ObjectUtil.defaultIfNull(addParam.getAddon(), new JSONArray())));
        model.setCreateBy(addParam.getCreateBy());
        model.setCreateTime(DateUtils.currTime());
        model.setUpdateTime(DateUtils.currTime());
        siteGroupMapper.insert(model);
        return model.getGroupId();
    }

    /**
     * 店铺分组（分组权限）编辑
     *
     * @param id        主键
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, SiteGroupParam editParam) {
        SiteGroup model = siteGroupMapper.selectOne(
                new QueryWrapper<SiteGroup>()
                        .eq("group_id", id)
                        .last("limit 1"));
        if (model == null) {
            throw new AdminException("套餐不存在");
        }

        validateGroupNameUnique(editParam.getGroupName(), id);

        // 用户创建套餐必须选择应用（对齐 PHP edit）
        JSONArray appArray = ObjectUtil.defaultIfNull(editParam.getApp(), new JSONArray());
        if (ShopGroupCreateTypeEnum.USER.getStatus().equals(model.getCreateBy()) && appArray.isEmpty()) {
            throw new AdminException("请选择应用");
        }

        // app 为空则回填全部已安装应用（对齐 PHP edit）
        List<String> appKeys = getInstallAppKeys();
        if (appArray.isEmpty()) {
            appArray = JSONUtil.parseArray(appKeys);
            editParam.setApp(appArray);
        }

        JSONArray groupRoles = new JSONArray();
        if (editParam.getAddon() != null) {
            groupRoles.addAll(editParam.getAddon());
        }
        groupRoles.addAll(editParam.getApp());
        // 判断应用是否全部是有效的已安装应用
        checkAddon(groupRoles);

        SiteGroup group = new SiteGroup();
        group.setGroupId(id);
        group.setGroupName(editParam.getGroupName());
        group.setGroupDesc(editParam.getGroupDesc());
        group.setApp(JSONUtil.toJsonStr(editParam.getApp()));
        group.setAddon(JSONUtil.toJsonStr(ObjectUtil.defaultIfNull(editParam.getAddon(), new JSONArray())));
        group.setUpdateTime(DateUtils.currTime());
        siteGroupMapper.updateById(group);
        // 删除缓存
        cached.remove(cacheName + id);

        // 同步更新站点支持应用（与 PHP 一致，当前注释不执行）
        List<Site> siteList = siteMapper.selectList(new QueryWrapper<Site>().eq("group_id", id));
        if (!siteList.isEmpty()) {
            for (Site site : siteList) {
                // 修改站点应用
                if (ObjectUtil.isNotEmpty(editParam.getSyncSite()) && editParam.getSyncSite() == 1) {
                    Site siteModel = new Site();
                    siteModel.setApp(JSONUtil.toJsonStr(ObjectUtil.defaultIfNull(editParam.getApp(), new JSONArray())));
                    siteModel.setAddons(JSONUtil.toJsonStr(ObjectUtil.defaultIfNull(editParam.getAddon(), new JSONArray())));
                    siteMapper.update(siteModel, new QueryWrapper<Site>().eq("group_id", id));
                }
                // 清除全部缓存
                sysMenuService.clear();
                coreSiteService.clearSiteCache(site.getSiteId());
            }
        }
    }

    /**
     * 店铺分组（分组权限）删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        long siteCount = siteMapper.selectCount(new QueryWrapper<Site>().eq("group_id", id));
        if (siteCount > 0) {
            throw new CommonException("当前套餐存在店铺，请调整店铺对应套餐后重试");
        }
        siteGroupMapper.delete(new QueryWrapper<SiteGroup>().eq("group_id", id));
        // 删除缓存
        cached.remove(cacheName + id);
    }

    /**
     * 校验套餐名称唯一（对齐 PHP ShopGroup 验证器 unique:site_group）
     */
    private void validateGroupNameUnique(String groupName, Integer excludeGroupId) {
        if (!StringUtils.hasText(groupName)) {
            return;
        }
        QueryWrapper<SiteGroup> queryWrapper = new QueryWrapper<SiteGroup>()
                .eq("group_name", groupName);
        if (excludeGroupId != null) {
            queryWrapper.ne("group_id", excludeGroupId);
        }
        if (siteGroupMapper.selectCount(queryWrapper) > 0) {
            throw new AdminException("店铺套餐名称必须是惟一的");
        }
    }

    /**
     * 检测插件/应用是否有效
     */
    private void checkAddon(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.isEmpty()) {
            return;
        }
        Map<String, InstallAddonListVo> addonListVoMap = coreAddonService.getInstallAddonList();
        List<String> keys = new ArrayList<>(addonListVoMap.keySet());
        List<String> addonString = JSONUtil.toList(jsonArray, String.class);
        keys.retainAll(addonString);
        if (keys.size() != addonString.size()) {
            throw new AdminException("存在无效的应用");
        }
    }

    /**
     * 获取已安装的应用 keys（对应 PHP 中的 getInstallAddonKeys(AddonDict::APP)）
     *
     * @return 应用 key 列表
     */
    private List<String> getInstallAppKeys() {
        List<Addon> apps = addonMapper.selectList(new QueryWrapper<Addon>()
                .eq("status", AddonStatusEnum.ON.getCode())
                .eq("type", AddonActionEnum.APP.getCode())
                .select("`key`"));
        return apps.stream().map(Addon::getKey).collect(Collectors.toList());
    }
}
