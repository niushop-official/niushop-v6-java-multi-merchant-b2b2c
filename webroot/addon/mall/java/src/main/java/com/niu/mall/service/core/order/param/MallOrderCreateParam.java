package com.niu.mall.service.core.order.param;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class MallOrderCreateParam implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer memberId;
    private String orderFrom;
    private String orderKey;
    private Map<Integer, Body> body;
    private MallDiscount mallDiscount;
    private OrderHelperParam.Delivery delivery = new OrderHelperParam.Delivery();
    private JSONObject formData;
    private Long buyerId;
    private String mainType;
    private Long mainId;
    private String weappVersion;
    @Data
    public static class Body {
        private String orderKey;
        private String orderType;
        private String memberRemark;
        private List<Integer> cartIds;
        private List<OrderHelperParam.SkuItem> skuData;
        private OrderHelperParam.Discount discount;
        private OrderHelperParam.Delivery delivery = new OrderHelperParam.Delivery();
        private OrderHelperParam.InvoiceCreateParam invoice;
        private JSONObject extendData;
    }

    @Data
    public static class MallDiscount {
        private Integer platformCouponId;
    }
}
