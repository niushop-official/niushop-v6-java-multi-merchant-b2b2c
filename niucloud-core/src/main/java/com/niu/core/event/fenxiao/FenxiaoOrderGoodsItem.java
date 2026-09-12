package com.niu.core.event.fenxiao;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 分销订单项数据（通用，跨业务类型）
 */
@Data
public class FenxiaoOrderGoodsItem implements Serializable {

    /**
     * 订单项ID
     */
    private Integer orderGoodsId;

    /**
     * 商品ID
     */
    private Integer goodsId;

    /**
     * 规格ID
     */
    private Integer skuId;

    /**
     * 购买数量
     */
    private Integer num;

    /**
     * 订单项金额（分销计算用）
     */
    private BigDecimal orderGoodsMoney;

    /**
     * 供应订单ID
     */
    private Integer supplyOrderId;

    /**
     * 供应商站点ID
     */
    private Integer supplyId;

    /**
     * 商品成本金额
     */
    private BigDecimal costGoodsMoney;

    /**
     * 订单项快照 JSON（snake_case）
     */
    private String tradeItemJson;
}
