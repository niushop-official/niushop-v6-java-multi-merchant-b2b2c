package com.niu.core.service.admin.auth;

import cn.hutool.json.JSONArray;
import com.niu.core.service.admin.auth.param.EditAuthUserParam;
import com.niu.core.service.admin.auth.param.EditManagerPwdParam;
import com.niu.core.service.admin.site.vo.SiteUserVo;
import com.niu.core.service.admin.sys.vo.SysUserRoleInfoVo;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 用户权限服务
 */
public interface IAuthService {

    /**
     * 校验用户和传入站点是否存在从属关系
     */
    void checkSiteAuth(HttpServletRequest request);


    /**
     * 是否是超级管理员
     * @return
     */
    boolean isSuperAdmin();

    /**
     * 校验权限
     * @param request
     */
    void checkRole(HttpServletRequest request);

    /**
     * 校验授权
     */
    void checkAuthInfo(HttpServletRequest request);

    /**
     * 检测是否是演示环境
     */
    void checkIsDemo(HttpServletRequest request);

    /**
     * 当前授权用户接口权限
     * @return
     */
    Map<String, List<String>> getAuthApiList();

    /**
     * 当前授权用户的菜单权限
     * @param addon
     * @return
     */
    JSONArray getAuthMenuTreeList(String addon);

    JSONArray getAdminAuthMenuList(Boolean isTree, String addon);
    
    /**
     * 获取管理员菜单列表（与PHP逻辑一致）
     *
     * @param isTree   是否树形结构
     * @param status   状态
     * @param addon    插件
     * @param isButton 是否按钮
     * @return 菜单列表
     */
    JSONArray getAdminAuthMenuList(Boolean isTree, Integer status, String addon, Integer isButton);

    JSONArray getSiteAuthMenuList(Boolean isTree, String addon);

    /**
     * 获取授权用户信息
     */
    SiteUserVo getAuthUserInfo();

    /**
     * 修改登录用户单个字段
     */
    void modifyAuth(String field, String value);

    /**
     * 更新用户
     *
     * @param editAuthUserParam
     */
    void editAuth(EditAuthUserParam editAuthUserParam);

    void sitePassword(EditManagerPwdParam param);

    SysUserRoleInfoVo getAuthRole(Integer siteId);

}
