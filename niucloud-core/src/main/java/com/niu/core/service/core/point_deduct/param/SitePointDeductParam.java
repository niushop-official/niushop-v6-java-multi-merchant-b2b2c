package com.niu.core.service.core.point_deduct.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SitePointDeductParam {
    @NotNull(message = "deduct_rate不能为空")
    private Integer deductRate;  // 每X积分可抵扣N元中的X（积分数量）
}
