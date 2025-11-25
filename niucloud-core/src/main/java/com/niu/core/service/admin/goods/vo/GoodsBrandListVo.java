package com.niu.core.service.admin.goods.vo;

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
    private String logo;  // 品牌logo
    private String desc;  // 品牌介绍
    private Integer sort;  // 排序
    private String createTime; // 创建时间
    private String updateTime; // 更新时间

    @Data
    public static class SiteInfoVO{
        private Integer siteId;
        private String siteName;
        private String frontEndLogo;

    }

}
