package com.niu.core.service.adminapp.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
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
import com.niu.core.service.adminapp.param.PstarEditParam;
import com.niu.core.service.core.adminapp.ICoreAdminAppService;
import com.niu.core.common.utils.RequestUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
    @Resource
    private ICoreAdminAppService coreAdminAppService;

    public JSONArray getApps() {
        JSONArray apps = AppsEnum.getApps();
        Integer isAdmin;
        SysUserRoleInfoVo userRoleInfo;
        if(authService.isSuperAdmin()){
            isAdmin = 1;
            userRoleInfo = new SysUserRoleInfoVo();
        }else{
            userRoleInfo = authService.getAuthRole(RequestUtils.siteId());
            if(ObjectUtil.isEmpty(userRoleInfo)){
                return new JSONArray();
            }
            isAdmin = userRoleInfo.getIsAdmin();
        }
        List<SysMenu> menuList;
        if (ObjectUtil.isNotEmpty(isAdmin) && isAdmin > 0){
            menuList = sysMenuService.getSiteAllMenuList(AppTypeEnum.SITE.getName(),RequestUtils.siteId(),1,true,1);
        }else{
            String roleIds = userRoleInfo.getRoleIds();
            List<String> roleListIds = JSONUtil.parseArray(roleIds).stream().map(Object::toString).toList();
            List<String> menuKeys = sysRoleService.getMenuIdsByRoleIds(RequestUtils.siteId(),roleListIds);
            menuList = sysMenuService.getSiteMenuListByMenuKeys(AppTypeEnum.SITE.getName(),RequestUtils.siteId(),1,1,menuKeys,"all",1);
        }
        if (menuList == null) {
            menuList = new ArrayList<>();
        }
        List<String> menuKeys = menuList.stream().map(SysMenu::getMenuKey).distinct().toList();
        

        JSONArray appNew = new JSONArray();
        for (int i = 0; i<apps.size(); i++){
            JSONObject app = apps.getJSONObject(i);
            String menuKey = app.getStr("menu_key");
            if (StringUtils.isEmpty(menuKey) || menuKeys.contains(menuKey)){
                appNew.add(app);
            }
        }
        return appNew;
    }

    @Override
    public JSONArray getAppsOfIndex() {
        JSONArray apps = this.getApps();
        Integer uid = RequestUtils.uid();
        Integer siteId = RequestUtils.siteId();
        JSONArray keys = coreAdminAppService.getValue(uid, siteId, "app");
    
        int appLimit = 9;
        JSONArray appsOfIndex;
        if (keys == null || keys.isEmpty()) {
            List<JSONObject> sortedApps = JSONUtil.toList(apps, JSONObject.class)
                    .stream()
                    .sorted(Comparator.comparingInt(app -> app.getInt("sort", 0)))
                    .limit(appLimit)
                    .collect(Collectors.toList());
            appsOfIndex = new JSONArray(sortedApps);
        } else {
            appsOfIndex = new JSONArray();
            List<String> keysList = JSONUtil.toList(keys, String.class);
            for (int i = 0; i < apps.size(); i++) {
                JSONObject app = apps.getJSONObject(i);
                String appKey = app.getStr("key");
                if (appKey != null && keysList.contains(appKey)) {
                    int sortIndex = keysList.indexOf(appKey);
                    app.set("sort", sortIndex);
                    appsOfIndex.add(app);
                }
            }
            
            List<JSONObject> sortedList = JSONUtil.toList(appsOfIndex, JSONObject.class)
                    .stream()
                    .sorted(Comparator.comparingInt(app -> app.getInt("sort", 0)))
                    .collect(Collectors.toList());
            appsOfIndex = new JSONArray(sortedList);
        }
        
        return appsOfIndex;
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
        
   
        List<JSONObject> filteredGroups = sortedAppGroups.stream()
                .filter(group -> {
                    JSONArray childs = group.getJSONArray("childs");
                    return childs != null && !childs.isEmpty();
                })
                .collect(Collectors.toList());
        
        return new JSONArray(filteredGroups);
    }

    @Override
    public JSONArray getAppOfUserCenter() {
        JSONArray apps = AppUsersEnum.getApps();
        Integer isAdmin;
        SysUserRoleInfoVo userRoleInfo;
        if(authService.isSuperAdmin()){
            isAdmin = 1;
            userRoleInfo = new SysUserRoleInfoVo();
        }else{
            userRoleInfo = authService.getAuthRole(RequestUtils.siteId());
            if(ObjectUtil.isEmpty(userRoleInfo)){
                return new JSONArray();
            }
            isAdmin = userRoleInfo.getIsAdmin();
        }
        List<SysMenu> menuList;
        if (ObjectUtil.isNotEmpty(isAdmin) && isAdmin > 0){
            menuList = sysMenuService.getSiteAllMenuList(AppTypeEnum.SITE.getName(),RequestUtils.siteId(),1,true,1);
        }else{
            String roleIds = userRoleInfo.getRoleIds();
            List<String> roleListIds = JSONUtil.parseArray(roleIds).stream().map(Object::toString).toList();
            List<String> menuKeys = sysRoleService.getMenuIdsByRoleIds(RequestUtils.siteId(),roleListIds);
            menuList = sysMenuService.getSiteMenuListByMenuKeys(AppTypeEnum.SITE.getName(),RequestUtils.siteId(),1,1,menuKeys,"all",1);
        }
        if (menuList == null) {
            menuList = new ArrayList<>();
        }
        List<String> menuKeys = menuList.stream().map(SysMenu::getMenuKey).distinct().toList();
        
        JSONArray appNew = new JSONArray();
        for (int i = 0; i<apps.size(); i++){
            JSONObject app = apps.getJSONObject(i);
            String menuKey = app.getStr("menu_key");

            if (StringUtils.isEmpty(menuKey) || menuKeys.contains(menuKey)){
                appNew.add(app);
            }
        }
        return appNew;
    }

    @Override
    public Boolean setAppsOfIndex(PstarEditParam param) {
        Integer uid = RequestUtils.uid();
        Integer siteId = RequestUtils.siteId();
        JSONArray valueArray = new JSONArray();
        
        if (param != null && param.getValue() != null) {
            valueArray = new JSONArray(param.getValue());
        }
        
        return coreAdminAppService.setValue(uid, siteId, "app", valueArray);
    }
}
