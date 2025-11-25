package com.niu.mall.service.site.order.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 发票参数
 */
@Data
public class MallInvoiceSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer headerType;

    private String headerName;

    private String isInvoice;

    private String[] createTime;

    private String[] invoiceTime;



}
