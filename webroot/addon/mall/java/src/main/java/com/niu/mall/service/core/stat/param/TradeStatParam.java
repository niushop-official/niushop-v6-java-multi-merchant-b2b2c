package com.niu.mall.service.core.stat.param;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class TradeStatParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String startTime;
    private String endTime;
}
