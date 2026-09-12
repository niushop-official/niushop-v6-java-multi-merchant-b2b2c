package com.niu.mall.listener.fenxiao.order;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.enums.fenxiao.FenxiaoTradeTypeEnum;
import com.niu.core.event.fenxiao.AfterFenxiaoOrderCreateEvent;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.mapper.order.MallOrderMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 分销订单创建后回写商城订单佣金
 */
@EventListen("mall")
@Component
public class AfterFenxiaoOrderCreateListener extends AbstractListener {

    @Resource
    MallOrderMapper mallOrderMapper;

    /**
     * 分销订单创建后回写商城订单 shop_commission
     *
     * @param event 分销订单创建后事件
     */
    public void handleEvent(AfterFenxiaoOrderCreateEvent event) {
        // 非商城业务类型，不处理
        if (!FenxiaoTradeTypeEnum.MALL.getType().equals(event.getTradeType())) {
            return;
        }
        // 回写店铺支付佣金
        mallOrderMapper.update(null, new UpdateWrapper<MallOrder>()
                .eq("order_id", event.getOrderId())
                .set("shop_commission", event.getShopCommission()));
    }
}
