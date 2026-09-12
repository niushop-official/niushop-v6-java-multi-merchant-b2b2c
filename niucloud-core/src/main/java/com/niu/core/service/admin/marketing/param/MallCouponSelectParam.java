package com.niu.core.service.admin.marketing.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 优惠券参数
 */
@Data
public class MallCouponSelectParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private List<Integer> verifyCouponIds;

    /** 业务类型 mall/waimai/o2o */
    private String addon;

}
