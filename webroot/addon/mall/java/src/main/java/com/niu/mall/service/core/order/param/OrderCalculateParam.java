package com.niu.mall.service.core.order.param;

import cn.hutool.json.JSONObject;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class OrderCalculateParam implements Serializable {
    private static final long serialVersionUID = 1L;

    private String orderKey = "";
    private OrderHelperParam.Delivery delivery = new OrderHelperParam.Delivery();
    private List<OrderHelperParam.SkuItem> skuData;
    private List<Integer> cartIds;
    private OrderHelperParam.Discount discount;
    private JSONObject extendData;
    private Integer siteId;
    private Integer memberId;
    private String orderFrom;

}
