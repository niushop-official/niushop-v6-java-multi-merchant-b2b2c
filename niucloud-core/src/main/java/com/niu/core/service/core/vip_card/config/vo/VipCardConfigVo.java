package com.niu.core.service.core.vip_card.config.vo;

import lombok.Data;

@Data
public class VipCardConfigVo {
    /**
     * 是否开启（0关闭 1开启）
     */
    private Integer isUse;

    /**
     * 未支付取消订单时间（分钟）
     */
    private Integer closeLength;
}