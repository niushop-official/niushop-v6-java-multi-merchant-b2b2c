package com.niu.mall.service.api.order.vo;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.mall.entity.order.MallOrderDiscount;
import com.niu.mall.enums.order.OrderDeliveryTypeEnum;
import com.niu.mall.enums.order.OrderLogMainTypeEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ShopOrder视图
 */
@Data
public class MallGroupOrderInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer orderId; //
    private Integer siteId; // 站点id
    private String siteName; //站点名称
    private Integer groupId; //订单组id
    private String orderNo; // 订单编号
    private String body; // 订单内容
    private String orderType; // 订单类型
    private String orderFrom; // 订单来源
    private String outTradeNo; // 支付流水号
    private String status; // 订单状态
    private Integer memberId; // 会员id
    private String ip; // ip
    private BigDecimal goodsMoney; // 商品金额
    private BigDecimal deliveryMoney; // 配送金额
    private BigDecimal discountMoney; // 优惠金额
    private BigDecimal mallDiscountMoney; //平台优惠金额
    private BigDecimal orderMoney; // 订单金额
    private BigDecimal costGoodsMoney; // 商品成本总价
    //表中没有该字段
    //private BigDecimal payMoney; // 支付金额
    private Integer invoiceId; // 发票id，0表示不开发票
    private Long createTime; // 创建时间
    private Long payTime; // 订单支付时间
    private Long deliveryTime; // 订单发货时间
    private Long takeTime; // 订单收货时间
    private Long finishTime; // 订单完成时间
    private Long closeTime; // 订单关闭时间
    @TableLogic
    private Long deleteTime; // 是否删除(针对后台)
    private Long timeout; // 通用业务超时时间记录
    private String deliveryType; // 配送方式
    private Integer takeStoreId; // 自提点
    private String takerName; // 收货人
    private String takerMobile; // 收货人手机号
    private Integer takerProvince; // 收货省
    private Integer takerCity; // 收货市
    private Integer takerDistrict; // 收货区县
    private String takerAddress; // 收货地址
    private String takerFullAddress; // 收货详细地址
    private String takerLongitude; // 收货地址经度
    private String takerLatitude; // 收货详细纬度
    private String takerStoreId; // 收货门店
    private Integer isEnableRefund; // 是否允许退款
    private String memberRemark; // 会员留言信息
    private String shopRemark; // 商家留言
    private String closeRemark; // 关闭原因
    private String closeType; // 关闭来源(未支付自动关闭   手动关闭  退款关闭)
    private Integer refundStatus; // 退款状态  1不存在退款  2 部分退款  3 全部退款
    @JsonSerialize(using = BeanJsonSerializer.StringToJSONObjectSerializer.class)
    private String hasGoodsTypes; // 包含的商品类型 json
    private Integer isEvaluate; // 是否评论
    private String mallRemark; // 卖家留言
    private BigDecimal mallRate; // 平台分成比率
    private BigDecimal refundMoney; // 累计退款金额
    private BigDecimal refundDeliveryMoney; // 累计退还运费
    private BigDecimal shopMoney; // 店铺结算金额
    private BigDecimal mallMoney; // 平台手续费
    private BigDecimal shopRefundMoney; // 店铺退款结算金额
    private BigDecimal mallRefundMoney; // 平台退款手续费
    private BigDecimal mallCouponMoney; // 平台优惠券补贴
    private BigDecimal mallRefundCouponMoney; // 返还平台优惠券补贴
    private BigDecimal shopCommission; // 店铺支付佣金
    private BigDecimal shopRefundCommission; // 店铺退还佣金
    private String settStatus; // 商户分账结算状态
    private String couponSettStatus; // 平台手续费结算状态
    private Integer formRecordId;
    private Integer isSyncSupply; // 是否同步到供应商 1 已同步  2 未同步
    private Integer supplyId; // 供应商id
    private Integer supplyOrderId; // 供货商订单id
    private BigDecimal supplyMoney; // 供货商支出
    private BigDecimal supplyRefundMoney; // 返还供货商支出
    private BigDecimal supplyDeliveryMoney; // 供货商运费
    private String settlementStatus; // 结算状态
    private Long settlementTime; // 结算时间

    /** 购买人要求的配送/发货/自提时间（文本） */
    private String buyerAskDeliveryTime;

    @TableField(value = "extend_field_1")
    private String extendField1; //扩展字段1
    @TableField(value = "extend_field_2")
    private String extendField2; //扩展字段2
    @JsonSerialize(using = BeanJsonSerializer.StringToJSONObjectSerializer.class)
    private String extendData; //扩展数据
    private Integer settlementTimer; //预计结算时间
    private Integer perkStatus; //补贴状态
    private Long perkTime; //补贴时间
    private Long evaluateTimer; //自动好评时间，评价后重置为0
    private String weappVersion;//小程序类型，developer为开发版；trial为体验版；formal为正式版；默认为正式版
}
