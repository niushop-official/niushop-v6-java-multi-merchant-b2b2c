package com.niu.mall.listener.pay;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.event.pay.PaySuccessEvent;
import com.niu.mall.service.core.goods.ICoreGoodsStatService;
import com.niu.mall.service.core.order.ICoreOrderPayService;
import com.niu.mall.service.core.order.ICoreOrderService;
import com.niu.mall.service.core.order.param.OrderPayParam;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 支付成功
 */
@EventListen("mall")
@Component
public class PaySuccessListener extends AbstractListener {

    @Resource
    ICoreOrderPayService coreOrderService;

    public void handleEvent(PaySuccessEvent event) {
        if (event.getTradeType().equals("mall")) {
            OrderPayParam param = new OrderPayParam();
            param.setTradeId(event.getTradeId());
            param.setOutTradeNo(event.getPay().getOutTradeNo());
            coreOrderService.payOfGroup(param);


        }
    }
}
