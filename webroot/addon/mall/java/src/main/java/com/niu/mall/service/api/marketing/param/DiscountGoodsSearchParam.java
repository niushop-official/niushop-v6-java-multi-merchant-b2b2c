package com.niu.mall.service.api.marketing.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 限时折扣参数
 */
@Data
public class DiscountGoodsSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "缺少必须参数active_id")
    private String activeId;  // 活动名称

}
