package com.niu.core.controller.adminapi.auth;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.sys.SysMenu;
import com.niu.core.service.admin.auth.IAuthService;
import com.niu.core.service.admin.auth.ILoginService;
import com.niu.core.service.admin.auth.param.EditAuthUserParam;
import com.niu.core.service.admin.auth.param.EditManagerPwdParam;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.param.SiteUserModifyParam;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.admin.site.vo.SiteUserVo;
import com.niu.core.service.admin.sys.ISysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.List;

/**
 * 菜单管理
 */
@Tag(name = "管理端鉴权")
@RestController
@RequestMapping("/adminapi/auth")
@SaCheckLogin
public class AuthController {

    @Resource
    ISysMenuService sysMenuService;

    @Resource
    IAuthService authService;

    @Resource
    ISiteService siteService;

    @Resource
    ILoginService loginService;


    /**
     * 登录用户菜单列表接口
     *
     * @return
     */
    @Operation(summary = "获取平台菜单")
    @GetMapping("/admin/authmenu")
    public Result<JSONArray> getAdminAuthMenuList() {
        return Result.success(authService.getAdminAuthMenuList(true, "all"));
    }

    /**
     * 登录用户菜单列表接口
     *
     * @return
     */
    @Operation(summary = "获取站点菜单")
    @GetMapping("/site/authmenu")
    public Result<JSONArray> getSiteAuthMenuList() {
        return Result.success(authService.getSiteAuthMenuList(true, "all"));
    }
    /**
     * 获取当前登录站点信息
     *
     * @return
     */
    @Operation(summary = "获取当前站点信息")
    @GetMapping("/site")
    public Result<SiteInfoVo> siteInfo() {
        return Result.success(siteService.info(RequestUtils.siteId()));
    }

    /**
     * 站点可以单独显示的菜单
     */
    @Operation(summary = "获取站点可显示菜单")
    @GetMapping("/site/showmenu")
    public Result<List<SysMenu>> getShowMenuList() {
        return Result.success(siteService.getShowMenuList(RequestUtils.siteId()));
    }

    /**
     * 获取当前登录用户的信息
     *
     * @return
     */
    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/get")
    public Result<SiteUserVo> getAuthUserinfo() {
        return Result.success(authService.getAuthUserInfo());
    }

    /**
     * 修改登录用户单个字段
     */
    @Operation(summary = "修改当前用户字段")
    @PutMapping("/modify/{field}")
    public Result<Object> modify(@PathVariable("field") String field, @RequestBody SiteUserModifyParam param) {
        authService.modifyAuth(field, param.getValue());
        return Result.success();
    }

    /**
     * 菜单树
     *
     * @return
     */
    @Operation(summary = "获取菜单树")
    @GetMapping("/tree")
    public Result<JSONArray> tree() {
        return Result.success(sysMenuService.menuTree());
    }


    /**
     * 更新用户
     *
     * @param editAuthUserParam
     * @return
     */
    @Operation(summary = "更新当前用户资料")
    @PutMapping("/edit")
    public Result<Object> edit(@Validated @RequestBody EditAuthUserParam editAuthUserParam){
        authService.editAuth(editAuthUserParam);
        return Result.success();
    }
    /**
     * 退出登录
     *
     * @return
     */
    @Operation(summary = "退出登录")
    @PutMapping("/logout")
    public Result<Object> logout(){
        loginService.logout();
        return Result.success();
    }
    @Operation(summary = "修改站点管理员密码")
    @PutMapping("/site/password/{id}")
    public Result<Object> sitePassword(@PathVariable("id") Integer id, @RequestBody EditManagerPwdParam param) {
        param.setId(id);
        authService.sitePassword(param);
        return Result.success();
    }
}
