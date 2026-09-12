package com.niu.core.event.fenxiao.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分销商品列表查询参数
 */
@Data
public class FenxiaoGoodsListParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 站点ID
     */
    private Integer siteId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 指定商品ID列表
     */
    private List<Integer> goodsIds;

    /**
     * 排除的商品ID列表
     */
    private List<Integer> excludeGoodsIds;

    /**
     * 商品ID（详情查询用）
     */
    private Integer goodsId;

    /**
     * 分页页码
     */
    private Integer page;

    /**
     * 每页条数
     */
    private Integer limit;
}
