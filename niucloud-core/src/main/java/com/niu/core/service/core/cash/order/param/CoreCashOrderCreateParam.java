package com.niu.core.service.core.cash.order.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class CoreCashOrderCreateParam implements Serializable {

    private String orderKey;

    private Integer memberId;

    private String orderFrom;

    private String mainType;

    private Integer mainId;

    private String memberRemark;
}
