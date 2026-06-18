package com.niu.core.service.core.point_deduct.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 积分抵扣计算结果
 */
@Data
public class DeductMoneyVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "使用积分")
    private Integer usePoint;

    @Schema(description = "积分抵扣金额")
    private BigDecimal deductMoney;

}
