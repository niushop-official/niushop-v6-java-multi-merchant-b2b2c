package com.niu.core.service.core.user.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.crypto.PasswordEncipher;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.sys.SysUser;
import com.niu.core.entity.sys.SysUserRole;
import com.niu.core.enums.member.StatusEnum;
import com.niu.core.enums.sys.AppTypeEnum;
import com.niu.core.mapper.sys.SysUserMapper;
import com.niu.core.mapper.sys.SysUserRoleMapper;
import com.niu.core.service.admin.auth.ILoginService;
import com.niu.core.service.admin.site.param.SiteUserParam;
import com.niu.core.service.admin.sys.ISysUserRoleService;
import com.niu.core.service.admin.sys.param.SysUserRoleParam;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import com.niu.core.service.core.user.ICoreUserRoleService;
import com.niu.core.service.core.user.ICoreUserService;
import com.niu.core.service.core.user.dto.UserInfoDto;
import com.niu.core.service.core.user.param.CoreUserAddParam;
import com.niu.core.service.core.user.param.CoreUserEditParam;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class CoreUserServiceImpl implements ICoreUserService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Resource
    private ISysUserRoleService userRoleService;

    @Resource
    private ICoreUserRoleService coreUserRoleService;

    @Resource
    private ICoreConfigService coreConfigService;

    @Resource
    private Cached cached;

    @Resource
    @Lazy
    private ILoginService loginService;

    @Override
    public UserInfoDto getUserInfo(Integer uid) {
        SysUser sysUser = sysUserMapper.selectById(uid);
        UserInfoDto result = new UserInfoDto();
        BeanUtils.copyProperties(sysUser, result);
        result.setRole(userRoleService.getUserRole(RequestUtils.siteId(), uid));
        return result;
    }

    /**
     * 获取管理员信息
     * @param uid
     * @return
     */
    @Override
    public SysUser getInfoByUid(Integer uid) {
        SysUser sysUser = sysUserMapper.selectById(uid);
        return sysUser != null ? sysUser : new SysUser();
    }

    @Override
    public boolean checkUsername(String username) {
        return sysUserMapper.exists(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getIsDel, 0)
                .eq(SysUser::getUsername, username));
    }

    /**
     * 通过手机号创建管理员
     * @param mobile
     * @return
     */
    @Override
    public Integer createUserByMobile(String mobile) {
        SysUser existingUser = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getMobile, mobile)
                        .last("limit 1")
        );
        if (existingUser != null) {
            return existingUser.getUid();
        }
        SysUser newUser = new SysUser();
        newUser.setMobile(mobile);
        newUser.setStatus(1);
        sysUserMapper.insert(newUser);
        return newUser.getUid();
    }

    /**
     * 获取消息接收人uid
     * @param siteId
     * @return
     */
    @Override
    public Integer getNoticeUid(Integer siteId) {
        CoreSysConfigVo config = coreConfigService.getConfig(siteId, "NOTICE_RECIPIENT_INFO");
        if (config == null || config.getValueJsonObject().isEmpty()) {
            return 0;
        }
        return config.getValueJsonObject().getInt("uid", 0);
    }

    /**
     * 修改密码
     * @param uid
     * @param password
     */
    @Override
    public void modifyPassword(Integer uid, String password) {
        modifyPassword(find(uid), password);
    }

    @Override
    public void modifyPassword(SysUser user, String password) {
        if (user == null || user.getUid() == null) {
            throw new CommonException("USER_NOT_EXIST");
        }
        Integer uid = user.getUid();
        SysUser updateUser = new SysUser();
        updateUser.setUid(uid);
        updateUser.setPassword(PasswordEncipher.encode(password));
        updateUser.setUpdateTime(DateUtils.currTime());
        sysUserMapper.updateById(updateUser);
        loginService.clearToken(uid, AppTypeEnum.ADMIN.getName(), null);
        loginService.clearToken(uid, AppTypeEnum.SITE.getName(), null);
    }

    /**
     * 添加对应站点用户(添加站点，同时添加站点用户,用于添加站点以及站点添加站点用户)
     * @param siteUserParam
     * @param siteId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addSiteUser(SiteUserParam siteUserParam, Integer siteId) {
        Integer uid;
        if (ObjectUtil.isNotNull(siteUserParam.getUid()) && siteUserParam.getUid() > 0) {
            uid = siteUserParam.getUid();
            SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>()
                    .eq("uid", uid)
                    .select("uid", "mobile")
                    .last("limit 1"));
            if (user == null) {
                throw new CommonException("USER_NOT_EXIST");
            }
            if (StringUtils.isNotEmpty(siteUserParam.getMobile())) {
                SysUser updateUser = new SysUser();
                updateUser.setUid(uid);
                updateUser.setMobile(siteUserParam.getMobile());
                updateUser.setUpdateTime(DateUtils.currTime());
                sysUserMapper.updateById(updateUser);
            }
        } else {
            String username = siteUserParam.getUsername() == null ? "" : siteUserParam.getUsername().replace(" ", "");
            if (checkUsername(username)) {
                throw new CommonException("USERNAME_REPEAT");
            }
            CoreUserAddParam addParam = new CoreUserAddParam();
            addParam.setUsername(username);
            addParam.setHeadImg(siteUserParam.getHeadImg());
            addParam.setStatus(siteUserParam.getStatus());
            addParam.setRealName(siteUserParam.getRealName());
            addParam.setPassword(siteUserParam.getPassword());
            addParam.setMobile(siteUserParam.getMobile());
            uid = add(addParam);
        }

        List<String> roleIds = siteUserParam.getRoleIds() != null ? siteUserParam.getRoleIds() : new ArrayList<>();
        Integer isAdmin = siteUserParam.getIsAdmin() == null ? 0 : siteUserParam.getIsAdmin();
        Integer status = siteUserParam.getStatus() == null ? StatusEnum.ON.getStatus() : siteUserParam.getStatus();

        SysUserRoleParam sysUserRoleParam = new SysUserRoleParam();
        sysUserRoleParam.setUid(uid);
        sysUserRoleParam.setSiteId(siteId);
        sysUserRoleParam.setIsAdmin(isAdmin);
        sysUserRoleParam.setStatus(status);
        if (isAdmin > 0) {
            sysUserRoleParam.setRoleIds(JSONUtil.toJsonStr(new ArrayList<>()));
        } else {
            sysUserRoleParam.setRoleIds(JSONUtil.toJsonStr(roleIds));
        }
        coreUserRoleService.add(sysUserRoleParam);
        cached.remove("user_role_list_" + uid);
        return uid;
    }

    /**
     * 更新对应站点用户
     * @param uid
     * @param siteUserParam
     * @param siteId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editSiteUser(Integer uid, SiteUserParam siteUserParam, Integer siteId) {
        CoreUserEditParam editParam = new CoreUserEditParam();
        editParam.setHeadImg(siteUserParam.getHeadImg());
        editParam.setRealName(siteUserParam.getRealName());
        editParam.setMobile(siteUserParam.getMobile());
        editParam.setStatus(siteUserParam.getStatus());
        if (StringUtils.isNotEmpty(siteUserParam.getPassword())) {
            editParam.setPassword(siteUserParam.getPassword());
        }
        editOfColumn(uid, editParam, siteId);

        List<String> roleIds = siteUserParam.getRoleIds() != null ? siteUserParam.getRoleIds() : new ArrayList<>();
        SysUserRoleParam roleParam = new SysUserRoleParam();
        roleParam.setUid(uid);
        roleParam.setSiteId(siteId);
        roleParam.setRoleIds(JSONUtil.toJsonStr(roleIds));
        coreUserRoleService.edit(roleParam);
    }

    /**
     * 编辑用户部分
     * @param uid
     * @param param
     * @param siteId
     */
    @Override
    public void editOfColumn(Integer uid, CoreUserEditParam param, Integer siteId) {
        SysUser user = find(uid);
        SysUser userData = new SysUser();
        userData.setUid(uid);
        boolean hasUserData = false;
        boolean isOffStatus = false;
        boolean isChangePassword = false;

        if (param.getStatus() != null) {
            if (siteId != null) {
                statusChange(uid, param.getStatus(), siteId);
                if (StatusEnum.OFF.getStatus().equals(param.getStatus())) {
                    isOffStatus = true;
                }
            } else {
                userData.setStatus(param.getStatus());
                hasUserData = true;
                if (StatusEnum.OFF.getStatus().equals(param.getStatus())) {
                    isOffStatus = true;
                }
            }
        }
        if (param.getHeadImg() != null) {
            userData.setHeadImg(param.getHeadImg());
            hasUserData = true;
        }
        if (param.getRealName() != null) {
            userData.setRealName(param.getRealName());
            hasUserData = true;
        }
        if (param.getMobile() != null) {
            userData.setMobile(param.getMobile());
            hasUserData = true;
        }
        if (StringUtils.isNotEmpty(param.getPassword()) && !PasswordEncipher.matche(param.getPassword(), user.getPassword())) {
            userData.setPassword(PasswordEncipher.encode(param.getPassword()));
            hasUserData = true;
            isChangePassword = true;
        }

        if (!hasUserData) {
            return;
        }
        userData.setUpdateTime(DateUtils.currTime());
        sysUserMapper.updateById(userData);
        if (isOffStatus || isChangePassword) {
            loginService.clearToken(uid, null, null);
        }
    }

    /**
     * 添加用户（添加用户，不添加站点）
     * @param param
     * @return
     */
    @Override
    public Integer add(CoreUserAddParam param) {
        SysUser user = new SysUser();
        user.setUsername(param.getUsername());
        user.setHeadImg(param.getHeadImg());
        user.setStatus(param.getStatus());
        user.setRealName(param.getRealName());
        user.setPassword(PasswordEncipher.encode(param.getPassword()));
        if (StringUtils.isNotEmpty(param.getMobile())) {
            user.setMobile(param.getMobile());
        }
        user.setCreateTime(DateUtils.currTime());
        user.setUpdateTime(DateUtils.currTime());
        user.setLoginCount(0);
        user.setIsDel(0);
        sysUserMapper.insert(user);
        return user.getUid();
    }

    /**
     * 编辑用户
     * @param uid
     * @param param
     */
    @Override
    public void edit(Integer uid, CoreUserEditParam param) {
        editOfColumn(uid, param, null);
    }

    /**
     * 用户查找对象
     * @param uid
     * @return
     */
    @Override
    public SysUser find(Integer uid) {
        SysUser user = sysUserMapper.selectById(uid);
        Assert.notNull(user, "USER_NOT_EXIST");
        return user;
    }

    /**
     * 改变用户状态
     * @param uid
     * @param status
     * @param siteId
     */
    @Override
    public void statusChange(Integer uid, Integer status, Integer siteId) {
        sysUserRoleMapper.update(null, new UpdateWrapper<SysUserRole>()
                .eq("uid", uid)
                .eq("site_id", siteId)
                .set("status", status));
        loginService.clearToken(uid, null, null);
        cached.remove("user_role_list_" + uid);
        cached.remove("user_role_" + uid + "_" + siteId);
    }
}
