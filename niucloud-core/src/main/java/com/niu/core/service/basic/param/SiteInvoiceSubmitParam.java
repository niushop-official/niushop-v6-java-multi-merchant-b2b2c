package com.niu.core.service.basic.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 发票表参数
 */
@Data
public class SiteInvoiceSubmitParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "invoiceNumber参数缺失")
    private String invoiceNumber;

    @NotNull(message = "invoiceVoucher参数缺失")
    private String invoiceVoucher;

    private String remark;



}
