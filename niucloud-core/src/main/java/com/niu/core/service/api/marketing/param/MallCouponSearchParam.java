package com.niu.core.service.api.marketing.param;

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

    /** 类型：平台 plat，店铺 site */
    private String appType;

    /** 所属应用 */
    private String addon;

    private String order = "";

    private String sort = "desc";

}
