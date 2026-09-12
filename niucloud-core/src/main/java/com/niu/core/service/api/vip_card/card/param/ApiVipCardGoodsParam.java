package com.niu.core.service.api.vip_card.card.param;

import lombok.Data;

@Data
public class ApiVipCardGoodsParam {
    private String group;   // 插件标识，如 'mall', 'o2o'
    private Integer num;    // 数量限制
}