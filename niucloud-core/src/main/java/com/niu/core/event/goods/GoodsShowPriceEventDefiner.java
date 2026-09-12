package com.niu.core.event.goods;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 获取商品展示价格（对齐 PHP GoodsShowPrice，多插件回调）
 */
public abstract class GoodsShowPriceEventDefiner
        extends CallbackListener<GoodsShowPriceEventDefiner.GoodsShowPriceEvent> {

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class GoodsShowPriceEvent extends Event {
        private Map<String, Object> skuInfo;
        private Integer memberId;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class GoodsShowPriceResult extends EventResult {
        private BigDecimal showPrice;
        private String showType;
        private Integer activeId;
    }

    public abstract GoodsShowPriceResult handleCallback(GoodsShowPriceEvent event);
}
