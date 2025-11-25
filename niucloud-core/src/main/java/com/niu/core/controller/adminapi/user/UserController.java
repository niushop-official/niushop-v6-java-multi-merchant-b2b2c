package com.niu.core.controller.adminapi.user;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.entity.sys.SysUser;
import com.niu.core.entity.user.UserCreateSiteLimit;
import com.niu.core.enums.sys.UserEnum;
import com.niu.core.service.admin.site.param.SiteGroupSearchParam;
import com.niu.core.service.admin.site.param.SiteUserSearchParam;
import com.niu.core.service.admin.site.vo.SiteUserVo;
import com.niu.core.service.admin.sys.ISysUserService;
import com.niu.core.service.admin.sys.param.*;
import com.niu.core.service.admin.sys.vo.SysUserCreateSiteLimitVo;
import com.niu.core.service.admin.sys.vo.SysUserDetailVo;
import com.niu.core.service.admin.sys.vo.SysUserInfoVo;
import com.niu.core.service.admin.sys.vo.SysUserListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 后台管理员管理
 */
@RestController
@RequestMapping("/adminapi/user")
@SaCheckLogin
public class UserController {

    @Resource
    ISysUserService sysUserService;

    /**
     * 后台管理员列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return Result<Object>
     */
    @GetMapping("/user")
    public Result<PageResult<SiteUserVo>> list(@Validated PageParam pageParam,
                                                  @Validated SiteUserSearchParam searchParam) {
        PageResult<SiteUserVo> list = sysUserService.getAdminPage(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 后台管理员详情
     *
     * @param id 主键ID
     * @return Result<SysUserInfoVo>
     */
    @GetMapping("/user/{id}")
    public Result<SiteUserVo> info(@Validated @PathVariable("id") Integer id) {
        SiteUserVo detail = sysUserService.getAdminUserInfo(id);
        return Result.success(detail);
    }

    /**
     * 后台管理员添加
     *
     * @param addParam 参数
     * @return Result<Object>
     */
    @PostMapping("/user")
    public Result<Object> add(@Validated @RequestBody SysUserAddParam addParam) {
        SysUserParam sysUserParam=new SysUserParam();
        sysUserParam.setUsername(addParam.getUsername());
        sysUserParam.setStatus(UserEnum.ON.getCode());
        sysUserParam.setRealName("");
        sysUserParam.setPassword(addParam.getPassword());
        sysUserParam.setIsDel(0);
        sysUserParam.setHeadImg("");
        sysUserParam.setCreateSiteLimitParamList(addParam.getCreateSiteLimit());
        sysUserParam.setMobile(addParam.getMobile());
        sysUserService.add(sysUserParam);
        return Result.success();
    }


    /**
     * 查询账号是否存在
     *
     * @param username
     * @return
     */
    @GetMapping("/isexist")
    public Result<Object> checkUserIsExist(@Validated String username){
        return Result.success(sysUserService.checkUserName(username));
    }

    /**
     * 获取用户站点创建限制
     *
     * @param uid
     * @return
     */
    @GetMapping("/user/create_site_limit/{uid}")
    public Result<List<SysUserCreateSiteLimitVo>> getUserCreateSiteLimit(@Validated @PathVariable("uid") Integer uid){
        return Result.success(sysUserService.getUserCreateSiteLimit(uid));
    }


    /**
     * 获取用户站点创建限制
     *
     * @param id
     * @return
     */
    @GetMapping("/user/create_site_limit/info/{id}")
    public Result<SysUserCreateSiteLimitVo> getUserCreateSiteLimitInfo(@Validated @PathVariable("id") Integer id){
        return Result.success(sysUserService.getUserCreateSiteLimitInfo(id));
    }

    /**
     * 添加用户站点创建限制
     *
     * @param sysUserCreateSiteLimitAddParam
     * @return
     */
    @PostMapping("/user/create_site_limit")
    public Result<Object> addUserCreateSiteLimit(@Validated @RequestBody SysUserCreateSiteLimitAddParam sysUserCreateSiteLimitAddParam){
        sysUserService.addUserCreateSiteLimit(sysUserCreateSiteLimitAddParam);
        return Result.success();
    }

    /**
     * 编辑用户站点创建限制
     *
     * @param id
     * @param userCreateSiteLimitEditParam
     * @return
     */
    @PutMapping("/user/create_site_limit/{id}")
    public Result<Object> editUserCreateSiteLimit(@Validated @PathVariable("id") Integer id, @Validated @RequestBody SysUserCreateSiteLimitEditParam userCreateSiteLimitEditParam){
        userCreateSiteLimitEditParam.setId(id);
        sysUserService.editUserCreateSiteLimit(userCreateSiteLimitEditParam);
        return Result.success();
    }

    /**
     * 删除用户站点创建限制
     *
     * @param id
     * @return
     */
    @DeleteMapping("/user/create_site_limit/{id}")
    public Result<Object> delUserCreateSiteLimit(@Validated @PathVariable("id") Integer id){
        sysUserService.delUserCreateSiteLimit(id);
        return Result.success();
    }

    /**
     * 后台全部管理员
     *
     */
    @GetMapping("/user_all")
    public Result<Object> userAll(SysUserAllSearchParam searchParam) {

        return Result.success(sysUserService.getUserAll(searchParam));
    }

    /**
     * 后台全部管理员
     *
     */
    @GetMapping("/site/user_all")
    public Result<Object> siteUserAll(SiteUserSearchParam searchParam) {

        return Result.success(sysUserService.getSiteUserAll(searchParam));
    }

    /**
     * 站点用户列表
     *
     */
    @GetMapping("/site/user_list")
    public Result<Object> siteUserList(SiteUserSearchParam searchParam) {

        return Result.success(sysUserService.getSiteUserList(searchParam));
    }



}
