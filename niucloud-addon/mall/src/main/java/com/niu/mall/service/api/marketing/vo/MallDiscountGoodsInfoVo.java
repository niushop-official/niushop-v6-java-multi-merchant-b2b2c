package com.niu.mall.service.api.marketing.vo;

import com.niu.mall.entity.active.MallActive;
import lombok.Data;

import java.io.Serializable;

/**
 * ShopActive视图
 */
@Data
public class MallDiscountGoodsInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer activeGoodsId;  // 活动商品id
    private Integer activeId;  // 活动id
    private Integer siteId;  // 站点id
    private Integer goodsId;  // 商品id
    private String activeGoodsType;  // 商品活动类型（单品，独立商品，店铺整体商品）
    private String activeClass;  // 商品活动类别
    private String activeGoodsLabel;  // 活动商品标签（针对活动有标签）
    private String activeGoodsCategory;  // 活动商品分类（针对活动有分类）
    private String activeGoodsValue;  // 活动商品信息数据
    private String activeGoodsStatus;  // 活动状态
    private MallActive active;
    private Long startTime; // 活动开始时间
    private Long endTime; // 活动结束时间

    public Long getStartTime() {
        return this.active.getStartTime();
    }

    public Long getEndTime() {
        return this.active.getEndTime();
    }
}
