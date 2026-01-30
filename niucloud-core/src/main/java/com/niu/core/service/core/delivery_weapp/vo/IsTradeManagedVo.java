package com.niu.core.service.core.delivery_weapp.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class IsTradeManagedVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean isTradeManaged = false;
    private Integer mchId;
}
