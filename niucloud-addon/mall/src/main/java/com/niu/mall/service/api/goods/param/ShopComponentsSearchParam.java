package com.niu.mall.service.api.goods.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品参数
 */
@Data
public class ShopComponentsSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer[] goodsIds;

    private Integer num = 0;

    private Integer siteId = 0;
}
