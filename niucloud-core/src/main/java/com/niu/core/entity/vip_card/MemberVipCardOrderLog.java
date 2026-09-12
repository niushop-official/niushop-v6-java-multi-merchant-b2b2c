package com.niu.core.entity.vip_card;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 付费会员卡订单日志表 实体类
 */
@Data
//@TableName("ns_member_vip_card_order_log")
public class MemberVipCardOrderLog {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单id
     */
    private Integer orderId;

    /**
     * 操作人类型
     */
    private String mainType;

    /**
     * 操作人id
     */
    private Integer mainId;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 类型
     */
    private String type;

    /**
     * 日志内容
     */
    private String content;

    /**
     * 创建时间（时间戳，Long类型）
     */
    private Long createTime;
}