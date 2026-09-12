package com.niu.core.entity.vip_card;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 付费会员卡权益规则表 实体类
 */
@Data
//@TableName("ns_member_vip_card_benefits_item")
public class MemberVipCardBenefitsItem {

    /**
     * 权益规则id
     */
    @TableId(value = "benefits_item_id", type = IdType.AUTO)
    private Long benefitsItemId;

    /**
     * 权益id
     */
    private Long benefitsId;

    /**
     * 规则名称
     */
    private String name;

    /**
     * 权益配置（JSON）
     */
    private String benefitsConfig;
}