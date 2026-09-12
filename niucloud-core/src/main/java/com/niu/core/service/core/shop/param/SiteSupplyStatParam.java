package com.niu.core.service.core.shop.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 供货商扩展表 site_supply 累计统计
 */
@Data
public class SiteSupplyStatParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    /** 供货商商品销量 */
    private Integer supplySaleNum;

    /** 供货商上架商品数 */
    private Integer supplyUpNum;

    /** 合作商城店铺数 */
    private Integer supplyFollowNum;

    /** 供货商商品总数 */
    private Integer supplyGoodsNum;
}
