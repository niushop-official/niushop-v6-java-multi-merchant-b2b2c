package com.niu.core.service.site.shop.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.service.admin.site.vo.SiteAdminVo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
* 店铺扩展表详情
*/
@Data
public class ShopSiteInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 站点id */
    private Integer siteId;

    /** 站点名称 */
    private String siteName;

    private List<String> appName;

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    private String expireTime;

    private String categoryName;

    private Integer isSelf;

}