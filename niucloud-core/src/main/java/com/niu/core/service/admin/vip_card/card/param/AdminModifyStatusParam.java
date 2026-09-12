package com.niu.core.service.admin.vip_card.card.param;

import lombok.Data;

@Data
public class AdminModifyStatusParam {
    private Integer cardId;
    private Integer benefitsId;
    private Integer status;
}