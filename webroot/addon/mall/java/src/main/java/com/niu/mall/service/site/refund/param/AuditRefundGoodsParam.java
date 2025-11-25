package com.niu.mall.service.site.refund.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 订单退款参数
 */
@Data
public class AuditRefundGoodsParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderRefundNo;

    @NotNull(message = "is_agree参数缺失")
    private Integer isAgree;

    private String shopReason = "";

    private Integer mainId;

    private String mainType;

    private Integer siteId;
}
