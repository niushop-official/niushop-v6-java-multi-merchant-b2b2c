package com.niu.core.service.site.delivery.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * ShopDeliveryCompany列表视图
 */
@Data
public class DeliveryPackageListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 业务ID或编号
     */
    private String tradeNo;

    /**
     * 包裹名称
     */
    private String name;

    /**
     * 配送方式
     */
    private String deliveryType;

    /**
     * 详细配送方式
     */
    private String subDeliveryType;

    /**
     * 快递公司ID
     */
    private Integer expressCompanyId;

    /**
     * 配送单号
     */
    private String expressNumber;

    /**
     * 创建时间（时间戳）
     */
    private Integer createTime;

    /**
     * 配送单号
     */
    private Integer companyId;

    /**
     * 配送单号
     */
    private String companyName;

    /**
     * 配送单号
     */
    private String expressNo;


}