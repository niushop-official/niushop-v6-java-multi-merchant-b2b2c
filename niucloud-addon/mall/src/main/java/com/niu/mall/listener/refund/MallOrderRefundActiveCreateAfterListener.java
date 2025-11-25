package com.niu.mall.listener.refund;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.event.common.CommonEvent;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.entity.order.MallOrderRefund;
import com.niu.mall.enums.order.OrderRefundActionTypeEnum;
import com.niu.mall.enums.order.OrderRefundStatusEnum;
import com.niu.mall.event.order.ShopOrderRefundActiveCreateAfterEvent;
import com.niu.mall.event.refund.SyncSupplyRefundCloseUpdateEvent;
import com.niu.mall.event.refund.SyncSupplyRefundNoEvent;
import com.niu.mall.service.core.refund.ICoreRefundService;
import com.niu.mall.service.core.refund.param.ActiveRefundParam;
import com.niu.mall.service.core.refund.param.AddRefundLogParam;
import com.niu.mall.service.core.refund.param.RefundFinishParam;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@EventListen("mall")
@Component
public class MallOrderRefundActiveCreateAfterListener extends AbstractListener {

    @Resource
    ICoreRefundService coreRefundService;

    public void handleEvent(ShopOrderRefundActiveCreateAfterEvent event) {
        ActiveRefundParam refundParam = event.getRefundParam();
        MallOrderRefund refund = event.getRefund();
        MallOrderGoods orderGoods = event.getOrderGoods();

        AddRefundLogParam logParam = new AddRefundLogParam();
        logParam.setOrderRefundNo(refund.getOrderRefundNo());
        logParam.setStatus(Integer.valueOf(refund.getStatus()));
        logParam.setMainType(refundParam.getMainType());
        logParam.setMainId(refundParam.getMainId());
        logParam.setType(OrderRefundActionTypeEnum.ACTIVE_REFUND_ACTION.getType());
        logParam.setContent("商品【" + orderGoods.getGoodsName() + " " + orderGoods.getSkuName() + "】商家主动退款，退款金额：" + refund.getMoney() + "元，退款说明：" + refund.getRemark());
        coreRefundService.addLog(logParam);

        //todo  尝试同步商城退款单号、退款id
        if(ObjectUtil.isNotEmpty(refund.getSupplyRefundId()) &&  refund.getSupplyRefundId() > 0){
            // 触发事件 退款同步供货商的退款单据号
            SyncSupplyRefundNoEvent eventRefund = new SyncSupplyRefundNoEvent();
            eventRefund.addAppSign("supply");
            eventRefund.setRefundId(refund.getSupplyRefundId());
            eventRefund.setMallRefundId(refund.getRefundId());
            eventRefund.setOrderRefundNo(refund.getOrderRefundNo());
            eventRefund.setSiteId(RequestUtils.siteId());
            EventAndSubscribeOfPublisher.publishAll(eventRefund);
        }
        //todo  尝试同步商城退款单号到供货商订单项
        if(ObjectUtil.isNotEmpty(orderGoods.getSupplyOrderGoodsId()) &&  orderGoods.getSupplyOrderGoodsId() > 0){
            // 触发事件 退款同步供货商的退款单据号
            SyncSupplyRefundCloseUpdateEvent eventRefund = new SyncSupplyRefundCloseUpdateEvent();
            eventRefund.addAppSign("supply");
            eventRefund.setOrderGoodsId(orderGoods.getSupplyOrderGoodsId());
            eventRefund.setOrderRefundNo(refund.getOrderRefundNo());
            eventRefund.setSiteId(RequestUtils.siteId());
            EventAndSubscribeOfPublisher.publishAll(eventRefund);
        }

        if (refund.getStatus().equals(OrderRefundStatusEnum.STORE_AGREE_REFUND_WAIT_TRANSFER.getStatus().toString())) {
            if (refund.getMoney().compareTo(BigDecimal.ZERO) > 0) {
                coreRefundService.toTransfer(refund);
            } else {
                RefundFinishParam finishParam = new RefundFinishParam();
                finishParam.setOrderRefundNo(refund.getOrderRefundNo());
                coreRefundService.finish(finishParam);
            }
        }
    }
}
