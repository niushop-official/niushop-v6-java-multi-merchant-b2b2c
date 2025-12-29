package com.niu.mall.service.admin.points_mall.param;

import lombok.Data;

/**
 * 积分商城商品选择参数
 */
@Data
public class PointsMallGoodsSelectParam {

    /**
     * 站点ID
     */
    private Integer siteId;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 商品分类
     */
    private String goodsMallCategory;

    /**
     * 商品类型
     */
    private String goodsType;

}
