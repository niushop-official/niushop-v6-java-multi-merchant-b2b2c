package com.niu.core.service.core.user.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 核心用户添加参数
 */
@Data
public class CoreUserAddParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;

    private String headImg;

    private Integer status;

    private String realName;

    private String password;

    private String mobile;
}
