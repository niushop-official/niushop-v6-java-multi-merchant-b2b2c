package com.niu.mall.service.site.order.param;
import lombok.Data;
import java.io.Serializable;

import jakarta.validation.constraints.*;

/**
 * 发票参数
*/
@Data
public class MallInvoiceParam implements Serializable {

    private static final long serialVersionUID = 1L;


    @NotNull(message = "invoiceNumber参数缺失")
    private String invoiceNumber;

    @NotNull(message = "invoiceVoucher参数缺失")
    private String invoiceVoucher;

    private String remark;


}
