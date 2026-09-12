package com.niu.core.event.goods;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 满减活动按 trade_type 取商品 SKU（对齐 PHP FullReductionGoods）
 */
public abstract class FullReductionGoodsEventDefiner
        extends CallbackListener<FullReductionGoodsEventDefiner.FullReductionGoodsEvent> {

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class FullReductionGoodsEvent extends Event {
        private String tradeType;
        private Integer skuId;
        private Integer goodsId;
        private Boolean allSkus;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class FullReductionGoodsResult extends EventResult {
        private Integer goodsId;
        private Integer skuId;
        private String skuName;
        private BigDecimal price;
        private Integer stock;
        private String skuImage;
        private Map<String, Object> goodsInfo;
        private List<Integer> skuIds;
    }

    public abstract FullReductionGoodsResult handleCallback(FullReductionGoodsEvent event);
}
