package com.niu.core.service.site.goods.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.goods.BrandAuditEnum;
import com.niu.core.enums.goods.BrandPopularEnum;
import com.niu.core.enums.goods.BrandStatusEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * ShopGoodsBrand列表视图
 */
@Data
public class GoodsBrandListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer brandId;  // 品牌ID
    private Integer siteId;  // 站点id
    private SiteInfoVO site;  // 站点信息
    private String siteName;  // 站点名称
    private String frontEndLogo;
    private String brandName;  // 品牌名称
    private String brandType;  // 品牌类型
    private String brandTypeName;  // 品牌类型名称
    private String logo;  // 品牌logo
    private String desc;  // 品牌介绍
    private Integer sort;  // 排序
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 创建时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime; // 更新时间

    private Integer status;  // 排序
    private Integer auditStatus;  // 排序
    private String auditReason;  // 排序
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long auditTime;  // 排序
    private String auditStatusName;
    private String statusName;

    public String getStatusName() {
        return BrandStatusEnum.getNameByType(this.status);
    }
    public String getAuditStatusName() {
        return BrandAuditEnum.getNameByType(this.auditStatus);
    }

    @Data
    public static class SiteInfoVO{
        private Integer siteId;
        private String siteName;
        private String frontEndLogo;

    }

}
