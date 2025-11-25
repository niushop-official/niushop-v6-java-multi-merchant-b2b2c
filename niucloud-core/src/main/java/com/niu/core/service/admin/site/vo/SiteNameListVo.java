package com.niu.core.service.admin.site.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Site列表视图
 */
@Data
public class SiteNameListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer siteId;
    /** 站点名称 */
    private String siteName;
    /** 站点logo */
    private String logo;
    /** 网站图标 */
    private String icon;
    /** 前台名称 */
    private String frontEndName;
    /** 前台名称 */
    private String frontEndLogo;
    /** 关注站点数 */
    private Integer supplyFollowNum;

}
