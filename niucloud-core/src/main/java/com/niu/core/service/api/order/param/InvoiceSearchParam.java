package com.niu.core.service.api.order.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 发票参数
 */
@Data
public class InvoiceSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String headerType ;

    private String headerName;

    private String isInvoice ;

    private String[] createTime;

    private String[] invoiceTime;



}
