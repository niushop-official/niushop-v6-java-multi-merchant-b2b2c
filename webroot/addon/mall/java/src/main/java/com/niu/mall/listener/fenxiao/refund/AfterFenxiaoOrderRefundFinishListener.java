package com.niu.mall.listener.fenxiao.refund;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.enums.fenxiao.FenxiaoTradeTypeEnum;
import com.niu.core.event.fenxiao.AfterFenxiaoOrderRefundFinishEvent;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.mapper.order.MallOrderMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 分销退款完成后回写商城订单退还佣金
 */
@EventListen("mall")
@Component
public class AfterFenxiaoOrderRefundFinishListener extends AbstractListener {

    @Resource
    MallOrderMapper mallOrderMapper;

    /**
     * 分销退款完成后累加商城订单 shop_refund_commission
     *
     * @param event 分销退款完成事件
     */
    public void handleEvent(AfterFenxiaoOrderRefundFinishEvent event) {
        // 非商城业务类型，不处理
        if (!FenxiaoTradeTypeEnum.MALL.getType().equals(event.getTradeType())) {
            return;
        }
        // 累加退还佣金
        mallOrderMapper.update(null, new UpdateWrapper<MallOrder>()
                .eq("site_id", event.getSiteId())
                .eq("order_id", event.getOrderId())
                .setSql("shop_refund_commission = shop_refund_commission + " + event.getTotalCommission()));
    }
}
