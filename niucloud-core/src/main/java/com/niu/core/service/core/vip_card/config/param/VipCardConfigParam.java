package com.niu.core.service.core.vip_card.config.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class VipCardConfigParam {
    @NotNull(message = "is_use不能为空")
    private Integer isUse;

    @NotNull(message = "close_length不能为空")
    private Integer closeLength;
}