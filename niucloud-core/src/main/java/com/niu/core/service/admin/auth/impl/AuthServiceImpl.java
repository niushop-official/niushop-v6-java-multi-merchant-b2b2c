package com.niu.core.service.admin.auth.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.exception.AuthException;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.TreeUtils;
import com.niu.core.common.utils.crypto.PasswordEncipher;
import com.niu.core.common.utils.json.JacksonUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.sys.SysMenu;
import com.niu.core.entity.sys.SysUser;
import com.niu.core.entity.sys.SysUserRole;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.enums.sys.AppTypeEnum;
import com.niu.core.enums.sys.CacheTagEnum;
import com.niu.core.mapper.sys.SysUserRoleMapper;
import com.niu.core.service.admin.auth.IAuthService;
import com.niu.core.service.admin.auth.ILoginService;
import com.niu.core.service.admin.auth.param.EditAuthUserParam;
import com.niu.core.service.admin.auth.param.EditManagerPwdParam;
import com.niu.core.service.admin.niucloud.INiucloudService;
import com.niu.core.service.admin.niucloud.vo.AuthInfoVo;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.ISiteUserService;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.admin.site.vo.SiteUserVo;
import com.niu.core.service.admin.sys.ISysMenuService;
import com.niu.core.service.admin.sys.ISysRoleService;
import com.niu.core.service.admin.sys.vo.SysUserRoleInfoVo;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.circle.ICoreAuthCircleUserService;
import com.niu.core.service.core.site.ICoreSiteService;
import com.niu.core.service.core.user.ICoreUserRoleService;
import com.niu.core.service.core.user.ICoreUserService;
import com.niu.core.service.core.user.param.CoreUserEditParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements IAuthService {

    @Resource
    ISiteService siteService;

    @Resource
    ISiteUserService siteUserService;

    @Resource
    ICoreUserRoleService coreUserRoleService;

    @Resource
    ISysMenuService sysMenuService;

    @Resource
    SysUserRoleMapper sysUserRoleMapper;

    @Resource
    ISysRoleService sysRoleService;

    @Resource
    ICoreSiteService coreSiteService;

    @Resource
    ICoreUserService coreUserService;

    @Resource
    ICoreAddonService coreAddonService;

    @Resource
    Cached cached;

    @Resource
    INiucloudService niucloudService;

    @Lazy
    @Resource
    ILoginService loginService;

    @Autowired(required = false)
    private ICoreAuthCircleUserService coreAuthCircleUserService;

    /**
     * 校验用户和传入站点是否存在从属关系
     *
     * @param request
     */
    @Override
    public void checkSiteAuth(HttpServletRequest request) {
        Integer uid = RequestUtils.uid();
        Integer siteId = RequestUtils.adminSiteId();
        //缓存站点信息数据
        SiteInfoVo siteInfo = coreSiteService.getSiteCache(siteId);
        //站点不存在 抛出异常
        if (siteInfo == null) {
            loginService.clearToken(uid, RequestUtils.appType(), null);
            clearSiteAuthCache(siteId);
            throw new AuthException("SITE_NOT_EXIST", 403);
        }
        if (AppTypeEnum.CIRCLE.getName().equals(RequestUtils.appType())) {
            if (coreAddonService.getInfoByKey("circle") == null) {
                throw new AuthException("ADDON_IS_NOT_EXIST", 403);
            }
            Integer circleId = RequestUtils.circleId();
            if (circleId != null && circleId > 0) {
                RequestUtils.setCircleIds(List.of(circleId));
            } else if (coreAuthCircleUserService != null) {
                Integer defaultCircleId = coreAuthCircleUserService.getCircleId();
                if (defaultCircleId != null && defaultCircleId > 0) {
                    RequestUtils.setCircleIds(List.of(defaultCircleId));
                }
            }
            return;
        }
        //没有当前站点的信息
        if (!isSuperAdmin()  && ObjectUtil.isNotNull(uid) && uid > 0) {
            if (getAuthRole(siteId) == null) {
                loginService.clearToken(uid, RequestUtils.appType(), null);
                clearSiteAuthCache(siteId);
                throw new AuthException("NO_SITE_PERMISSION", 403);
            }
        }
        //设置当前操作站点id
        RequestUtils.setSiteId(siteId);
        RequestUtils.setAppType(siteInfo.getAppType());
    }

    /**
     * 是否是超级管理员
     *
     * @return
     */
    @Override
    public boolean isSuperAdmin() {
        Integer siteId = RequestUtils.defaultSiteId();
        Integer uid = RequestUtils.uid();
        Integer superAdminUid = (Integer) cached.tag("adminAuth").get("superAdminUid");
        if (ObjectUtil.isNotNull(superAdminUid) && ObjectUtil.isNotEmpty(superAdminUid) && superAdminUid > 0) {
            return superAdminUid.equals(uid);
        } else {
            SysUserRole sysUserRole = sysUserRoleMapper.selectOne(new QueryWrapper<SysUserRole>().eq("site_id", siteId).eq("is_admin", 1).last(" limit 1"));
            cached.tag("adminAuth").put("superAdminUid", sysUserRole.getUid());
            return sysUserRole.getUid().equals(uid);
        }
    }

    /**
     * 校验权限
     *
     * @param request
     */
    @Override
    public void checkRole(HttpServletRequest request) {
        checkAuthInfo(request);

        String rule = RequestUtils.getReqeustURI().toLowerCase().trim();
        String method = RequestUtils.getRequestMethod().toLowerCase().trim();

        //缓存站点信息数据
        SiteInfoVo siteInfo = siteService.info(RequestUtils.siteId());
        if (!"get".equals(method)) {
            if (SiteStatusEnum.EXPIRE.getCode().equals(siteInfo.getStatus())) {
                throw new AuthException("SITE_EXPIRE_NOT_ALLOW");
            }
            if (SiteStatusEnum.CLOSE.getCode().equals(siteInfo.getStatus())) {
                throw new AuthException("SITE_CLOSE_NOT_ALLOW");
            }
        }

        // 获取当前appType的全部受控接口清单（对齐 PHP MenuService::getAllApiList）
        Map<String, List<String>> allMenuList = sysMenuService.getAllApiList(RequestUtils.appType(), 100);
        List<String> methodMenuList = allMenuList.getOrDefault(method, Collections.emptyList());

        if(rule.startsWith("/adminapi")) {
            rule = rule.substring(9);
        }
        if(rule.startsWith("/")) {
            rule = rule.substring(1);
        }
        // 第一步：判断当前接口是否受权限控制。不在受控列表中则直接放行（对齐 PHP 逻辑）
        if (!methodMenuList.contains(rule)) {
            return;
        }

        // 第二步：接口受控，校验当前角色是否拥有该接口权限
        Map<String, List<String>> roleMenuList = this.getAuthApiList();
        List<String> authList = roleMenuList.getOrDefault(method, Collections.emptyList());
        if (!authList.contains(rule)) {
            throw new AuthException("NO_PERMISSION", 400);
        }

    }

    @Override
    public void checkIsDemo(HttpServletRequest request) {
        String method = RequestUtils.getRequestMethod();

        if (!method.equals("get") && GlobalConfig.isDemo) {
            throw new CommonException("演示环境不允许操作");
        }
    }

    @Override
    public void checkAuthInfo(HttpServletRequest request) {
        String method = RequestUtils.getRequestMethod();
        if (method.equals("get") || request.getRequestURI().contains("/adminapi/niucloud/authinfo")) return;

        if (!isCheckDomain()) return;

        AuthInfoVo authInfo = niucloudService.getAuthinfo();
        if (authInfo == null) return;

        String siteAddress = authInfo.getData().getSiteAddress();
        if (ObjectUtil.isNotEmpty(siteAddress) && RequestUtils.getRequestDomain().contains(siteAddress)) return;

        throw new CommonException("授权域名校验失败！请确保当前访问域名与授权码绑定的域名一致");
    }

    public boolean isCheckDomain() {
        String domain = RequestUtils.getRequestDomain();

        return !(domain.startsWith("localhost") || domain.startsWith("127.0.0.1"));
    }

    private void clearSiteAuthCache(Integer siteId) {
        cached.tag("site_cash" + siteId).clear();
        cached.tag(CacheTagEnum.ADDON_CACHE.getTagName()).clear();
        cached.tag(CacheTagEnum.MENU_CACHE.getTagName()).clear();
    }
    /**
     * 当前授权用户接口权限
     *
     * @return
     */
    @Override
    public Map<String, List<String>> getAuthApiList() {
        Integer isAdmin = 0;
        Integer siteId = RequestUtils.siteId();
        String appType = RequestUtils.appType();
        Map<String, List<String>> authApi = new HashMap<>();
        SysUserRoleInfoVo sysUserRoleInfoVo = new SysUserRoleInfoVo();
        if (AppTypeEnum.CIRCLE.getName().equals(appType)) {
            isAdmin = 1;
        } else if (isSuperAdmin()) {
            isAdmin = 1;
        } else {
            sysUserRoleInfoVo = getAuthRole(siteId);
            if (ObjectUtil.isNull(sysUserRoleInfoVo)) {
                return authApi;
            }
            isAdmin = sysUserRoleInfoVo.getIsAdmin();//是否是超级管理员
        }
        if (isAdmin > 0) {
            if (AppTypeEnum.CIRCLE.getName().equals(appType)) {
                authApi = sysMenuService.getAllApiList(appType, 1);
            } else {
                authApi = siteService.getSiteApiList(siteId, 1);
            }
        } else {
            List<String> roleIdList = JSONUtil.toList(JSONUtil.parseArray(sysUserRoleInfoVo.getRoleIds()), String.class);
            List<String> menuKeyList = sysRoleService.getMenuIdsByRoleIds(siteId, roleIdList);
            authApi = sysMenuService.getApiListByMenuKeys(appType, menuKeyList);
        }
        return authApi;

    }

    /**
     * 当前授权用户的菜单权限
     *
     * @param addon
     * @return
     */
    @Override
    public JSONArray getAuthMenuTreeList(String addon) {
        Integer isAdmin = 0;
        Integer siteId = RequestUtils.siteId();
        String appType = RequestUtils.appType();
        SysUserRoleInfoVo sysUserRoleInfoVo = new SysUserRoleInfoVo();
        List<SysMenu> menuList = new ArrayList<>();
        if (isSuperAdmin()) {
            isAdmin = 1;
        } else {
            sysUserRoleInfoVo = getAuthRole(siteId);
            if (ObjectUtil.isNull(sysUserRoleInfoVo)) {
                return new JSONArray();
            }
            isAdmin = sysUserRoleInfoVo.getIsAdmin();//是否是超级管理员
        }
        if (isAdmin > 0) {
            menuList = sysMenuService.getMenuListByCondition(appType, siteId, 1, 0, new ArrayList<>(), addon);
        } else {
            List<String> roleIdList = JSONUtil.toList(JSONUtil.parseArray(sysUserRoleInfoVo.getRoleIds()), String.class);
            List<String> menuKeyList = sysRoleService.getMenuIdsByRoleIds(siteId, roleIdList);
            menuList = sysMenuService.getMenuListByCondition(appType, siteId, 100, 0, menuKeyList, addon);
        }

        JSONArray jsonArray = JSONUtil.parseArray(JacksonUtils.toSnakeCaseJSONString(menuList));
        return TreeUtils.listToTree(jsonArray, "menu_key", "parent_key", "children");
    }

    @Override
    public JSONArray getAdminAuthMenuList(Boolean isTree, String addon) {
        Integer isAdmin = 0;
        Integer siteId = RequestUtils.siteId();
        String appType = RequestUtils.appType();
        SysUserRoleInfoVo sysUserRoleInfoVo = new SysUserRoleInfoVo();
        List<SysMenu> menuList = new ArrayList<>();
        if (AppTypeEnum.CIRCLE.getName().equals(appType)) {
            isAdmin = 1;
        } else if (isSuperAdmin()) {
            isAdmin = 1;
        } else {
            sysUserRoleInfoVo = getAuthRole(siteId);
            if (ObjectUtil.isNull(sysUserRoleInfoVo)) {
                return new JSONArray();
            }
            isAdmin = sysUserRoleInfoVo.getIsAdmin();//是否是超级管理员
        }
        if (isAdmin > 0) {
            menuList = sysMenuService.getAdminAllMenuList(appType, 1, false,1);
        } else {
            List<String> roleIdList = JSONUtil.toList(JSONUtil.parseArray(sysUserRoleInfoVo.getRoleIds()), String.class);
            List<String> menuKeyList = sysRoleService.getMenuIdsByRoleIds(siteId, roleIdList);
            menuList = sysMenuService.getAdminMenuListByMenuKeys(appType, siteId, 100, 0, menuKeyList, addon,1);
        }

        JSONArray jsonArray = JSONUtil.parseArray(JacksonUtils.toSnakeCaseJSONString(menuList));
        return TreeUtils.listToTree(jsonArray, "menu_key", "parent_key", "children");
    }

    @Override
    public JSONArray getSiteAuthMenuList(Boolean isTree, String addon) {
        Integer isAdmin = 0;
        Integer siteId = RequestUtils.siteId();
        String appType = RequestUtils.appType();
        SysUserRoleInfoVo sysUserRoleInfoVo = new SysUserRoleInfoVo();
        List<SysMenu> menuList = new ArrayList<>();
        if (isSuperAdmin()) {
            isAdmin = 1;
        } else {
            sysUserRoleInfoVo = getAuthRole(siteId);
            if (ObjectUtil.isNull(sysUserRoleInfoVo)) {
                return new JSONArray();
            }
            isAdmin = sysUserRoleInfoVo.getIsAdmin();//是否是超级管理员
        }
        if (isAdmin > 0) {
            menuList = sysMenuService.getSiteAllMenuList(appType,siteId, 1, false,1);
        } else {
            List<String> roleIdList = JSONUtil.toList(JSONUtil.parseArray(sysUserRoleInfoVo.getRoleIds()), String.class);
            List<String> menuKeyList = sysRoleService.getMenuIdsByRoleIds(siteId, roleIdList);
            menuList = sysMenuService.getSiteMenuListByMenuKeys(appType, siteId, 100, 0, menuKeyList, addon,1);
        }
        //未安装供应商去除商城选货平台
        Addon checkAddon = coreAddonService.getInfoByKey("supply");
        if(ObjectUtil.isEmpty(checkAddon))
        {
            menuList = menuList.stream().filter(item->!(item.getMenuKey().equals("supply_select_goods") || item.getMenuKey().equals("supply_select_goods_my_supply"))).collect(Collectors.toList());
        }

        JSONArray jsonArray = JSONUtil.parseArray(JacksonUtils.toSnakeCaseJSONString(menuList));
        return TreeUtils.listToTree(jsonArray, "menu_key", "parent_key", "children");
    }

    /**
     * 获取授权用户信息
     *
     * @return
     */
    @Override
    public SiteUserVo getAuthUserInfo() {
        return siteUserService.getInfo(RequestUtils.uid());
    }

    @Override
    public void modifyAuth(String field, String value) {
        siteUserService.modify(RequestUtils.uid(), field, value);
    }

    /**
     * 更新用户
     *
     * @param editAuthUserParam
     */
    @Override
    public void editAuth(EditAuthUserParam editAuthUserParam) {
        if (ObjectUtil.isNotNull(editAuthUserParam.getPassword()) && ObjectUtil.isNotEmpty(editAuthUserParam.getPassword())) {
            SysUser sysUser = coreUserService.find(RequestUtils.uid());
            if (!PasswordEncipher.matche(editAuthUserParam.getOriginalPassword(), sysUser.getPassword())) {
                throw new AuthException("原始密码不正确");
            }
        }
        CoreUserEditParam editParam = new CoreUserEditParam();
        editParam.setHeadImg(editAuthUserParam.getHeadImg());
        editParam.setRealName(editAuthUserParam.getRealName());
        editParam.setPassword(editAuthUserParam.getPassword());
        coreUserService.edit(RequestUtils.uid(), editParam);
    }

    /**
     * 修改站点管理员密码
     */
    @Override
    public void sitePassword(EditManagerPwdParam param) {
        Integer uid = coreUserRoleService.getSiteManagerUid(param.getId());
        if (uid == null) {
            throw new AuthException("SITE_MANAGER_NOT_EXIST");
        }
        if (ObjectUtil.isEmpty(param.getPassword())) {
            throw new AuthException("PASSWORD_EMPTY");
        }
        if (ObjectUtil.isEmpty(param.getConfirmPassword())) {
            throw new AuthException("CONFIRM_PASSWORD_EMPTY");
        }
        if (!param.getPassword().equals(param.getConfirmPassword())) {
            throw new AuthException("THE_TWO_PASSWORD_INPUTS_INCONSISTENT");
        }
        CoreUserEditParam editParam = new CoreUserEditParam();
        editParam.setPassword(param.getPassword());
        coreUserService.edit(uid, editParam);
    }

    @Override
    public JSONArray getAdminAuthMenuList(Boolean isTree, Integer status, String addon, Integer isButton) {
        return siteService.getAdminMenuList(RequestUtils.siteId(), isTree, status, addon, isButton);
    }

    @Override
    public SysUserRoleInfoVo getAuthRole(Integer siteId) {
        return coreUserRoleService.getUserRole(siteId, RequestUtils.uid());
    }
}
