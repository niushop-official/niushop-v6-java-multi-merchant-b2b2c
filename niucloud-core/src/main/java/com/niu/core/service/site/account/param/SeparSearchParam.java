package com.niu.core.service.site.account.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 参数
 */
@Data
public class SeparSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**  */
    private String[] createTime;

    /** 交易流水号(主要用于交互支付单据) */
    private String outTradeNo;

    /**  */
    private String payType;

    /** 业务单号 */
    private String tradeNo;

    /** 业务类型 */
    private String tradeType;


}
