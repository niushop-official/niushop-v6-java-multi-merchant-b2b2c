package com.niu.core.service.core.recharge.config.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RechargeConfigVo {
    //  是否开启 0：关闭 1：开启
    private Integer isUse = 1;
    // 充值说明
    private String rechargeExplain = "账户充值后，资金将存入消费余额，仅限在商城购物时使用，不支持提现。如充值过程中遇到任何问题，请联系商城客服协助处理。";
    // 最小充值金额
    private BigDecimal minPrice = new BigDecimal("0.01");
    // 未支付取消订单时间 分钟
    private Integer closeLength = 10;
}
