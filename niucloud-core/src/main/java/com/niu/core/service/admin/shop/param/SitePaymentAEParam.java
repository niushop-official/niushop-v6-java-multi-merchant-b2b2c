package com.niu.core.service.admin.shop.param;

import lombok.Data;

@Data
public class SitePaymentAEParam {
    private Integer id;
    private Integer siteId;
    private String type;
    private String typeName;
    private Boolean isCover = false;
    private String extendNo;
}
