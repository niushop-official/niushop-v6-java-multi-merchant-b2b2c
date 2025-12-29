package com.niu.core.service.site.basic.param;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 发票配置参数
 */
@Data
public class SiteInvoiceConfigParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否开启发票功能：1-开启，2-关闭
     */
    private String isInvoice;
    
    /**
     * 发票类型
     */
    private List<String> invoiceType = new ArrayList<>();
    
    /**
     * 发票内容
     */
    private List<String> invoiceContent = new ArrayList<>();
} 