package com.niu.core.service.admin.site.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 结算账户搜索参数
 */
@Data
public class SiteSettAccountSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 账户类型 */
    private Integer type;
}