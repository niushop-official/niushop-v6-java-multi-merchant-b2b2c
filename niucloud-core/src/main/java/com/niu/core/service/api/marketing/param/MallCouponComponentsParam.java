package com.niu.core.service.api.marketing.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 优惠券组件查询参数
 */
@Data
public class MallCouponComponentsParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String siteId;

    private String couponIds = "";

    private Integer num = 0;

    private String addon;
}
