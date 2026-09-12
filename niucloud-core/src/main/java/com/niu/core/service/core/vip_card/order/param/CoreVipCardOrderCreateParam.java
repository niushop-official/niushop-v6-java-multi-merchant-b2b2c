package com.niu.core.service.core.vip_card.order.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CoreVipCardOrderCreateParam {
    private Integer memberId;

    @NotNull(message = "card_id不能为空")
    private Integer cardId;

    @NotNull(message = "item_id不能为空")
    private Integer itemId;

    private Integer siteId;

    private String orderFrom = "h5";
    private String weappVersion = "";
    private String mainType;
    private Integer mainId;

    private String remark;
}