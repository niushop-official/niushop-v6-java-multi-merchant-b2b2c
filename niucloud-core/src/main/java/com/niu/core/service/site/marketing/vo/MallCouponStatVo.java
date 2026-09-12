package com.niu.core.service.site.marketing.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 店铺优惠券统计
 */
@Data
public class MallCouponStatVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long receiveCount;
    private Long receiveUseCount;
    private Long receiveExpireCount;
}
