package com.niu.mall.service.admin.site_shop.vo;

import com.niu.core.service.admin.site.vo.SiteAdminVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SiteAdminWithSiteIdVo extends SiteAdminVo {

    private Integer siteId;
}
