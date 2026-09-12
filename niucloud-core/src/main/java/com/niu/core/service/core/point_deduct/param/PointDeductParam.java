package com.niu.core.service.core.point_deduct.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PointDeductParam {
    @NotNull(message = "is_open不能为空")
    private Integer isOpen;          // 0关闭 1开启

    @NotNull(message = "deduct_amount不能为空")
    private Integer deductAmount;    // 每X积分可抵扣N元中的N（金额）
}