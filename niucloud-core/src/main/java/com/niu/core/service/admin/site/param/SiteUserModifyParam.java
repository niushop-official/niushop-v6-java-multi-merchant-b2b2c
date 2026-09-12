package com.niu.core.service.admin.site.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 站点用户单字段修改参数
 */
@Data
public class SiteUserModifyParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String value;
}
