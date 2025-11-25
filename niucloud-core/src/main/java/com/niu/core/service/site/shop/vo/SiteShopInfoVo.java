package com.niu.core.service.site.shop.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
* 店铺扩展表详情
*/
@Data
public class SiteShopInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 站点id */
    private Integer siteId;

    /** 站点名称 */
    private String siteName;

    /** 站点logo */
    private String logo;

    /** 简介 */
    private String desc;

    /** 网站图标 */
    private String icon;

    /** 前台名称 */
    private String frontEndName;

    /** 前台logo */
    private String frontEndLogo;
    /** 前台logo */
    private shopInfo shop;
    /** 前台logo */
    private List<serviceInfo> serviceList;

    @Data
    public static class shopInfo{
        private Integer supplyFollowNum;
    }

    @Data
    public static class serviceInfo{
        private String serviceName;
        private String image;
    }

}