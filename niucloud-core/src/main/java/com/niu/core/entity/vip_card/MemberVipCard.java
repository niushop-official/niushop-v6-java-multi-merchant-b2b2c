package com.niu.core.entity.vip_card;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 付费会员卡表 实体类
 */
@Data
//@TableName("ns_member_vip_card")
public class MemberVipCard {

    /**
     * 卡id
     */
    @TableId(value = "card_id", type = IdType.AUTO)
    private Long cardId;

    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 卡名称
     */
    private String cardName;

    /**
     * 简介
     */
    @TableField("`desc`")
    private String desc;

    /**
     * 状态 1商家 2下架
     */
    private Integer status;

    /**
     * 是否启用试用 0否 1是
     */
    private Integer isTrialOpen;

    /**
     * 试用天数
     */
    private Integer trialDay;

    /**
     * 试用价
     */
    private BigDecimal trialPrice;

    /**
     * 权益配置（JSON）
     */
    private String benefitsConfig;

    /**
     * 权益id集合（逗号分隔）
     */
    private String benefitsId;

    /**
     * 权益规则id集合（逗号分隔）
     */
    private String benefitsItemId;

    /**
     * 卡面设置（JSON）
     */
    private String cardConfig;

    /**
     * 分享配置（JSON）
     */
    private String shareConfig;

    /**
     * 添加时间（时间戳，Long类型）
     */
    private Long createTime;

    /**
     * 更新时间（时间戳，Long类型）
     */
    private Long updateTime;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 购卡会员数
     */
    private Integer memberNum;

    /**
     * 会员卡销量
     */
    private Integer saleNum;
}