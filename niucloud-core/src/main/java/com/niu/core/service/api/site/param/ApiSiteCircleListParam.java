package com.niu.core.service.api.site.param;

import lombok.Data;

import java.io.Serializable;

/**
 * C端站点商圈列表搜索参数（对齐 PHP api site/circle/list）
 */
@Data
public class ApiSiteCircleListParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商圈名称
     */
    private String name = "";
}
