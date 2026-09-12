package com.niu.core.service.core.shop.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.enums.addon.AddonActionEnum;
import com.niu.core.enums.addon.AddonStatusEnum;
import com.niu.core.enums.shop.ShopGroupCreateTypeEnum;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.site.SiteGroupMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.admin.sys.ISysMenuService;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.shop.ICoreShopSiteGroupService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 店铺套餐服务实现（对齐 PHP CoreShopSiteGroupService）
 */
@Service
public class CoreShopSiteGroupServiceImpl implements ICoreShopSiteGroupService {

    @Resource
    private SiteGroupMapper siteGroupMapper;

    @Resource
    private SiteMapper siteMapper;

    @Resource
    private AddonMapper addonMapper;

    @Resource
    private ICoreAddonService coreAddonService;

    @Resource
    private ISysMenuService sysMenuService;

    @Override
    public void createAppGroup(String addon) {
        if (!StringUtils.hasText(addon)) {
            return;
        }
        Addon addonInfo = coreAddonService.getInfoByKey(addon);
        if (addonInfo == null || !AddonActionEnum.APP.getCode().equals(addonInfo.getType())) {
            return;
        }
        String appJson = JSONUtil.toJsonStr(List.of(addon));
        Long count = siteGroupMapper.selectCount(new QueryWrapper<SiteGroup>()
                .eq("app", appJson)
                .eq("create_by", ShopGroupCreateTypeEnum.APP.getStatus()));
        if (count != null && count > 0) {
            return;
        }
        SiteGroup model = new SiteGroup();
        model.setGroupName(addonInfo.getTitle() + "套餐");
        model.setGroupDesc("");
        model.setApp(appJson);
        model.setAddon("[]");
        model.setCreateBy(ShopGroupCreateTypeEnum.APP.getStatus());
        model.setCreateTime(DateUtils.currTime());
        model.setUpdateTime(DateUtils.currTime());
        siteGroupMapper.insert(model);
    }

    @Override
    public void deleteAppGroup(String addon) {
        if (!StringUtils.hasText(addon)) {
            return;
        }
        Addon addonInfo = coreAddonService.getInfoByKey(addon);
        if (addonInfo != null) {
            return;
        }
        String appJson = JSONUtil.toJsonStr(List.of(addon));
        siteGroupMapper.delete(new QueryWrapper<SiteGroup>()
                .eq("app", appJson)
                .eq("create_by", ShopGroupCreateTypeEnum.APP.getStatus()));
    }

    @Override
    public void updateSysGroup() {
        List<String> apps = getInstallAddonKeys(AddonActionEnum.APP.getCode());
        List<String> addons = getInstallAddonKeys(AddonActionEnum.ADDON.getCode());
        String appsJson = JSONUtil.toJsonStr(apps);
        String addonsJson = JSONUtil.toJsonStr(addons);
        long now = DateUtils.currTime();

        SiteGroup systemGroup = siteGroupMapper.selectOne(new QueryWrapper<SiteGroup>()
                .eq("create_by", ShopGroupCreateTypeEnum.SYSTEM.getStatus())
                .last("limit 1"));
        if (systemGroup == null) {
            SiteGroup model = new SiteGroup();
            model.setGroupName("商户套餐");
            model.setGroupDesc("");
            model.setApp(appsJson);
            model.setAddon(addonsJson);
            model.setCreateBy(ShopGroupCreateTypeEnum.SYSTEM.getStatus());
            model.setCreateTime(now);
            model.setUpdateTime(now);
            siteGroupMapper.insert(model);
        } else {
            SiteGroup update = new SiteGroup();
            update.setGroupId(systemGroup.getGroupId());
            update.setApp(appsJson);
            update.setAddon(addonsJson);
            update.setUpdateTime(now);
            siteGroupMapper.updateById(update);
        }

        List<SiteGroup> appGroups = siteGroupMapper.selectList(new QueryWrapper<SiteGroup>()
                .eq("create_by", ShopGroupCreateTypeEnum.APP.getStatus()));
        for (SiteGroup appGroup : appGroups) {
            List<String> groupApps = StringUtils.hasText(appGroup.getApp())
                    ? JSONUtil.toList(appGroup.getApp(), String.class)
                    : List.of();
            String tempAddonsJson = JSONUtil.toJsonStr(filterAddonsForAppGroup(addons, groupApps));
            SiteGroup update = new SiteGroup();
            update.setGroupId(appGroup.getGroupId());
            update.setAddon(tempAddonsJson);
            update.setUpdateTime(now);
            siteGroupMapper.updateById(update);
            appGroup.setAddon(tempAddonsJson);
        }

        for (SiteGroup appGroup : appGroups) {
            Site siteUpdate = new Site();
            siteUpdate.setAddons(appGroup.getAddon());
            siteMapper.update(siteUpdate, new QueryWrapper<Site>()
                    .eq("group_id", appGroup.getGroupId()));
        }

        systemGroup = siteGroupMapper.selectOne(new QueryWrapper<SiteGroup>()
                .eq("create_by", ShopGroupCreateTypeEnum.SYSTEM.getStatus())
                .last("limit 1"));
        if (systemGroup != null && systemGroup.getGroupId() != null) {
            Site siteUpdate = new Site();
            siteUpdate.setAddons(systemGroup.getAddon());
            siteMapper.update(siteUpdate, new QueryWrapper<Site>()
                    .eq("group_id", systemGroup.getGroupId()));

            Site defaultSiteUpdate = new Site();
            defaultSiteUpdate.setApp(systemGroup.getApp());
            defaultSiteUpdate.setAddons(systemGroup.getAddon());
            siteMapper.update(defaultSiteUpdate, new QueryWrapper<Site>()
                    .eq("site_id", RequestUtils.defaultSiteId()));
        }
        //更新完毕清除菜单缓存
        sysMenuService.clear();
    }

    private List<String> filterAddonsForAppGroup(List<String> addons, List<String> groupApps) {
        List<String> tempAddons = new ArrayList<>();
        for (String addonKey : addons) {
            Addon addonInfo = coreAddonService.getInfoByKey(addonKey);
            if (addonInfo == null) {
                continue;
            }
            if (!StringUtils.hasText(addonInfo.getSupportApp()) || groupApps.contains(addonInfo.getSupportApp())) {
                tempAddons.add(addonKey);
            }
        }
        return tempAddons;
    }

    private List<String> getInstallAddonKeys(String type) {
        return addonMapper.selectList(new LambdaQueryWrapper<Addon>()
                        .eq(Addon::getStatus, AddonStatusEnum.ON.getCode())
                        .eq(Addon::getType, type)
                        .select(Addon::getKey))
                .stream()
                .map(Addon::getKey)
                .collect(Collectors.toList());
    }
}
