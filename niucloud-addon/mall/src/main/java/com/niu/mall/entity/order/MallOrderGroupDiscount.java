package com.niu.mall.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单分组优惠实体
 */
@Data
public class MallOrderGroupDiscount implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Integer id; // id
    private String siteIds; // 站点ids
    private String orderIds; // 订单ids
    private Integer groupId; // 订单组id
    private String type; // 类型 discount 优惠，gift 赠送
    private Integer num; // 使用数量
    private BigDecimal money; // 优惠金额
    private String discountType; // 优惠类型
    private Integer discountTypeId; // 优惠类型id
    private String content; // 订单优惠说明
    private Long createTime; // 创建时间
    private Integer status; // 状态

}