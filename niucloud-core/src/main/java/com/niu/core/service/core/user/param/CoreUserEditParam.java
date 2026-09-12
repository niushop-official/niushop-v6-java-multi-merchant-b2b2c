package com.niu.core.service.core.user.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 核心用户编辑参数
 */
@Data
public class CoreUserEditParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer status;

    private String headImg;

    private String realName;

    private String mobile;

    private String password;
}
