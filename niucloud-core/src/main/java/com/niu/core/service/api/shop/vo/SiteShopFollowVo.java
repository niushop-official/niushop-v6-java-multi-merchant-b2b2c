package com.niu.core.service.api.shop.vo;


import com.niu.core.entity.site.Site;
import lombok.Data;

@Data
public class SiteShopFollowVo {


    private Integer id;
    private String siteName;
    private Integer siteId;
    private String logo;
    private String icon;
    private String frontEndLogo;  // 前台Logo
    private Site site;
}
