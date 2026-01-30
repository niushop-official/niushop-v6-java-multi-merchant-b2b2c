package com.niu.core.service.core.recharge.order.param;

import com.niu.core.entity.recharge.RechargeOrder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AfterOrderCreateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private Integer orderId;

    private RechargeOrder orderData;

    private Integer mainId;

    private String mainType;

    private Long time;
}
