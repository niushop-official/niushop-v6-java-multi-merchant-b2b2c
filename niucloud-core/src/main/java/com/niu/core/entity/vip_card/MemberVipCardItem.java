package com.niu.core.entity.vip_card;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 付费会员卡规格表 实体类
 */
@Data
//@TableName("ns_member_vip_card_item")
public class MemberVipCardItem {

    /**
     * 规格id
     */
    @TableId(value = "item_id", type = IdType.AUTO)
    private Long itemId;

    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 卡id
     */
    private Integer cardId;

    /**
     * 规格名称
     */
    private String itemName;

    /**
     * 期限（数值）
     */
    private Integer timeLimit;

    /**
     * 期限类型 day天 month月 year年
     */
    private String timeType;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 划线价
     */
    private BigDecimal costPrice;

    /**
     * 续费价
     */
    private BigDecimal renewPrice;

    /**
     * 是否售卖 0否 1是
     */
    private Integer isSale;

    /**
     * 是否试用 0否 1是
     */
    private Integer isTrial;

    /**
     * 规格销量
     */
    private Integer saleNum;
}