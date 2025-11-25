package com.niu.core.service.api.goods.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品分类参数
 */
@Data
public class GoodsCategorySearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String categoryName;

    private Integer isShow;

    private Integer level;

    private Integer siteId = 0;
}
