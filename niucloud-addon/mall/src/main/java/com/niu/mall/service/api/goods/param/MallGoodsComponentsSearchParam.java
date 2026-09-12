package com.niu.mall.service.api.goods.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品参数
 */
@Data
public class MallGoodsComponentsSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer goodsCategory = 0;

    private String order;

    private Integer[] goodsIds;

    private Integer num = 0;

    private Integer siteId = 0;

    private Integer isLocalDelivery = 0;

    private Integer isLocation = 0;

    private String sort = "desc";

    private Integer labelIds = 0;

    private Integer serviceIds = 0;
}
