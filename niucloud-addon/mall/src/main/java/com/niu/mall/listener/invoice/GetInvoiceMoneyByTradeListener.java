package com.niu.mall.listener.invoice;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.exception.CommonException;
import com.niu.core.event.order.GetInvoiceMoneyByTradeEventDefiner;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.event.pintuan.CheckInvoiceStatusEventDefiner;
import com.niu.mall.mapper.order.MallOrderMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
@EventCallback("mall")
public class GetInvoiceMoneyByTradeListener extends GetInvoiceMoneyByTradeEventDefiner {

    @Resource
    MallOrderMapper mallOrderMapper;

    @Override
    public GetInvoiceMoneyByTradeResult handleCallback(GetInvoiceMoneyByTradeEvent event) {
        GetInvoiceMoneyByTradeResult result = new GetInvoiceMoneyByTradeResult();
        if("mall".equals(event.getTradeType())){
            MallOrder order = mallOrderMapper.selectOne(new QueryWrapper<MallOrder>().eq("order_id", event.getTradeId())
                    .eq("member_id", event.getMemberId()));
            if (ObjectUtil.isNotEmpty( order)){
                if (order.getOrderType().equals("mall")){
                    List<String> statusList = List.of(OrderStatusEnum.WAIT_PAY.getStatus().toString(), OrderStatusEnum.CLOSE.getStatus().toString());
                    if (statusList.contains(order.getStatus())){
                        throw new CommonException("开票申请失败");
                    }
                }else{
                    //校验发票生效状态
                    CheckInvoiceStatusEventDefiner.CheckInvoiceStatusEvent eventPintuan = new CheckInvoiceStatusEventDefiner.CheckInvoiceStatusEvent();
                    eventPintuan.setOrderType(order.getOrderType());
                    eventPintuan.setStatus(order.getStatus());
                    List<CheckInvoiceStatusEventDefiner.CheckInvoiceStatusResult> resultList = EventAndSubscribeOfPublisher.publishAndCallback(eventPintuan);
                    Boolean er = false;
                    if(!resultList.isEmpty()){
                        for (CheckInvoiceStatusEventDefiner.CheckInvoiceStatusResult item : resultList){
                            if(item == null) continue;
                            if(ObjectUtil.isNotEmpty(item.getData())){
                                er = item.getData();
                            }
                        }
                    }
                    if (!er){
                        throw new CommonException("开票申请失败");
                    }
                }
                result.setSiteId(order.getSiteId());
                BigDecimal orderMoney = order.getOrderMoney() != null ? order.getOrderMoney() : BigDecimal.ZERO;
                BigDecimal refundMoney = order.getRefundMoney() != null ? order.getRefundMoney() : BigDecimal.ZERO;
                result.setInvoiceMoney(orderMoney.subtract(refundMoney).setScale(2, RoundingMode.HALF_UP));
                return result;
            }
        }
        return null;
    }
}
