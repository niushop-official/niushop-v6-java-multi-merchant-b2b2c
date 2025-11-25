package com.niu.core.service.api.order.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.basic.InvoiceHeaderTypeEnum;
import com.niu.core.enums.basic.InvoiceTypeEnum;
import com.niu.core.enums.basic.InvoicingEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.core.sys.ICoreUploadService;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ShopInvoice列表视图
 */
@Data
public class InvoiceListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 发票id
    private Integer siteId;  // 站点id
    private Integer memberId;  // 会员id
    private String tradeType;  // 开票分类 order:订单
    private Integer tradeId;  // 业务id
    private Integer headerType;  // 抬头类型
    private String headerName;  // 名称（发票抬头）
    private Integer type;  // 发票类型
    private String name;  // 发票内容
    private String taxNumber;  // 公司税号
    private String mobile;  // 开票人手机号
    private String email;  // 开票人邮箱
    private String telephone;  // 注册电话
    private String address;  // 注册地址
    private String bankName;  // 开户银行
    private String bankCardNumber;  // 银行账号
    private BigDecimal money;  // 开票金额
    private Integer isInvoice;  // 是否开票
    private String isInvoiceName;  // 是否开票
    private String invoiceNumber;  // 发票代码
    private String invoiceVoucher;  // 发票凭证
    private String remark;  // 备注
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 申请时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long invoiceTime;  // 开票时间
    private Integer status;  // 是否生效

    private String siteName;  // 站点名称
    private String frontEndLogo;  // 站点前端logo

    private SiteInfo site;
    @JsonProperty("orderData")
    private OrderInfo orderData;

    @Data
    public  static class SiteInfo{
        private Integer siteId;  // 站点id
        private String siteName;  // 站点名称
        private String frontEndLogo;  // 站点前端logo
    }
    /**
     * 获取头名称
     * @return
     */
    public String getHeaderTypeName(){
        return InvoiceHeaderTypeEnum.getNameByType(this.headerType);
    }

    /**
     * 获取类型名称
     * @return
     */
    public String getTypeName(){
        return InvoiceTypeEnum.getNameByType(this.type);
    }

    /**
     * 获取类型名称
     * @return
     */
    public String getIsInvoiceName(){
        return InvoicingEnum.getNameByStatus(this.isInvoice);
    }

    @Data
    public  static class OrderInfo{
        private Integer orderId;  //
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
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal mallCouponMoney; // 平台优惠券补贴
        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal mallRefundCouponMoney; // 返还平台优惠券补贴
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
        @JsonProperty("orderGoods")
        private List<OrderGoods> orderGoods;
        private Pay pay;
        private Long nowTime = System.currentTimeMillis() / 1000;
        private Long expireTime;
        private Integer formRecordId;
        private Boolean formRecordShow;

        @Data
        public static class OrderGoods {
            private Integer siteId; // 订单id
            private Integer orderId; // 订单id
            private String goodsName; // 商品名称
            private String skuName; // 商品规格名称
            private String skuImage; // sku规格图片
            private BigDecimal price; // 商品单价
            private Integer num; // 购买数量
            private String skuImageThumbMid; // 购买数量
            private ICoreUploadService coreUploadService() {
                return SpringContext.bean(ICoreUploadService.class);
            }

            public String getSkuImageThumbMid() {
                return coreUploadService().thumb(this.siteId, this.getSkuImage(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
            }
        }

        @Data
        public static class Pay {
            private String outTradeNo;
            private String type;
            private String typeName;
            @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
            private Long payTime;

            public String getTypeName() {
                return PayTypeEnum.getType().getByPath(this.type + ".name", String.class);
            }
        }

        private String type;
        private String typeName;
    }

}
