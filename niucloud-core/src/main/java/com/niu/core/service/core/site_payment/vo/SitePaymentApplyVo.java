package com.niu.core.service.core.site_payment.vo;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.entity.site.Site;
import com.niu.core.enums.mall_huifu.ParamTypeEnum;
import com.niu.core.enums.site_payment.SitePaymentApplyStatusEnum;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 站点支付申请信息 VO
 */
@Data
public class SitePaymentApplyVo implements Serializable {

    @Serial
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
     * 失败原因
     */
    private String failReason;

    /**
     * 审核时间（时间戳）
     */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long auditTime;

    /**
     * 申请时间（时间戳）
     */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long applyTime;

    /**
     * 创建时间（时间戳）
     */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    /**
     * 扩展编号
     */
    private String extendNo;

    private String type;

    private String typeName;

    private String statusName;

    private String siteName;

    private String userType;

    /**
     * 进件信息（JSON格式）
     */
    @JsonRawValue
    private Object infos;

    private Site site;

    public String getTypeName() {
        return ParamTypeEnum.getName(this.type);
    }

    public String getStatusName() {
        return SitePaymentApplyStatusEnum.getNameByStatus(this.status);
    }
}

