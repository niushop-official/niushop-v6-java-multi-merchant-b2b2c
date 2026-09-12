package com.niu.core.service.core.vip_card.card.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VipCardItemVo {

    private Long itemId;

    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 卡id
     */
    private Integer cardId;

    /**
     * 规格名称
     */
    private String itemName;

    /**
     * 期限（数值）
     */
    private Integer timeLimit;

    /**
     * 期限类型 day天 month月 year年
     */
    private String timeType;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 划线价
     */
    private BigDecimal costPrice;

    /**
     * 续费价
     */
    private BigDecimal renewPrice;

    /**
     * 是否售卖 0否 1是
     */
    private Integer isSale;

    /**
     * 是否试用 0否 1是
     */
    private Integer isTrial;

    /**
     * 规格销量
     */
    private Integer saleNum;
    /**
     * 是否续费
     */
    private Integer isRenew;

    /**
     * 续费差值金额
     */
    private BigDecimal renewSubtractMoney;


    private BigDecimal subtractMoney;

    private String itemConfig;
}
