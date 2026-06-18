package com.niu.core.service.admin.shop.param;

import lombok.Data;

import java.util.List;

@Data
public class SitePaymentSearchParam {
    private String type;
    private String status;
    private String siteName;
    private Integer isSettle;
    private List<Long> createTime;
}
