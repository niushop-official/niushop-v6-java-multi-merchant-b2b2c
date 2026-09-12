package com.niu.core.entity.vip_card;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 付费会员卡权益发放记录表 实体类
 */
@Data
//@TableName("ns_member_vip_card_benefits_send_log")
public class MemberVipCardBenefitsSendLog {

    /**
     * 发放id
     */
    @TableId(value = "send_id", type = IdType.AUTO)
    private Long sendId;

    /**
     * 会员卡id
     */
    private Integer cardId;

    /**
     * 权益id
     */
    private Integer benefitsId;

    /**
     * 权益类型
     */
    private String benefitsType;

    /**
     * 会员id
     */
    private Integer memberId;

    /**
     * 发放时间（时间戳，Long类型）
     */
    private Long createTime;
}