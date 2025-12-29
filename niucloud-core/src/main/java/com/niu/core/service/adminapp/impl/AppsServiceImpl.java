package com.niu.core.service.adminapp.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.sys.SysAgreement;
import com.niu.core.entity.sys.SysMenu;
import com.niu.core.enums.adminapp.AppGroupEnum;
import com.niu.core.enums.adminapp.AppUsersEnum;
import com.niu.core.enums.adminapp.AppsEnum;
import com.niu.core.enums.sys.AppTypeEnum;
import com.niu.core.service.admin.auth.IAuthService;
import com.niu.core.service.admin.sys.ISysMenuService;
import com.niu.core.service.admin.sys.ISysRoleService;
import com.niu.core.service.admin.sys.vo.SysUserRoleInfoVo;
import com.niu.core.service.adminapp.IAppsService;
import com.niu.core.service.api.agreement.IAgreementService;
import com.niu.core.service.api.agreement.param.AgreementInfoParam;
import com.niu.core.service.api.agreement.vo.AgreementInfoVo;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.sys.ICoreAgreementService;
import com.niu.core.service.core.sys.ICoreMenuService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AppsServiceImpl implements IAppsService {

    @Resource
    private IAuthService authService;
    @Resource
    private ISysMenuService sysMenuService;
    @Resource
    private ISysRoleService sysRoleService;

    public JSONArray getApps() {
        JSONArray apps = AppsEnum.getApps();
        Integer isAdmin = 0;
        SysUserRoleInfoVo userRoleInfo = new SysUserRoleInfoVo();
        if(authService.isSuperAdmin()){
            isAdmin = 1;
        }else{
            userRoleInfo = authService.getAuthRole(RequestUtils.siteId());
            if(ObjectUtil.isEmpty(userRoleInfo)){
                return new JSONArray();
            }
            isAdmin = userRoleInfo.getIsAdmin();
        }
        List<SysMenu> menuList = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(isAdmin) && isAdmin > 0){
            menuList = sysMenuService.getSiteAllMenuList(AppTypeEnum.SITE.getName(),RequestUtils.siteId(),1,true);
        }else{
            String roleIds = userRoleInfo.getRoleIds();
            List<String> roleListIds = JSONUtil.parseArray(roleIds).stream().map(Object::toString).toList();
            List<String> menuKeys = sysRoleService.getMenuIdsByRoleIds(RequestUtils.siteId(),roleListIds);
            menuList = sysMenuService.getSiteMenuListByMenuKeys(AppTypeEnum.SITE.getName(),RequestUtils.siteId(),1,1,menuKeys,"all");
        }
        JSONArray appNew = new JSONArray();
        if (!menuList.isEmpty()){
            List<String> menuKeys = menuList.stream().map(SysMenu::getMenuKey).distinct().toList();
            for (int i = 0; i<apps.size(); i++){
                JSONObject app = apps.getJSONObject(i);
                String menuKey = app.getStr("menu_key");
                if (StringUtils.isNotEmpty(menuKey) && menuKeys.contains(menuKey)){
                    appNew.add(app);
                }
            }
        }
        return appNew;
    }

    @Override
    public JSONArray getAppsOfIndex() {
        JSONArray apps = this.getApps();
        List<JSONObject> sortedApps = JSONUtil.toList(apps,JSONObject.class)
                .stream()
                .sorted(Comparator.comparingInt(app -> app.getInt("sort")))
                .limit(9)
                .collect(Collectors.toList());
        return new JSONArray(sortedApps);
    }

    @Override
    public JSONArray getAppsOfCheck() {
        JSONArray apps = this.getApps();
        JSONArray appGroups = AppGroupEnum.getAppGroup();
        List<JSONObject> sortedAppGroups = JSONUtil.toList(appGroups,JSONObject.class)
                .stream()
                .sorted(Comparator.comparingInt(app -> app.getInt("sort")))
                .toList();

        if(!apps.isEmpty()){
            List<JSONObject> sortedApps = JSONUtil.toList(apps,JSONObject.class)
                    .stream()
                    .sorted(Comparator.comparingInt(app -> app.getInt("sort")))
                    .toList();

            for (JSONObject app : sortedApps){
                String groupKey = app.getStr("group");
                for (JSONObject group : sortedAppGroups){
                    if(groupKey.equals(group.getStr("key"))){
                        if(group.getJSONArray("childs") == null){
                            JSONArray childs = new JSONArray();
                            childs.add(app);
                            group.set("childs",childs);
                        }else{
                            JSONArray childs = group.getJSONArray("childs");
                            childs.add(app);
                            group.set("childs",childs);
                        }
                    }
                }
            }
        }
        return new JSONArray(sortedAppGroups);
    }

    @Override
    public JSONArray getAppOfUserCenter() {
        JSONArray apps = AppUsersEnum.getApps();
        Integer isAdmin = 0;
        SysUserRoleInfoVo userRoleInfo = new SysUserRoleInfoVo();
        if(authService.isSuperAdmin()){
            isAdmin = 1;
        }else{
            userRoleInfo = authService.getAuthRole(RequestUtils.siteId());
            if(ObjectUtil.isEmpty(userRoleInfo)){
                return new JSONArray();
            }
            isAdmin = userRoleInfo.getIsAdmin();
        }
        List<SysMenu> menuList = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(isAdmin) && isAdmin > 0){
            menuList = sysMenuService.getSiteAllMenuList(AppTypeEnum.SITE.getName(),RequestUtils.siteId(),1,true);
        }else{
            String roleIds = userRoleInfo.getRoleIds();
            List<String> roleListIds = JSONUtil.parseArray(roleIds).stream().map(Object::toString).toList();
            List<String> menuKeys = sysRoleService.getMenuIdsByRoleIds(RequestUtils.siteId(),roleListIds);
            menuList = sysMenuService.getSiteMenuListByMenuKeys(AppTypeEnum.SITE.getName(),RequestUtils.siteId(),1,1,menuKeys,"all");
        }
        JSONArray appNew = new JSONArray();
        if (!menuList.isEmpty()){
            List<String> menuKeys = menuList.stream().map(SysMenu::getMenuKey).distinct().toList();
            for (int i = 0; i<apps.size(); i++){
                JSONObject app = apps.getJSONObject(i);
                String menuKey = app.getStr("menu_key");
                if (StringUtils.isNotEmpty(menuKey) && menuKeys.contains(menuKey)){
                    appNew.add(app);
                }
            }
        }
        return appNew;
    }
}
