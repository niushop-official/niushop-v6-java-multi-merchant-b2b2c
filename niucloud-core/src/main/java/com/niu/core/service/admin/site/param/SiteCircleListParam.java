package com.niu.core.service.admin.site.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 站点商圈列表搜索参数（对齐 PHP adminapi site/circle/list）
 */
@Data
public class SiteCircleListParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 管理员uid
     */
    private Integer uid = 0;
}
