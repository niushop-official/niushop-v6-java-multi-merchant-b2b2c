package com.niu.mall.event.order;

import com.niu.core.common.component.context.event.Event;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.entity.order.MallOrderRefund;
import com.niu.mall.service.core.refund.param.ActiveRefundParam;
import lombok.Data;

@Data
public class ShopOrderRefundActiveCreateAfterEvent extends Event {

    private ActiveRefundParam refundParam;

    private MallOrderRefund refund;

    private MallOrderGoods orderGoods;
}
