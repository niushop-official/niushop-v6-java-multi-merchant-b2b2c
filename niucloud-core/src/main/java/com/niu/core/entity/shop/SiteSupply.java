package com.niu.core.entity.shop;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 供货商扩展表
 * 对应表名：ns_site_supply
 */
@Data
//@TableName("ns_site_supply")
public class SiteSupply {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 服务费比率
     */
    private BigDecimal serviceRate;

    /**
     * 供货商商品销量
     */
    private Integer supplySaleNum;

    /**
     * 供货商上架商品数
     */
    private Integer supplyUpNum;

    /**
     * 关注站点数
     */
    private Integer supplyFollowNum;

    /**
     * 供货商商品总数
     */
    private Integer supplyGoodsNum;

    /**
     * 营业状态（0已打烊，1营业中）
     */
    private Integer businessStatus;

    /**
     * 行业类目ID
     */
    private Integer categoryId;

    /**
     * 子行业类目ID
     */
    private Integer childCategoryId;

    /**
     * 店铺关键词
     */
    private String keywords;
}