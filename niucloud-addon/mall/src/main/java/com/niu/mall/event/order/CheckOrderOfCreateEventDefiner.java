package com.niu.mall.event.order;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.event.marketing.GoodsMarketCalculateEventDefiner;
import com.niu.mall.service.core.order.vo.OrderCreateDataVo;
import lombok.Data;

import java.math.BigDecimal;

@Data
public abstract class CheckOrderOfCreateEventDefiner extends CallbackListener<CheckOrderOfCreateEventDefiner.CheckOrderOfCreateEvent> {

    @Data
    public static class CheckOrderOfCreateEvent extends Event {
        private Integer supplyId;
        private Integer supplySkuId;
        private BigDecimal goodsMoney;
        private BigDecimal discountMoney;
        private BigDecimal mallDiscountMoney;
        private Integer num;
        private BigDecimal costPrice;
        private String goodsName;
        private String skuName;
    }

    @Data
    public static class CheckOrderOfCreateResult extends EventResult {
        private String error;
    }

    public abstract CheckOrderOfCreateResult handleCallback(CheckOrderOfCreateEvent event);
}
