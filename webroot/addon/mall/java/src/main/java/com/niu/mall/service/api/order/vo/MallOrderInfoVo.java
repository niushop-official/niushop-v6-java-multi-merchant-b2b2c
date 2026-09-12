package com.niu.mall.service.api.order.vo;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryStatusEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryTypeEnum;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.mall.entity.order.MallOrderDiscount;
import com.niu.mall.enums.order.OrderDeliveryTypeEnum;
import com.niu.mall.enums.order.OrderLogMainTypeEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.enums.order.OrderTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ShopOrder视图
 */
@Data
public class MallOrderInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer orderId;  //
    private Integer groupId;
    private OrderGroup orderGroup;
    private Integer siteId;  // 站点id
    private String orderNo;  // 订单编号
    private String body;  // 订单内容
    private String orderType;  // 订单类型
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
    private Long closeTime;  // 订单关闭时间
    private Long timeout;  // 通用业务超时时间记录
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
    private String closeRemark;  // 关闭原因
    private String closeType;  // 关闭来源(未支付自动关闭   手动关闭  退款关闭)
    private Integer refundStatus;  // 退款状态  1不存在退款  2 部分退款  3 全部退款
    private String hasGoodsTypes;  // 包含的商品类型 json
    private Integer isEvaluate;  // 是否评论
    private Integer relateId;  // 关联id
    private Integer point;  // 积分兑换
    private String activityType;  // 营销类型
    private List<OrderGoods> orderGoods = new ArrayList<>();
    private MallOrderListVo.Pay pay;
    private Long nowTime = System.currentTimeMillis() / 1000;
    private Long expireTime;
    private Store store;
    private List<OrderDelivery> orderDelivery;
    private List<MallOrderDiscount> orderDiscount;
    private BigDecimal couponMoney = BigDecimal.ZERO;
    private BigDecimal manjianDiscountMoney = BigDecimal.ZERO;
    private Integer formRecordId;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long buyerAskDeliveryTime;
    private Boolean showLocalDeliveryInfo;
    private Integer localDeliveryStatus;
    private String localDeliveryStatusName;
    private List<LocalDeliveryOrderLogInfo> localDeliveryOrderLog;

    public String getLocalDeliveryStatusName() {
        return LocalDeliveryStatusEnum.getNameByStatus(this.localDeliveryStatus);
    }

    private Site site;
    private BigDecimal mallDiscountMoney = BigDecimal.ZERO;//平台优惠金额

    private Integer mchId;  // 关联id
    private Boolean isTradeManaged;  // 积分兑换
    private Boolean canInvoice;  // 积分兑换
    @JsonProperty("extend_field_1")
    private String extendField1; //扩展字段1
    @JsonProperty("extend_field_2")
    private String extendField2; //扩展字段2
    @JsonProperty("extend_data")
    private String extendData; //扩展数据（JSON格式，包含pintuan_id等）

    public JSONObject getExtendData() {
        if (StringUtils.isEmpty(this.extendData)) {
            return null;
        }
        return JSONUtil.parseObj(this.extendData);
    }

    //26-04-23 新增
    private String subDeliveryType;
    @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
    private BigDecimal usePoint = BigDecimal.ZERO; // 使用积分
    @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
    private BigDecimal pointDeductMoney;
    @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
    private BigDecimal mallCouponMoney;
    @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
    private BigDecimal fullReductionMoney;
    private String orderDeliveryContent;
    //===
    //赠品列表（与 PHP order_gift_goods / gift_order_goods 一致）
    private List<OrderGiftGoods> orderGiftGoods = new ArrayList<>();
    private List<Map<String, Object>> giftOrderGoods = new ArrayList<>();
    /**
     * 与 PHP order_status_data、与 status_name 同源（对象，非数组）
     */
    private Map<String, Object> orderStatusData = new LinkedHashMap<>();
    private List<MallOrderDiscount> discountRecords = new ArrayList<>();
    private List<MallOrderDiscount> platformDiscountRecords = new ArrayList<>();

    @Data
    public static class Site {
        private Integer siteID;
        private String siteName;
        private String frontEndLogo;
    }

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
        private Integer status; // 状态
        private String orderRefundNo; // 退款单号
        private String goodsType; // 商品类型
        private String deliveryStatus; // 配送状态
        private Integer verifyCount; // 已核销次数
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long verifyExpireTime; // 过期时间 0 为永久
        private Integer isVerify; // 是否需要核销
        private BigDecimal discountMoney;
        private String extend;
        private Integer isGift;
        private Integer formRecordId;
        private String goodsConfig; // 商品对应配置

        @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
        private String orderGoods;

        private ICoreUploadService coreUploadService() {
            return SpringContext.bean(ICoreUploadService.class);
        }

        public String getGoodsImageThumbSmall() {
            return coreUploadService().thumb(this.siteId, this.getGoodsImage(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType());
        }

        public JSONObject getExtend() {
            if (StringUtils.isEmpty(this.extend)) {
                return null;
            }
            //if (this.extend.isEmpty()) return null;
            return JSONUtil.parseObj(this.extend);
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
        private Integer groupId; // id
        private Integer status; // 状态
        private Integer isBundlePay; // 是否只能捆绑支付
        private List<MallOrderInfoVo> mallOrders; // 订单组下的其他订单
    }

    @Data
    public static class Pay {
        private String type;
        private String typeName;
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long payTime;
        private String tradeNo;
        private String outTradeNo;
    }

    @Data
    public static class Store {
        private Integer storeId; //
        private String storeName; // 门店名称
        private String storeLogo; // 门店logo
        private String storeMobile; // 手机号
        private String fullAddress; // 完整地址
        private String longitude; // 经度
        private String latitude; // 纬度
        private String tradeTime; // 营业时间
        private Double distance;

    }

    @Data
    public static class OrderDelivery {
        private Integer id; // id
        private Integer siteId; // 站点id
        private Integer orderId; //
        private String name; // 包裹名称
        private String deliveryType; // 配送方式
        private String subDeliveryType; // 详细配送方式
        private Integer expressCompanyId; // 快递公司id
        private String expressNumber; // 配送单号
        private String localDeliveryOrderId;
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long createTime; // 创建时间

        private String deliveryService;
        private String deliveryServiceName;
        private Integer localDeliverId;
        private Integer localDeliverOrderId;
        private Integer localDeliveryStatus;
        private String localDeliveryStatusName;
        private String riderMobile;
        private String riderName;

        public String getDeliveryServiceName() {
            return LocalDeliveryTypeEnum.getName(this.deliveryService);
        }

        public String getLocalDeliveryStatusName() {
            return LocalDeliveryStatusEnum.getNameByStatus(this.localDeliveryStatus);
        }
    }


    @Data
    public static class LocalDeliveryOrderLogInfo {
        /**
         * 主键ID
         */
        private Integer id;

        /**
         * 订单ID
         */
        private Integer orderId;

        /**
         * 操作人ID
         */
        private Integer mainId;

        /**
         * 操作人类型
         */
        private String mainType;

        private String mainTypeName;

        /**
         * 操作人名称
         */
        private String mainName;

        /**
         * 操作类型
         */
        private String operate;

        /**
         * 操作描述
         */
        private String operateDesc;

        /**
         * 操作后配送状态
         */
        private String status;

        /**
         * 配送时间
         */
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long createTime;

        /**
         * 备注
         */
        private String remark;

        public String getMainTypeName() {
            return OrderLogMainTypeEnum.getNameByType(this.mainType);
        }

        @JsonProperty("localDeliveryOrder")
        private LocalDeliveryOrderInfo localDeliveryOrder;
    }

    @Data
    public static class LocalDeliveryOrderInfo {
        private Integer id;

        /**
         * 站点ID
         */
        private Integer siteId;

        /**
         * 配送方标识
         */
        private String deliveryService;

        /**
         * 配送单号
         */
        private String deliveryNo;

        /**
         * 三方配送单号
         */
        private String outDeliveryNo;

        /**
         * 交易类型，mall商城 supply供货商
         */
        private String tradeType;

        /**
         * 交易单号
         */
        private String tradeNo;

        /**
         * 交易ID
         */
        private Integer tradeId;

        /**
         * 配送费用
         */
        private BigDecimal deliveryMoney;

        /**
         * 配送起点（发货点）
         */
        private String deliveryStart;

        /**
         * 配送起点省
         */
        private Integer deliveryStartProvinceId;

        /**
         * 配送起点市
         */
        private Integer deliveryStartCityId;

        /**
         * 配送起点区
         */
        private Integer deliveryStartDistrictId;

        /**
         * 配送起点经度
         */
        private String deliveryStartLng;

        /**
         * 配送起点纬度
         */
        private String deliveryStartLat;

        /**
         * 配送终点（收货点）
         */
        private String deliveryEnd;

        /**
         * 配送终点省
         */
        private Integer deliveryEndProvinceId;

        /**
         * 配送终点市
         */
        private Integer deliveryEndCityId;

        /**
         * 配送终点区
         */
        private Integer deliveryEndDistrictId;

        /**
         * 配送终点经度
         */
        private String deliveryEndLng;

        /**
         * 配送终点纬度
         */
        private String deliveryEndLat;

        /**
         * 配送距离
         */
        private BigDecimal deliveryDistance;

        /**
         * 配送状态
         */
        private String status;

        /**
         * 三方配送状态
         */
        private String outStatus;

        /**
         * 配送时间
         */
        private Long createTime;

        /**
         * 送达时间
         */
        private Long finishTime;

        /**
         * 取消时间
         */
        private Long cancelTime;

        /**
         * 骑手姓名
         */
        private String riderName;

        /**
         * 骑手电话
         */
        private String riderMobile;

        /**
         * 违约金
         */
        private BigDecimal deductMoney;

        /**
         * 备注
         */
        private String remark;

        /**
         * 配送产品信息
         */
        private String body;

        /**
         * 实际消费费用(订单取消或完成后实际定义)
         */
        private BigDecimal actualMoney;
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
        return OrderStatusEnum.getDeliveryTypeMapByStatus(Integer.valueOf(this.status), this.deliveryType, this.orderType);
    }
}
