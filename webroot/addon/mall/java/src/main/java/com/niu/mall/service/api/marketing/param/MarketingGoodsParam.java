package com.niu.mall.service.api.marketing.param;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class MarketingGoodsParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String type;
    
    private Integer id;
}
