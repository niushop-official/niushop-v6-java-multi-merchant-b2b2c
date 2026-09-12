package com.niu.core.service.core.user;

import com.niu.core.entity.sys.SysUser;
import com.niu.core.service.admin.site.param.SiteUserParam;
import com.niu.core.service.core.user.dto.UserInfoDto;
import com.niu.core.service.core.user.param.CoreUserAddParam;
import com.niu.core.service.core.user.param.CoreUserEditParam;

public interface ICoreUserService {

    /**
     * 获取当前登录用户详情
     */
    UserInfoDto getUserInfo(Integer uid);

    /**
     * 获取管理员信息
     */
    SysUser getInfoByUid(Integer uid);

    /**
     * 检验用户名是否已使用
     */
    boolean checkUsername(String username);

    /**
     * 通过手机号创建用户，如果用户已存在则返回现有用户id
     */
    Integer createUserByMobile(String mobile);

    /**
     * 获取消息接收人uid
     */
    Integer getNoticeUid(Integer siteId);

    /**
     * 修改密码
     */
    void modifyPassword(Integer uid, String password);

    /**
     * 修改密码
     */
    void modifyPassword(SysUser user, String password);

    /**
     * 添加对应站点用户
     */
    Integer addSiteUser(SiteUserParam siteUserParam, Integer siteId);

    /**
     * 更新对应站点用户
     */
    void editSiteUser(Integer uid, SiteUserParam siteUserParam, Integer siteId);

    /**
     * 编辑用户部分字段
     */
    void editOfColumn(Integer uid, CoreUserEditParam param, Integer siteId);

    /**
     * 添加用户（不添加站点）
     */
    Integer add(CoreUserAddParam param);

    /**
     * 编辑用户
     */
    void edit(Integer uid, CoreUserEditParam param);

    /**
     * 用户模型对象
     */
    SysUser find(Integer uid);

    /**
     * 改变用户站点状态
     */
    void statusChange(Integer uid, Integer status, Integer siteId);
}
