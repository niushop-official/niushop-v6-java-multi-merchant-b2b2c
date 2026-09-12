package com.niu.core.event.fenxiao.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 分销业务类型 JSON 配置（路径等，name 来自枚举）
 */
@Data
public class FenxiaoTradeTypeConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务类型标识
     */
    private String type;

    /**
     * 商品详情页路径
     */
    @JsonProperty("goods_detail_path")
    private String goodsDetailPath;

    /**
     * 订单详情页路径
     */
    @JsonProperty("order_detail_path")
    private String orderDetailPath;
}
