package com.niu.core.service.admin.stat.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
* 详情
*/
@Data
public class SysStatRechargeVo implements Serializable {

private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String time;

    /** 充值金额 */
    @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
    private BigDecimal rechargeMoney;


    /** 订单充值金额 */
    @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
    private BigDecimal rechargeOrderMoney;


    /** 订单支付金额 */
    @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
    private BigDecimal rechargePayMoney;


    /** 订单赠送金额 */
    @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
    private BigDecimal rechargeGiveMoney;

}