package com.niu.core.service.api.order.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.service.api.order.vo.InvoiceListVo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 发票参数
 */
@Data
public class InvoiceApplyParam implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer tradeId;  // 业务id
    private String tradeType;  // 开票分类 order:订单
    private Integer headerType;  // 抬头类型
    private String headerName;  // 名称（发票抬头）
    private Integer type;  // 发票类型
    private String contentType;  // 名称（发票抬头）
    private String taxNumber;  // 公司税号
    private String mobile;  // 开票人手机号
    private String email;  // 开票人邮箱
    private String telephone;  // 注册电话
    private String address;  // 注册地址
    private String bankName;  // 开户银行
    private String bankCardNumber;  // 银行账号
}
