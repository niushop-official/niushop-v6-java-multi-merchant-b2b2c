package com.niu.core.service.api.marketing.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 会员优惠券查询参数
 */
@Data
public class MallCouponMemberSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer status;

    private String type = "all";

    /** 业务类型（waimai/mall/o2o 等） */
    private String tradeType;
}
