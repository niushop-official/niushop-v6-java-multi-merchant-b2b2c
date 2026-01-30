package com.niu.mall.listener.pay;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.event.pay.PayCreateEventDefiner;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGroup;
import com.niu.mall.enums.order.OrderGroupEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.event.order.CheckOrderOfCreateEventDefiner;
import com.niu.mall.event.pintuan.CheckPintuanOrderPayEventDefiner;
import com.niu.mall.service.core.order.ICoreOrderGroupService;
import com.niu.mall.service.core.order.ICoreOrderService;
import com.niu.mall.service.core.order.vo.MallOrderGroupVo;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

import java.util.List;

/**
 * 获取支付数据
 */
@EventCallback("mall")
@Component
public class PayCreateListener extends PayCreateEventDefiner {

    @Resource
    ICoreOrderGroupService coreOrderGroupService;

    @Override
    public PayCreateEventResult handleCallback(PayCreateEvent event) {
        if (!event.getTradeType().equals("mall")) return null;

        MallOrderGroupVo order = coreOrderGroupService.getInfo(event.getTradeId());
        if (order == null) throw new CommonException("订单不存在");
        if (!order.getStatus().equals(OrderGroupEnum.WAIT_PAY.getStatus())) throw new CommonException("只有待支付的订单可以支付");

        //商城订单支付前校验
        CheckPintuanOrderPayEventDefiner.CheckPintuanOrderPayEvent payCheckEvent = new CheckPintuanOrderPayEventDefiner.CheckPintuanOrderPayEvent();
        payCheckEvent.setSiteId(RequestUtils.siteId());
        payCheckEvent.setGroupId(order.getGroupId());
        List<CheckPintuanOrderPayEventDefiner.CheckPintuanOrderPayResult> resultList = EventAndSubscribeOfPublisher.publishAndCallback(payCheckEvent);
        if(!resultList.isEmpty()){
            for (CheckPintuanOrderPayEventDefiner.CheckPintuanOrderPayResult item : resultList){
                if(item != null && ObjectUtil.isNotEmpty( item.getError())){
                    throw new CommonException(item.getError());
                }
            }
        }
        //添加订单支付表
        PayCreateEventResult result = new PayCreateEventResult();
        result.setTradeId(event.getTradeId());
        result.setTradeType(event.getTradeType());
        result.setMainType("member");
        result.setMainId(order.getMemberId());
        result.setMoney(order.getOrderMoney());
        result.setBody(order.getBody());
        result.setSiteId(order.getSiteId());

        return result;
    }

}
