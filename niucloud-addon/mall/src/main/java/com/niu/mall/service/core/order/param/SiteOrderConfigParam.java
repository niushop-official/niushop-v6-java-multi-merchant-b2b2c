package com.niu.mall.service.core.order.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单配置参数
 */
@Data
public class SiteOrderConfigParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String formId;


}
