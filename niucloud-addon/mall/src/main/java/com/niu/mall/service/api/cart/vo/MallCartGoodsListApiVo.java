package com.niu.mall.service.api.cart.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * ShopCart列表视图
 */
@Data
public class MallCartGoodsListApiVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String siteName;

    private String logo;

    private List<MallCartGoodsListVo> goodsList;
}
