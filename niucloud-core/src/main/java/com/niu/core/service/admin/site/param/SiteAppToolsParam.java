package com.niu.core.service.admin.site.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 站点应用工具参数
 */
@Data
public class SiteAppToolsParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应用标题（用于搜索）
     */
    private String title;
}

