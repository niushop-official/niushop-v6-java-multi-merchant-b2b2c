package com.niu.core.entity.vip_card;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 付费会员卡会员关联表 实体类
 */
@Data
//@TableName("ns_member_vip_card_buy")
public class MemberVipCardBuy {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 订单id
     */
    private Integer orderId;

    /**
     * 购买会员id
     */
    private Integer memberId;

    /**
     * 卡id
     */
    private Integer cardId;

    /**
     * 规格id
     */
    private Integer itemId;

    /**
     * 是否过期 0否 1是
     */
    private Integer isExpire;

    /**
     * 是否试用 0否 1是
     */
    private Integer isTrial;

    /**
     * 试用天数
     */
    private Integer trialDay;

    /**
     * 到期时间（时间戳，Long类型）
     */
    private Long expireTime;

    /**
     * 创建时间（时间戳，Long类型）
     */
    private Long createTime;

    /**
     * 卡权益配置（JSON）
     */
    private String benefitsConfig;

    /**
     * 卡规格配置
     */
    private String itemConfig;
}