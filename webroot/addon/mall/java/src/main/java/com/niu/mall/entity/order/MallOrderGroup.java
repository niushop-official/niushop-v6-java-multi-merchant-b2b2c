package com.niu.mall.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单分组实体
 */
@Data
public class MallOrderGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="group_id", type= IdType.AUTO)
    private Integer groupId; // id
    private Integer memberId; // 会员id
    private String body; // 订单内容
    private BigDecimal goodsMoney; // 商品金额
    private BigDecimal deliveryMoney; // 配送金额
    private BigDecimal shopDiscountMoney; // 店铺优惠金额
    private BigDecimal mallDiscountMoney; // 平台优惠金额
    private BigDecimal orderMoney; // 订单金额
    private BigDecimal shopMoney; // 店铺结算金额
    private BigDecimal mallMoney; // 平台结算金额
    private Integer status; // 状态
    private Integer isBundlePay; // 是否只能捆绑支付
    private String outTradeNo; // 外部交易号
    private Long createTime; // 创建时间
    private Long payTime; // 支付时间
    private Integer siteId; // 站点id(跨店支付为0)
    private String takerFullAddress; // 收货地址

}