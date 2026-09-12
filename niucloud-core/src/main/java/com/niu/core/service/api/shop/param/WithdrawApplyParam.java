package com.niu.core.service.api.shop.param;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 提现申请参数
 */
@Data
public class WithdrawApplyParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 结算账户ID
     */
    @NotNull(message = "请选择提现账号")
    private Integer accountId;

    /**
     * 申请提现金额
     */
    @NotNull(message = "提现金额不能为空")
    @Positive(message = "提现金额必须大于0")
    private BigDecimal applyMoney;
} 