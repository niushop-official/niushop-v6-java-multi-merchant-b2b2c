package com.niu.core.service.core.invoice.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 发票配置
 */
@Data
public class InvoiceConfigVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 发票开关配置 */
    private Invoice invoice = new Invoice();

    /**
     * 发票开关
     */
    @Data
    public static class Invoice implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /** 是否开启发票功能：1-开启，2-关闭 */
        @JsonProperty("is_invoice")
        private String isInvoice = "2";
    }
}
