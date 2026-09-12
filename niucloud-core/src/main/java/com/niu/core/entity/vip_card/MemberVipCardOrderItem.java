package com.niu.core.entity.vip_card;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 付费会员卡订单项表 实体类
 */
@Data
//@TableName("ns_member_vip_card_order_item")
public class MemberVipCardOrderItem {

    /**
     * 订单项ID
     */
    @TableId(value = "order_item_id", type = IdType.AUTO)
    private Long orderItemId;

    /**
     * 订单id
     */
    private Integer orderId;

    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 购买会员id
     */
    private Integer memberId;

    /**
     * 会员卡id
     */
    private Integer cardId;

    /**
     * 会员卡规格id
     */
    private Integer itemId;

    /**
     * 会员卡名称
     */
    private String cardName;

    /**
     * 会员卡规格名称
     */
    private String itemName;

    /**
     * 规格单价
     */
    private BigDecimal price;

    /**
     * 购买数量
     */
    private Integer num;

    /**
     * 规格总价
     */
    private BigDecimal itemMoney;

    /**
     * 是否退款
     */
    private Integer isRefund;

    /**
     * 退款编号
     */
    private String refundNo;

    /**
     * 退款状态
     */
    private Integer refundStatus;

    /**
     * 卡权益配置（JSON）
     */
    private String benefitsConfig;

    /**
     * 卡规格配置
     */
    private String itemConfig;

    /**
     * 是否续费
     */
    private Integer isRenew;

    /**
     * 是否试用
     */
    private Integer isTrial;
}