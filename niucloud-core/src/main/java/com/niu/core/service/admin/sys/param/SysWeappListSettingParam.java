package com.niu.core.service.admin.sys.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 小程序上架设置参数
 */
@Data
public class SysWeappListSettingParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer isOpen;
}

