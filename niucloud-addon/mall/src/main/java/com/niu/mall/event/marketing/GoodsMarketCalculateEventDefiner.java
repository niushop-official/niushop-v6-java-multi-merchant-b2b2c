package com.niu.mall.event.marketing;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import com.niu.mall.service.core.order.param.OrderCalculateParam;
import com.niu.mall.service.core.order.param.OrderHelperParam;
import com.niu.mall.service.core.order.vo.OrderCreateDataVo;
import lombok.Data;

import java.util.List;

/**
 * 活动信息
 */
public abstract class GoodsMarketCalculateEventDefiner extends CallbackListener<GoodsMarketCalculateEventDefiner.GoodsMarketCalculateEvent> {

    @Data
    public static class GoodsMarketCalculateEvent extends Event {
        private OrderCreateDataVo vo;
        private OrderCreateDataVo.Sku skuInfo;
        private List<OrderHelperParam.SkuItem> skuData;
    }

    @Data
    public static class GoodsMarketCalculateResult extends EventResult {
        private OrderCreateDataVo.Sku skuInfo;
        private String activeClass;
    }

    public abstract GoodsMarketCalculateResult handleCallback(GoodsMarketCalculateEvent event);
}
