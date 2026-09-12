package com.niu.mall.listener.localdelivery;

 import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.event.local_delivery.LocalDeliveryEvent;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.order.ICoreOrderFinishService;
import com.niu.mall.service.core.order.ICoreOrderGoodsService;
import com.niu.mall.service.core.order.param.OrderFinishParam;
import com.niu.mall.service.core.order.param.OrderGoodsDeliveryParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@EventListen("mall")
@Component
public class LocalDeliveryEventListener extends AbstractListener {

    private ICoreOrderFinishService coreOrderFinishService() {
        return SpringContext.bean(ICoreOrderFinishService.class);
    }

    private ICoreOrderGoodsService coreOrderGoodsService() {
        return SpringContext.bean(ICoreOrderGoodsService.class);
    }

    private MallOrderMapper mallOrderMapper() {
        return SpringContext.bean(MallOrderMapper.class);
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
                    // 配送完成回调幂等：交易单已完成时不阻断配送单完结（切勿先抛再吞，否则仍会 rollback-only）
                    MallOrder mallOrder = mallOrderMapper().selectOne(new QueryWrapper<MallOrder>()
                            .eq("order_no", event.getTradeNo())
                            .last("limit 1"));
                    if (mallOrder != null && OrderStatusEnum.FINISH.getStatus().toString().equals(mallOrder.getStatus())) {
                        log.info("LocalDelivery finish skipped, mall order already finished: {}", event.getTradeNo());
                        break;
                    }
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
