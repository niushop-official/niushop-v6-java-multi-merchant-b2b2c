package com.niu.core.service.admin.sys.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 小程序上架设置视图
 */
@Data
public class SysWeappListSettingVo implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * 是否开启 (0=关闭, 1=开启)
     */
    private Integer isOpen = 0;
}

