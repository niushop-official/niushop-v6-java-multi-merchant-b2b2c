package com.niu.core.service.api.marketing.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 即将过期优惠券
 */
@Data
public class MallCouponExpiringVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer couponId;
    private String type;
    private BigDecimal minConditionMoney;
    private String title;
    private BigDecimal price;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long expireTime;
}
