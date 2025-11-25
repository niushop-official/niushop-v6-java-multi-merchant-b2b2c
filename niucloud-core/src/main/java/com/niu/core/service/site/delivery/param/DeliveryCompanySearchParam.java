package com.niu.core.service.site.delivery.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 【请填写功能名称】参数
 */
@Data
public class DeliveryCompanySearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String companyName;

    private String electronicSheetSwitch = "";

}
