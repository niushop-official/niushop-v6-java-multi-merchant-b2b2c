package com.niu.mall.service.core.order.param;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class OrderHelperParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @Data
    public static class Body {
        private String orderKey = "";
        private List<SkuItem> skuData;
        private List<Integer> cartIds;
        private Discount discount;
    }
    @Data
    public static class SkuItem {
        private Long id;
        private Integer skuId;
        private Integer num;
    }

    @Data
    public static class Delivery {
        protected String deliveryType = "";

        protected Object takeAddress;

        private Integer takeAddressId = 0;

        private Integer takeStoreId = 0;

        private String localDeliveryType = "";

        private String takerMobile = "";
    }

    @Data
    public static class Discount {
        private Integer couponId;
    }

    @Data
    public static class MallDiscount {
        private String platformCouponId = "";
    }

    @Data
    public static class InvoiceCreateParam {
        private Integer headerType; // 抬头类型
        private String headerName; // 名称（发票抬头）
        private Integer type; // 发票类型
        private String name; // 发票内容
        private String taxNumber; // 公司税号
        private String mobile; // 开票人手机号
        private String email; // 开票人邮箱
        private String telephone; // 注册电话
        private String address; // 注册地址
        private String bankName; // 开户银行
        private String bankCardNumber; // 银行账号
    }
}
