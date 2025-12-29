package com.niu.core.service.core.delivery_package.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 配送包裹信息VO
 */
@Data
public class DeliveryPackageVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 包裹ID */
    private Integer id;

    /** 站点ID */
    private Integer siteId;

    /** 业务类型 */
    private String tradeType;

    /** 业务ID或编号 */
    private String tradeNo;

    /** 配送单号 */
    private String code;

    /** 包裹名称 */
    private String name;

    /** 配送方式 */
    private String deliveryType;

    /** 详细配送方式 */
    private String subDeliveryType;

    /** 快递公司ID */
    private Integer expressCompanyId;

    /** 配送单号 */
    private String expressNumber;

    /** 同城配送员ID */
    private Integer localDeliverId;

    /** 包裹状态 */
    private String status;

    /** 创建时间（时间戳） */
    private Integer createTime;

    /** 快递公司ID */
    private Integer companyId;

    /** 快递公司名称 */
    private String companyName;

    /** 快递编号 */
    private String expressNo;
}
