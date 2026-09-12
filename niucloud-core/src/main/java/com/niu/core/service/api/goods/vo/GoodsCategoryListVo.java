package com.niu.core.service.api.goods.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品分类列表视图（对齐 PHP getList 返回字段）
 */
@Data
public class GoodsCategoryListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer categoryId;
    private String categoryName;
    private String image;
    private Integer level;
    private Integer pid;
    private String categoryFullName;
}
