package com.niu.core.service.api.goods.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品分类参数
 */
@Data
public class GoodsCategorySearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 上级分类 id */
    private Integer pid;

    /** 层级 */
    private Integer level;

    /** 站点 id，0 或不传则不过滤站点 */
    private Integer siteId = 0;

    /** 所属应用，如 waimai / mall / o2o */
    private String addon;

    /** 分类名称（可选模糊搜索） */
    private String categoryName;
}
