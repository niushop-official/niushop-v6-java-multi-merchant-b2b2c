package com.niu.core.service.core.site_payment.vo;

import com.niu.core.enums.site_payment.SitePaymentApplyStatusEnum;
import com.niu.core.enums.site_payment.SitePaymentStatusEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 站点支付申请信息 VO
 */
@Data
public class SitePaymentListVo implements Serializable {

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
     * 申请状态
     */
    private String type;

    /**
     * 创建时间（时间戳）
     */
    private Integer createTime;

    /**
     * 扩展编号
     */
    private String extendNo;

    /**
     * 是否结算（0:否, 1:是）
     */
    private Integer isSettle;

    private String withdrawalNo;
    /**
     * 结算开通情况描述
     */
    private String settleDesc;

    private String typeName;

    public String getTypeName() {
        return SitePaymentStatusEnum.getNameByStatus(this.type);
    }

}

