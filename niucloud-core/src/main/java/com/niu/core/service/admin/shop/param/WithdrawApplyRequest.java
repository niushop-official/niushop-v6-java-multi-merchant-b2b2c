package com.niu.core.service.admin.shop.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawApplyRequest {
    private static final long serialVersionUID = 1L;
    @NotNull(message = "提现金额不能为空")
    private BigDecimal applyMoney;

}
