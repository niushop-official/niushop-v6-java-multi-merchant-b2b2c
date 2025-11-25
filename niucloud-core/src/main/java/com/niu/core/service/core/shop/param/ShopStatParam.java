package com.niu.core.service.core.shop.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 统计数据添加
*/
@Data
public class ShopStatParam implements Serializable {

    private static final long serialVersionUID = 1L;


    /** 站点id */
    private Integer siteId;

    /** 订单数 */
    private Integer supplySaleNum;

    /** 供货商上架商品数 */
    private Integer supplyUpNum;

    private Integer supplyFollowNum;

    private Integer supplyGoodsNum;

    private Integer mallUpNum;

    private Integer mallSaleNum;


}
