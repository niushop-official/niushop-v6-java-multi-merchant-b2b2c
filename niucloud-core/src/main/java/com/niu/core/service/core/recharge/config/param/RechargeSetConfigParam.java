package com.niu.core.service.core.recharge.config.param;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RechargeSetConfigParam {
    //  是否开启 0：关闭 1：开启
    private Integer isUse;
    // 充值说明
    private String rechargeExplain;
    // 最小充值金额
    private BigDecimal minPrice;
    // 未支付取消订单时间 分钟
    private Integer closeLength;
}
