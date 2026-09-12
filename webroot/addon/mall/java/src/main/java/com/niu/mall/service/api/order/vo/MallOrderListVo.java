package com.niu.mall.service.api.order.vo;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.mall.enums.order.OrderDeliveryTypeEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.enums.order.OrderTypeEnum;
import jodd.util.StringUtil;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ShopOrder列表视图
 */
@Data
public class MallOrderListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    public static JSONObject payTypeEnum = PayTypeEnum.getType();

    private Integer groupId;
    private Integer orderId;  // 
    private Integer siteId;  // 站点id
    private String orderNo;  // 订单编号
    private String body;  // 订单内容
    private String orderType;
    private String orderTypeName;// 订单类型
    private String orderFrom;  // 订单来源
    private String orderFromName;
    private String outTradeNo;  // 支付流水号
    private String status;  // 订单状态
    private Map<String, Object> statusName;
    private Integer memberId;  // 会员id
    private String ip;  // ip
    private BigDecimal goodsMoney;  // 商品金额
    private BigDecimal deliveryMoney;  // 配送金额
    private BigDecimal discountMoney;  // 优惠金额
    private BigDecimal orderMoney;  // 订单金额
    private BigDecimal payMoney;  // 支付金额
    private BigDecimal refundMoney;  // 累计退款金额
    /** 可开票金额 = order_money - refund_money，对齐 PHP */
    private BigDecimal invoiceMoney;
    private Integer invoiceId;  // 发票id，0表示不开发票
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long payTime;  // 订单支付时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long deliveryTime;  // 订单发货时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long takeTime;  // 订单收货时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long finishTime;  // 订单完成时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Integer closeTime;  // 订单关闭时间
    private Integer timeout;  // 通用业务超时时间记录
    private String deliveryType;  // 配送方式
    private String deliveryTypeName;
    private Integer takeStoreId;  // 自提点
    private String takerName;  // 收货人
    private String takerMobile;  // 收货人手机号
    private Integer takerProvince;  // 收货省
    private Integer takerCity;  // 收货市
    private Integer takerDistrict;  // 收货区县
    private String takerAddress;  // 收货地址
    private String takerFullAddress;  // 收货详细地址
    private String takerLongitude;  // 收货地址经度
    private String takerLatitude;  // 收货详细纬度
    private String takerStoreId;  // 收货门店
    private Integer isEnableRefund;  // 是否允许退款
    private String memberRemark;  // 会员留言信息
    private String shopRemark;  // 商家留言
    private String mallRemark;  // 商家留言
    private String closeRemark;  // 关闭原因
    private String closeType;  // 关闭来源(未支付自动关闭   手动关闭  退款关闭)
    private Integer refundStatus;  // 退款状态  1不存在退款  2 部分退款  3 全部退款
    private String hasGoodsTypes;  // 包含的商品类型 json
    private Integer isEvaluate;  // 是否评论
    private Integer relateId;  // 关联id
    private Integer point;  // 积分兑换
    private String activityType;  // 营销类型
    private Long nowTime = System.currentTimeMillis() / 1000;
    private Long expireTime;
    private Pay pay;
    private Boolean canInvoice;
    private Site site;
    @JsonProperty("extend_field_1")
    private String extendField1; //扩展字段1
    @JsonProperty("extend_field_2")
    private String extendField2; //扩展字段2

    /**  列表 field 对齐 */
    private Integer usePoint;
    private BigDecimal pointDeductMoney;

    /**  with order_gift_goods 一致（完整行 + 缩略图 getter） */
    private List<OrderGiftGoods> orderGiftGoods = new ArrayList<>();

    /**  回调 gift_order_goods 精简结构一致 */
    private List<Map<String, Object>> giftOrderGoods = new ArrayList<>();

    /** order_status_data、与 status_name 同源结构 */
    private Map<String, Object> orderStatusData;

    @Data
    public static class Site{
        private Integer siteID;
        private String siteName;
    }

    private List<OrderGoods> orderGoods;
    private OrderGroup orderGroup;

    @Data
    public static class OrderGoods {
        private Integer orderGoodsId; //
        private Integer siteId; // 站点id
        private Integer orderId; // 订单id
        private Integer memberId; // 购买会员id
        private Integer goodsId; // 商品id
        private Integer skuId; // 商品规格id
        private String goodsName; // 商品名称
        private String skuName; // 商品规格名称
        private String goodsImage; // 商品图片
        private String goodsImageThumbSmall;
        private String skuImage; // sku规格图片
        private BigDecimal price; // 商品单价
        private Integer num; // 购买数量
        private BigDecimal goodsMoney; // 商品总价
        private Integer isEnableRefund; // 是否允许退款
        private Integer deliveryId;

        private ICoreUploadService coreUploadService() {
            return SpringContext.bean(ICoreUploadService.class);
        }

        public String getGoodsImageThumbSmall() {
            return coreUploadService().thumb(this.siteId, this.getGoodsImage(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType());
        }
    }

    @Data
    public static class OrderGiftGoods {
        private Integer orderGiftId;
        private Integer siteId;
        private Integer orderId;
        private Integer memberId;
        private Integer goodsId;
        private Integer skuId;
        private String goodsName;
        private String skuName;
        private String goodsImage;
        private String skuImage;
        private BigDecimal price;
        private Integer num;
        private BigDecimal goodsMoney;

        private ICoreUploadService coreUploadService() {
            return SpringContext.bean(ICoreUploadService.class);
        }

        public String getGoodsImageThumbSmall() {
            return coreUploadService().thumb(this.siteId, this.getGoodsImage(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType());
        }
    }

    @Data
    public static class OrderGroup {
        private Integer groupId;
        private Integer isBundlePay;
        private Integer status;
        private List<MallGroupOrderInfoVo> mallOrder;
    }

    @Data
    public static class Pay {
        private String type;
        private String typeName;
        private String tradeNo;
        private String outTradeNo;
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long payTime;

        public String getTypeName() {
            return MallOrderListVo.payTypeEnum.getByPath(this.type + ".name", String.class);
        }
    }

    public String getOrderTypeName() {
        return OrderTypeEnum.getOrderTypeName(this.orderType);
    }

    public String getOrderFromName() {
        return ChannelEnum.getNameByCode(this.orderFrom);
    }

    public String getDeliveryTypeName() {
        return OrderDeliveryTypeEnum.getNameByType(this.deliveryType);
    }

    public Map<String, Object> getStatusName() {
        return OrderStatusEnum.getDeliveryTypeMapByStatus(Integer.valueOf(this.status),this.deliveryType,this.orderType);
    }
}
