package com.niu.core.listener.recharge.order;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.entity.recharge.RechargeOrder;
import com.niu.core.event.sys.WapOrderDetailPathEventDefiner;
import com.niu.core.service.core.recharge.order.ICoreOrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;


@EventCallback("core")
@Component
public class WapOrderDetailPathListener extends WapOrderDetailPathEventDefiner {

    @Resource
    private ICoreOrderService coreOrderService;

    @Override
    public WapOrderDetailPathResult handleCallback(WapOrderDetailPathEvent event) {
        WapOrderDetailPathResult result = new WapOrderDetailPathResult();
        String tradeType = event.getPayInfo().getTradeType();
        if(tradeType.equals("recharge")){
            RechargeOrder order = coreOrderService.orderInfoByOutTradeNo(event.getPayInfo().getOutTradeNo());
            if(ObjectUtil.isEmpty( order)){
                result.setPath("app/pages/recharge/recharge_record");
                return result;
            }
            result.setPath("app/pages/recharge/recharge_record_detail?id="+order.getOrderId());
            return result;
        }
        return null;
    }
}
