package com.niu.core.service.admin.goods.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品角标排序参数
 */
@Data
public class EditGoodsBadgeSortParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "badge_id参数缺失")
    private Integer badgeId;

    @NotNull(message = "sort参数缺失")
    private Integer sort;
}
