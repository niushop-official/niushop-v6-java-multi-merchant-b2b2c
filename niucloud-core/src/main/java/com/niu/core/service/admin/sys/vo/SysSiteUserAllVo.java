package com.niu.core.service.admin.sys.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.entity.sys.SysUser;
import com.niu.core.enums.member.StatusEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * SysRoleVo
 */
@Data
public class SysSiteUserAllVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer uid;  // 系统用户ID
    private String username;  // 用户账号
    private String headImg;  //
    private String realName;  // 实际姓名
    private String lastIp;  // 最后一次登录ip
    private Integer lastTime;  // 最后一次登录时间
    private Integer loginCount;  // 登录次数
    private Integer status;  //
    private Integer isDel;  //
    private Integer id;  //
    private Integer siteId;  // 站点id
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String roleIds;  // 角色id
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 添加时间
    private Integer isAdmin;  // 是否是超级管理员
}
