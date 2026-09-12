package com.niu.core.service.core.cash.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CashOrderCreateCacheVo implements Serializable {

    private String orderKey;

    private Integer memberId;

    private Integer siteId;

    private CashOrderBasicVo basic = new CashOrderBasicVo();

    private Map<String, Object> config = new HashMap<>();

    private Map<String, Object> discount = new HashMap<>();

    private List<String> error = new ArrayList<>();

    private String tradeType = "";

    private Integer isCanUsePoint = 1;
}
