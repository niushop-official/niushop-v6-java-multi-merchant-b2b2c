package com.niu.core.service.admin.goods.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.goods.BrandAuditEnum;
import com.niu.core.enums.goods.BrandPopularEnum;
import com.niu.core.enums.goods.BrandStatusEnum;
import com.niu.core.enums.goods.BrandTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品品牌列表视图
 */
@Data
public class GoodsBrandListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer brandId;
    private Integer siteId;
    private SiteInfoVO site;
    private String siteName;
    private String frontEndLogo;
    private String brandName;
    private String brandType;
    private String brandTypeName;
    private String logo;
    private String desc;
    private Integer sort;

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime;

    private Integer status;
    private Integer isHot;
    private Integer auditStatus;
    private String auditReason;

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long auditTime;

    private String auditStatusName;
    private String statusName;
    private String isHotName;
    private String addon;

    public String getStatusName() {
        return BrandStatusEnum.getNameByType(this.status);
    }

    public String getIsHotName() {
        return BrandPopularEnum.getNameByType(this.isHot);
    }

    public String getAuditStatusName() {
        return BrandAuditEnum.getNameByType(this.auditStatus);
    }

    public String getBrandTypeName() {
        return BrandTypeEnum.getNameByType(this.brandType);
    }

    @Data
    public static class SiteInfoVO {
        private Integer siteId;
        private String siteName;
        private String frontEndLogo;
    }
}
