package com.niu.core.service.admin.site.vo;

import com.niu.core.entity.site.SitePayment;
import com.niu.core.entity.site.SitePaymentApply;
import lombok.Data;

import java.util.Map;

@Data
public class SitePaymentTypeVo {
    /**
     * 支付类型key
     */
    private String key;

    /**
     * 支付方式名称
     */
    private String name;

    /**
     * 支付方式描述
     */
    private String desc;

    /**
     * 支付方式图标
     */
    private String icon;

    /**
     * 管理端配置URL
     */
    private String adminUrl;

    /**
     * 站点端配置URL
     */
    private String siteUrl;

    /**
     * 站点信息URL
     */
    private String siteInfoUrl;

    /**
     * 站点支付配置（实体类）
     */
    private  SitePayment sitePayment;

    /**
     * 站点支付申请信息
     */
    private  SitePaymentApply sitePaymentApply;
}
