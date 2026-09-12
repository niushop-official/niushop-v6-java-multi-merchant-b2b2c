package com.niu.core.entity.coupon;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 优惠券会员领取记录实体
 */
@Data
public class MallCouponMember implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Integer id; // 优惠券发放记录id
    private Integer siteId; // 站点id
    private Integer couponId; // 优惠券id
    private Integer memberId; // 会员id
    private Long createTime; // 领取时间
    private Long expireTime; // 过期时间
    private Long useTime; // 使用时间
    private String type; // 优惠券类型
    private Integer status; // 状态
    private String title; // 优惠券名称
    private BigDecimal price; // 面值
    private BigDecimal minConditionMoney; // 最低使用门槛
    private String receiveType; // 领取方式
    private Integer tradeId; // 关联业务id
    /** 业务类型（来自优惠券 addon） */
    private String tradeType;
    private String memo; // 备注

}