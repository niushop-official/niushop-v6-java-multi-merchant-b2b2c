package com.niu.mall.service.core.shop.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单退款参数
 */
@Data
public class OrderRefundParam implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 退款ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long refundId;
    
    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;
    
    /**
     * 店铺ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long siteId;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 退款单号
     */
    private String refundNo;
    
    /**
     * 退款金额
     */
    @NotNull(message = "退款金额不能为空")
    @Min(value = 0, message = "退款金额不能小于0")
    private BigDecimal refundMoney;
    
    /**
     * 店铺退款金额
     */
    private BigDecimal shopRefundMoney;
    
    /**
     * 平台退款金额
     */
    private BigDecimal mallRefundMoney;
    
    /**
     * 平台优惠券退款金额
     */
    private BigDecimal mallRefundCouponMoney;
    
    /**
     * 退款类型
     */
    private String refundType;
    
    /**
     * 退款原因
     */
    private String refundReason;
    
    /**
     * 退款状态
     */
    private String refundStatus;
    
    /**
     * 退款时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date refundTime;
}
