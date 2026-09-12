package com.niu.core.event.fenxiao;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 分销订单数据（通用，跨业务类型）
 */
@Data
public class FenxiaoOrderData implements Serializable {

    /**
     * 站点ID
     */
    private Integer siteId;

    /**
     * 订单ID
     */
    private Integer orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 会员ID
     */
    private Integer memberId;

    /**
     * 订单金额
     */
    private BigDecimal orderMoney;
}
