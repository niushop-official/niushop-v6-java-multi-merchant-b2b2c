package com.niu.mall.service.core.order.param;

import cn.hutool.json.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class MallOrderCalculateParam implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer memberId;
    private String orderFrom;
    @Schema(description = "是否使用积分抵扣功能")
    private Integer isUsePointDeduct;
    private Map<Integer, Body> body;
    private MallDiscount mallDiscount = new MallDiscount();
    private OrderHelperParam.Delivery delivery = new OrderHelperParam.Delivery();
    private Object position = new OrderHelperParam.Position();
    private JSONObject extendData;

    @Data
    public static class Body {
        private String orderKey;
        private String orderType;
        private String memberRemark;
        private List<Integer> cartIds = new ArrayList<>();
        private List<OrderHelperParam.SkuItem> skuData = new ArrayList<>();
        private OrderHelperParam.Discount discount = new OrderHelperParam.Discount();
        private OrderHelperParam.Delivery delivery = new OrderHelperParam.Delivery();
        private JSONObject extendData;
    }

    @Data
    public static class MallDiscount {
        private Integer platformCouponId = 0;
    }

}
