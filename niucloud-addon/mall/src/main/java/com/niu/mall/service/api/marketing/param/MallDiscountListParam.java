package com.niu.mall.service.api.marketing.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 限时折扣参数
 */
@Data
public class MallDiscountListParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer limit;
    private String activeName;  // 活动名称


}
