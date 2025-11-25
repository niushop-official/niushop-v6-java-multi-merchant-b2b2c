package com.niu.core.service.admin.sys.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 后台管理员参数
 */
@Data
public class SysUserAllSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户名称 */
    private String username;

    /** 用户名称 */
    private String realName;

    /** 注册时间 */
    private String[] createTime;

}
