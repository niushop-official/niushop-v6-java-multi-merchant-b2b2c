package com.niu.mall.service.api.marketing.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 优惠券参数
 */
@Data
public class MallCouponSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String type;

    private String order = "";

    private String sort = "desc";

}
