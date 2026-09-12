package com.niu.core.service.admin.marketing.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 优惠券参数
 */
@Data
public class MallCouponMemberSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer memberId;

}
