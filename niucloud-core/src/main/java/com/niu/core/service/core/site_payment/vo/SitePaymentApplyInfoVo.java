package com.niu.core.service.core.site_payment.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 站点支付申请信息 VO
 */
@Data
public class SitePaymentApplyInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 站点ID
     */
    private Integer siteId;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 申请状态
     */
    private String status;

    /**
     * 进件信息（JSON格式）
     */
    private Object infos;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 审核时间（时间戳）
     */
    private Integer auditTime;

    /**
     * 申请时间（时间戳）
     */
    private Integer applyTime;

    /**
     * 创建时间（时间戳）
     */
    private Integer createTime;

    /**
     * 扩展编号
     */
    private String extendNo;

    /**
     * 关联的支付信息
     */
    private SitePaymentVo sitePayment;

    /**
     * 站点支付信息内部类
     */
    @Data
    public static class SitePaymentVo implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 状态
         */
        private String status;

        /**
         * 扩展编号
         */
        private String extendNo;

        /**
         * 是否结算（0:否, 1:是）
         */
        private Integer isSettle;

        /**
         * 结算开通情况描述
         */
        private String settleDesc;
    }
}

