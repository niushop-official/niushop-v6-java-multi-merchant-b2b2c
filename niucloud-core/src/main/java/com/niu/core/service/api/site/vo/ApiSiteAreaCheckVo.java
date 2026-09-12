package com.niu.core.service.api.site.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 配送范围检查结果
 * 对齐 PHP CoreLocalDeliveryService::checkLocationInAreaBySiteId
 */
@Data
public class ApiSiteAreaCheckVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 是否在配送范围内 */
    private Boolean inArea = false;

    /** 起送价（在范围内时返回） */
    private BigDecimal startPrice;
}
