package com.niu.core.service.core.invoice.param;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 发票配置参数
 */
@Data
public class InvoiceConfigParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 站点ID */
    private Integer siteId;

    /** 是否开启发票功能：1-开启，2-关闭 */
    private String isInvoice;
}
