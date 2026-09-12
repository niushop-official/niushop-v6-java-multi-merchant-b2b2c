package com.niu.core.service.admin.site.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.entity.sys.SysUserRole;
import com.niu.core.enums.sys.CacheTagEnum;
import com.niu.core.mapper.site.SiteGroupMapper;
import com.niu.core.mapper.sys.SysUserRoleMapper;
import com.niu.core.service.admin.shop.IShopSiteGroupService;
import com.niu.core.service.admin.site.ISiteGroupService;
import com.niu.core.service.admin.site.param.SiteGroupAddParam;
import com.niu.core.service.admin.site.param.SiteGroupParam;
import com.niu.core.service.admin.site.param.SiteGroupSearchParam;
import com.niu.core.service.admin.site.vo.SiteGroupListVo;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 店铺分组（分组权限）实现
 * <p>CRUD 委托 {@link ShopSiteGroupServiceImpl}，与 PHP ShopSiteGroupService 对齐</p>
 */
@Service
public class SiteGroupServiceImpl implements ISiteGroupService {

    /**
     * 站点套餐 tag
     */
    public static String cacheTagName = CacheTagEnum.SITE_GROUP_CACHE.getTagName();

    /**
     * 是否使用缓存
     */
    public static final boolean useCache = true;

    @Resource
    private IShopSiteGroupService shopSiteGroupService;

    @Resource
    SiteGroupMapper siteGroupMapper;

    @Resource
    SysUserRoleMapper sysUserRoleMapper;

    @Resource
    ICoreAddonService coreAddonService;

    /**
     * 店铺分组（分组权限）列表
     */
    @Override
    public PageResult<SiteGroupListVo> list(PageParam pageParam, SiteGroupSearchParam searchParam) {
        return shopSiteGroupService.list(pageParam, searchParam);
    }

    /**
     * 获取所有站点分组
     */
    @Override
    public List<SiteGroup> getAll(String app) {
        return shopSiteGroupService.getAll(app);
    }

    /**
     * C 端入驻可选套餐列表
     */
    @Override
    public List<SiteGroup> getApplyList() {
        return shopSiteGroupService.getApplyList();
    }

    /**
     * 店铺分组（分组权限）详情
     */
    @Override
    public SiteGroup info(Integer id) {
        return shopSiteGroupService.info(id);
    }

    /**
     * 店铺分组（分组权限）添加
     */
    @Override
    public Integer add(SiteGroupAddParam addParam) {
        return shopSiteGroupService.add(addParam);
    }

    /**
     * 店铺分组（分组权限）编辑
     */
    @Override
    public void edit(Integer id, SiteGroupParam editParam) {
        shopSiteGroupService.edit(id, editParam);
    }

    /**
     * 店铺分组（分组权限）删除
     */
    @Override
    public void del(Integer id) {
        shopSiteGroupService.del(id);
    }

    /**
     * 检测插件是否有效
     */
    @Override
    public void checkAddon(JSONArray jsonArray) {
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
     * 查询店铺套餐以及用户创建的站点数量
     *
     * @param uid 用户 id
     */
    @Override
    public List<SiteGroupListVo> getUserSiteGroupAll(Integer uid) {
        List<SiteGroupListVo> siteGroupListVoList = new ArrayList<>();
        List<SiteGroup> siteGroupList = siteGroupMapper.selectList(new QueryWrapper<>());
        for (SiteGroup siteGroup : siteGroupList) {
            SiteGroupListVo siteGroupListVo = new SiteGroupListVo();
            BeanUtils.copyProperties(siteGroup, siteGroupListVo);
            siteGroupListVo.setSiteNum(getUserSiteGroupSiteNum(uid, siteGroup.getGroupId()));
            siteGroupListVoList.add(siteGroupListVo);
        }
        return siteGroupListVoList;
    }

    /**
     * 查询用户某个站点套餐的站点数量
     *
     * @param uid     用户 id
     * @param groupId 套餐 id
     */
    @Override
    public Long getUserSiteGroupSiteNum(Integer uid, Integer groupId) {
        MPJQueryWrapper<SysUserRole> userRoleMPJQueryWrapper = new MPJQueryWrapper<>();
        userRoleMPJQueryWrapper.setAlias("sur")
                .leftJoin("?_site s ON sur.site_id = s.site_id".replace("?_", GlobalConfig.tablePrefix));
        userRoleMPJQueryWrapper.eq("sur.uid", uid);
        userRoleMPJQueryWrapper.eq("sur.is_admin", 1);
        userRoleMPJQueryWrapper.eq("s.group_id", groupId);
        return sysUserRoleMapper.selectJoinCount(userRoleMPJQueryWrapper);
    }
}
