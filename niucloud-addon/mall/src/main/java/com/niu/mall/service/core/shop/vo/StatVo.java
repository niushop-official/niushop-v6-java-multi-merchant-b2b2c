package com.niu.mall.service.core.shop.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class StatVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer orderNum = 0; // 订单总数

    private BigDecimal saleMoney = new BigDecimal(0); // 销售总额

    private BigDecimal refundMoney = new BigDecimal(0); // 退款总额

    private Integer accessSum = 0; // 访问数

    private Integer addonNum = 0;//应用总数

    private Integer goodsNum = 0;//商品总数

    private Integer accessNum = 0;//商品浏览量
    private Integer cartNum = 0;//加购数量
    private Integer payNum = 0;//支付件数
    private Integer refundNum = 0;//退款件数
    private Integer saleNum = 0;//下单数量
}
