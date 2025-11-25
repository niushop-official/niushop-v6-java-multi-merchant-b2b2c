package com.niu.mall.service.core.order.vo;

import com.niu.mall.entity.order.config.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单配置数据
 */
@Data
public class InvoiceInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String isInvoice;
    private String invoiceType;
    private String invoiceContent;



}
