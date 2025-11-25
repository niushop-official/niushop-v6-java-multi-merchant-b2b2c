package com.niu.core.service.api.order.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.basic.InvoiceHeaderTypeEnum;
import com.niu.core.enums.basic.InvoiceTypeEnum;
import com.niu.core.enums.basic.InvoicingEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.core.sys.ICoreUploadService;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ShopInvoice列表视图
 */
@Data
public class InvoiceConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String isInvoice;
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String invoiceType;
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String invoiceContent;
}
