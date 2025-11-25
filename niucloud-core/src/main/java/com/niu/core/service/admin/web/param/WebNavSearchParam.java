package com.niu.core.service.admin.web.param;

import lombok.Data;

import java.io.Serializable;

/**
 * PC导航管理参数
 */
@Data
public class WebNavSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 导航名称 */
    private String navTitle;



}
