package com.niu.core.service.admin.goods.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品角标状态参数
 */
@Data
public class EditGoodsBadgeStatusParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "badge_id参数缺失")
    private Integer badgeId;

    @NotNull(message = "status参数缺失")
    private Integer status;
}
