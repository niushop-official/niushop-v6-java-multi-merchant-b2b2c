package com.niu.mall.listener.localdelivery;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.event.local_delivery.LocalDeliveryEvent;
import com.niu.mall.enums.active.ActiveClassEnum;
import com.niu.mall.event.marketing.ActiveEndAfterEvent;
import com.niu.mall.service.core.order.ICoreOrderFinishService;
import com.niu.mall.service.core.order.ICoreOrderGoodsService;
import com.niu.mall.service.core.order.param.OrderFinishParam;
import com.niu.mall.service.core.order.param.OrderGoodsDeliveryParam;
import com.niu.mall.service.site.marketing.IMallDiscountService;
import org.springframework.stereotype.Component;

@EventListen("mall")
@Component
public class LocalDeliveryEventListener extends AbstractListener {

    private ICoreOrderFinishService coreOrderFinishService() {
        return SpringContext.bean(ICoreOrderFinishService.class);
    }

    private ICoreOrderGoodsService coreOrderGoodsService() {
        return SpringContext.bean(ICoreOrderGoodsService.class);
    }


    public void handleEvent(LocalDeliveryEvent event) {
        if (event.getTradeType().equals("mall")) {

            switch (event.getEvent()){
                case "cancel":
                case "createOrderFailed":
                    OrderGoodsDeliveryParam orderGoodsDeliveryParam = new OrderGoodsDeliveryParam();
                    orderGoodsDeliveryParam.setOrderNo(event.getTradeNo());
                    coreOrderGoodsService().cancelOrderGoodsDelivery(orderGoodsDeliveryParam);
                    break;
                case "finish":
                    OrderFinishParam param = new OrderFinishParam();
                    param.setOrderNo(event.getTradeNo());
                    coreOrderFinishService().finish(param);
                    break;
                default:
                    break;
            }
        }
    }
}
