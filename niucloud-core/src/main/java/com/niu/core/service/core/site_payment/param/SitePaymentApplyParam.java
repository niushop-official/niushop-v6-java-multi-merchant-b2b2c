package com.niu.core.service.core.site_payment.param;

import lombok.Data;

import java.util.List;

@Data
public class SitePaymentApplyParam {

    private String siteName;
    private String type;
    private List<Long> createTime;
    private Integer siteId;
    private Integer id;
}
