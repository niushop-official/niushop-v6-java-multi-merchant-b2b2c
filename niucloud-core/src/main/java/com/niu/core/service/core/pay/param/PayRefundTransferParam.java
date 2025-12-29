package com.niu.core.service.core.pay.param;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 支付记录参数
*/
@Data
public class PayRefundTransferParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;


    private String refundNo;


    private String refundType = "back";


    private String voucher;

    private String mainType;
    private Integer mainId;
}
