package com.niu.core.service.admin.goods.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * ShopGoodsCategory列表视图
 */
@Data
public class GoodsCategoryListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer categoryId;  // 商品分类id
    private Integer siteId;  // 站点id
    private String categoryName;  // 分类名称
    private String image;  // 分类图片
    private Integer level;  // 层级
    private Integer pid;  // 上级分类id
    private String categoryFullName;  // 组装分类名称
    private Integer isShow;  // 是否显示（1：显示，0：不显示）
    private Integer sort;  // 排序号
}
