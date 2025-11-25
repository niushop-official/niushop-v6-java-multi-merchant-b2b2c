package com.niu.core.service.core.shop_delivery.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 店铺同城配送创建配送单返回参数
 */

@Data
public class ShopDeliveryOrderCreateVo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 商家配送的配送单号就是同城配送的三方配送单号
     */
    private String outDeliveryNo;
    /**
     * 配送费用
     */
    private BigDecimal deliveryMoney;
    /**
     * 配送距离
     */
    private BigDecimal deliveryDistance;
    /**
     * 配送员姓名
     */
    private String riderName;
    /**
     * 配送员手机号
     */
    private String riderMobile;


}