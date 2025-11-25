package com.niu.mall.service.core.order.param;

import cn.hutool.json.JSONObject;
import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
public class MallOrderCalculateParam implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer memberId;
    private String orderFrom;
    private Map<Integer, Body> body;
    private MallDiscount mallDiscount = new MallDiscount();
    private OrderHelperParam.Delivery delivery = new OrderHelperParam.Delivery();
    private JSONObject extendData;

    @Data
    public static class Body {
        private String orderKey;
        private List<Integer> cartIds = new ArrayList<>();
        private List<OrderHelperParam.SkuItem> skuData = new ArrayList<>();
        private OrderHelperParam.Discount discount = new OrderHelperParam.Discount();
        private OrderHelperParam.Delivery delivery = new OrderHelperParam.Delivery();
    }

    @Data
    public static class MallDiscount {
        private Integer platformCouponId = 0;
    }

}
