package com.niu.core.service.admin.auth.impl;

import cn.hutool.core.bean.BeanUtil;
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
import com.niu.core.mapper.sys.SysUserRoleMapper;
import com.niu.core.service.admin.auth.IAuthService;
import com.niu.core.service.admin.auth.param.EditAuthUserParam;
import com.niu.core.service.admin.auth.param.EditManagerPwdParam;
import com.niu.core.service.admin.auth.vo.AuthUserInfoVo;
import com.niu.core.service.admin.niucloud.INiucloudService;
import com.niu.core.service.admin.niucloud.vo.AuthInfoVo;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.admin.sys.ISysMenuService;
import com.niu.core.service.admin.sys.ISysRoleService;
import com.niu.core.service.admin.sys.ISysUserRoleService;
import com.niu.core.service.admin.sys.ISysUserService;
import com.niu.core.service.admin.sys.param.SysUserParam;
import com.niu.core.service.admin.sys.vo.SysUserDetailVo;
import com.niu.core.service.admin.sys.vo.SysUserRoleInfoVo;
import com.niu.core.service.core.addon.ICoreAddonService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements IAuthService {

    @Resource
    ISiteService siteService;

    @Resource
    ISysUserRoleService sysUserRoleService;

    @Resource
    ISysMenuService sysMenuService;

    @Resource
    SysUserRoleMapper sysUserRoleMapper;

    @Resource
    ISysRoleService sysRoleService;

    @Resource
    ISysUserService sysUserService;

    @Resource
    ICoreAddonService coreAddonService;

    @Resource
    Cached cached;

    @Resource
    INiucloudService niucloudService;

    /**
     * 校验用户和传入站点是否存在从属关系
     *
     * @param request
     */
    @Override
    public void checkSiteAuth(HttpServletRequest request) {

        Integer uid = RequestUtils.uid();

        //设置当前操作站点id
        Integer siteId = RequestUtils.adminSiteId();
        RequestUtils.setSiteId(siteId);

        //缓存站点信息数据
        SiteInfoVo siteinfo = siteService.info(siteId);
        //站点不存在 抛出异常
        if (siteinfo == null) {
            throw new AuthException("SITE_NOT_EXIST", 400);
        }
        //没有当前站点的信息
        if (!isSuperAdmin() && ObjectUtil.isNotNull(uid) && uid > 0) {
            SysUserRoleInfoVo sysUserRoleInfoVo = sysUserRoleService.getUserRole(siteId, uid);
            if (sysUserRoleInfoVo == null) {
                throw new AuthException("您没有当前店铺的访问权限", 400);
            }
        }
        RequestUtils.setAppType(siteinfo.getAppType());
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
        //当前访问的路由地址
        String rule = RequestUtils.getReqeustURI();
        String method = RequestUtils.getRequestMethod();

        //缓存站点信息数据
        SiteInfoVo siteinfo = siteService.info(RequestUtils.siteId());
        if (!method.equals("get")) {
            if (siteinfo.getStatus().equals(SiteStatusEnum.EXPIRE.getCode())) {
                throw new AuthException("站点已打烊，续费后可继续使用此项功能", 400);
            }
            if (siteinfo.getStatus().equals(SiteStatusEnum.CLOSE.getCode())) {
                throw new AuthException("站点已停止", 400);
            }
        }

        Map<String, List<String>> allMenuList = sysMenuService.getAllApiList(RequestUtils.appType(), 100);
        List<String> menulist = allMenuList.get(method);
        int is_exists = menulist.indexOf(rule);

        //判断当前访问的接口是否收到权限的限制
        if (is_exists < 0) {
            Map<String, List<String>> otherMenuList = sysMenuService.getAllApiList(RequestUtils.appType().equals(AppTypeEnum.ADMIN.getName()) ? AppTypeEnum.SITE.getName() : AppTypeEnum.ADMIN.getName(), 100);
            List<String> methodMenuList = otherMenuList.get(method);
            int is_method_exists = methodMenuList.indexOf(rule);
            if (is_method_exists > 0) {
                throw new AuthException("NO_PERMISSION", 400);
            }
        }
        Map<String, List<String>> roleMenuList = this.getAuthApiList();
        /*if(roleMenuList.get(method).size()<=0 || roleMenuList.get(method).indexOf(rule)<=0){
            throw new AuthException("NO_PERMISSION");
        }*/

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

    /**
     * 当前授权用户接口权限
     *
     * @return
     */
    @Override
    public Map<String, List<String>> getAuthApiList() {
        Integer isAdmin = 0;
        Integer siteId = RequestUtils.siteId();
        Integer uid = RequestUtils.uid();
        String appType = RequestUtils.appType();
        Map<String, List<String>> authApi = new HashMap<>();
        SysUserRoleInfoVo sysUserRoleInfoVo = new SysUserRoleInfoVo();
        if (isSuperAdmin()) {
            isAdmin = 1;
        } else {
            sysUserRoleInfoVo = sysUserRoleService.getUserRole(siteId, uid);
            if (ObjectUtil.isNull(sysUserRoleInfoVo)) {
                return authApi;
            }
            isAdmin = sysUserRoleInfoVo.getIsAdmin();//是否是超级管理员
        }
        if (isAdmin > 0) {
            authApi = siteService.getSiteApiList(siteId, 1);
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
        Integer uid = RequestUtils.uid();
        String appType = RequestUtils.appType();
        Map<String, List<String>> authApi = new HashMap<>();
        SysUserRoleInfoVo sysUserRoleInfoVo = new SysUserRoleInfoVo();
        List<SysMenu> menuList = new ArrayList<>();
        if (isSuperAdmin()) {
            isAdmin = 1;
        } else {
            sysUserRoleInfoVo = sysUserRoleService.getUserRole(siteId, uid);
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
        Integer uid = RequestUtils.uid();
        String appType = RequestUtils.appType();
        Map<String, List<String>> authApi = new HashMap<>();
        SysUserRoleInfoVo sysUserRoleInfoVo = new SysUserRoleInfoVo();
        List<SysMenu> menuList = new ArrayList<>();
        if (isSuperAdmin()) {
            isAdmin = 1;
        } else {
            sysUserRoleInfoVo = sysUserRoleService.getUserRole(siteId, uid);
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
        Integer uid = RequestUtils.uid();
        String appType = RequestUtils.appType();
        Map<String, List<String>> authApi = new HashMap<>();
        SysUserRoleInfoVo sysUserRoleInfoVo = new SysUserRoleInfoVo();
        List<SysMenu> menuList = new ArrayList<>();
        if (isSuperAdmin()) {
            isAdmin = 1;
        } else {
            sysUserRoleInfoVo = sysUserRoleService.getUserRole(siteId, uid);
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
    public AuthUserInfoVo getAuthUserInfo() {
        Integer uid = RequestUtils.uid();
        SysUserDetailVo userInfo = sysUserService.info(uid);
        if (userInfo == null) return null;

        AuthUserInfoVo vo = new AuthUserInfoVo();
        BeanUtil.copyProperties(userInfo, vo);
        return vo;
//        MPJQueryWrapper<SysUserRole> userRoleMPJQueryWrapper = new MPJQueryWrapper<>();
//        userRoleMPJQueryWrapper.select("sur.id, su.username, su.head_img, su.real_name, su.last_ip, su.last_time, su.login_count, sur.uid, sur.site_id, sur.role_ids, sur.create_time, sur.is_admin, sur.status")
//                .setAlias("sur")
//                .leftJoin("?_sys_user su ON sur.uid = su.uid".replace("?_", GlobalConfig.tablePrefix));
//        userRoleMPJQueryWrapper.eq("sur.uid", uid);
//        userRoleMPJQueryWrapper.eq("sur.site_id", siteId);
//        AuthUserInfoVo authUserInfoVo = sysUserRoleMapper.selectJoinOne(AuthUserInfoVo.class, userRoleMPJQueryWrapper);
//        if(ObjectUtil.isNotNull(authUserInfoVo))
//        {
//            authUserInfoVo.setStatusName("");
//        }
//
//        return authUserInfoVo;
    }

    /**
     * 更新用户
     *
     * @param editAuthUserParam
     */
    @Override
    public void editAuth(EditAuthUserParam editAuthUserParam) {
        if (ObjectUtil.isNotNull(editAuthUserParam.getPassword()) && ObjectUtil.isNotEmpty(editAuthUserParam.getPassword())) {
            SysUser sysUser = sysUserService.find(RequestUtils.uid());
            if (!PasswordEncipher.matche(editAuthUserParam.getOriginalPassword(), sysUser.getPassword())) {
                throw new AuthException("OLD_PASSWORD_ERROR");
            }
        }
        SysUserParam sysUserParam = new SysUserParam();
        sysUserParam.setHeadImg(editAuthUserParam.getHeadImg());
        sysUserParam.setRealName(editAuthUserParam.getRealName());
        sysUserParam.setPassword(editAuthUserParam.getPassword());
        sysUserService.edit(RequestUtils.uid(), sysUserParam);
    }

    @Override
    public void sitePassword(EditManagerPwdParam param) {
        Integer uid = sysUserRoleService.getSiteManagerUid(param.getId());
        if (uid == null){
            throw new AuthException("站点管理员不存在");
        }
        if (param.getPassword().isEmpty()){
            throw new AuthException("密码不能为空");
        }
        if (param.getConfirmPassword().isEmpty()){
            throw  new AuthException("确认密码不能为空");
        }
        if (!param.getPassword().equals(param.getConfirmPassword())){
            throw new AuthException("两次密码输入不一致");
        }
        SysUserParam sysUserParam = new SysUserParam();
        sysUserParam.setPassword(param.getPassword());
        sysUserParam.setUid(uid);
        sysUserService.edit(uid,sysUserParam);
    }



    @Override
    public JSONArray getAdminAuthMenuList(Boolean isTree, Integer status, String addon, Integer isButton) {
        Integer siteId = RequestUtils.siteId();
        
        return siteService.getAdminMenuList(siteId, isTree, status, addon, isButton);
    }

    @Override
    public SysUserRoleInfoVo getAuthRole(Integer siteId) {
        SysUserRoleInfoVo userRole = sysUserRoleService.getUserRole(siteId, RequestUtils.uid());
        return userRole;
    }
}
