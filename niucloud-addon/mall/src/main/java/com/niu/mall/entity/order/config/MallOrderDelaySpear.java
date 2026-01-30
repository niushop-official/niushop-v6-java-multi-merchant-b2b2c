package com.niu.mall.entity.order.config;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单延迟分账配置
 */
@Data
public class MallOrderDelaySpear implements Serializable {

    private static final long serialVersionUID = 1L;

    //是否延迟分账
    private Integer isDelaySpear;

    //延迟分账天数
    private Integer delayDays;

}

