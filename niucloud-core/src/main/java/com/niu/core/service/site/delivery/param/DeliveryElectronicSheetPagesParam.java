package com.niu.core.service.site.delivery.param;

import lombok.Data;

@Data
public class DeliveryElectronicSheetPagesParam {
    private String templateName;
    private Integer expressCompanyId;
    private Integer isNotice;
    private Integer status;
    private String supportTrade;
}
