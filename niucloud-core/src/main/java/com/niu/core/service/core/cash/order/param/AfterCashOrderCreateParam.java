package com.niu.core.service.core.cash.order.param;

import com.niu.core.entity.cash.CashOrder;
import com.niu.core.service.core.cash.order.vo.CashOrderBasicVo;
import lombok.Data;

import java.io.Serializable;

@Data
public class AfterCashOrderCreateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private Integer orderId;

    private CashOrder orderData;

    private CashOrderBasicVo basic;

    private String mainType;

    private Integer mainId;

    private Long time;
}
