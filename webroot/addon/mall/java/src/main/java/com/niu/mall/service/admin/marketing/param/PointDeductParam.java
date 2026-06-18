package com.niu.mall.service.admin.marketing.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 */
@Data
public class PointDeductParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "积分抵扣金额")
    private BigDecimal deductAmount;

    @Schema(description = "折扣功能是否开启")
    private Integer isOpen;

//    @Schema(description = "折扣比例")
//    private BigDecimal deductRate;
//
//    @Schema(description = "站点ID")
//    private Integer siteId;

    @Schema(description = "描述")
    private String desc;
}
