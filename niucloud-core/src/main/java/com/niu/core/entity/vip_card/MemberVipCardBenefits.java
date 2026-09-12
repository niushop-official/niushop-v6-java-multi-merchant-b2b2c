package com.niu.core.entity.vip_card;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 付费会员卡权益表 实体类
 */
@Data
//@TableName("ns_member_vip_card_benefits")
public class MemberVipCardBenefits {

    /**
     * 权益id
     */
    @TableId(value = "benefits_id", type = IdType.AUTO)
    private Long benefitsId;

    /**
     * 规格名称
     */
    private String name;

    /**
     * 权益展示图标
     */
    private String icon;

    /**
     * 权益类型
     */
    private String type;

    /**
     * 权益简介
     */
    @TableField("`desc`")
    private String desc;

    /**
     * 权益说明
     */
    @TableField("`explain`")
    private String explain;

    /**
     * 是否启用 0否 1是
     */
    private Integer status;

    /**
     * 归属插件
     */
    private String addon;

    /**
     * 创建时间（时间戳，Long类型）
     */
    private Long createTime;

    /**
     * 更新时间（时间戳，Long类型）
     */
    private Long updateTime;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 是否系统默认
     */
    private Integer isSystem;
}
