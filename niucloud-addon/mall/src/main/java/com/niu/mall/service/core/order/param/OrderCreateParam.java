package com.niu.mall.service.core.order.param;

import lombok.Data;

@Data
public class OrderCreateParam {
    private Integer siteId;
    private String orderKey;
    private String memberRemark;
    private String invoice;
    private Long buyerId;
    private String orderFrom;
    private String mainType;
    private Long mainId;
}
