package com.niu.core.service.core.shop.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 商城店铺扩展表 site_shop 累计统计
 */
@Data
public class ShopStatParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 站点id */
    private Integer siteId;

    /** 店铺关注数 */
    private Integer followNumber;

    /** 商城上架商品数 */
    private Integer mallUpNum;

    /** 商城商品销量 */
    private Integer mallSaleNum;
}
