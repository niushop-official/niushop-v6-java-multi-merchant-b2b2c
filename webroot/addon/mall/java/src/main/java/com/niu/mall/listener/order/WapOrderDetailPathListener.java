package com.niu.mall.listener.order;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.event.sys.WapOrderDetailPathEventDefiner;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.mapper.order.MallOrderMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;


@EventCallback("mall")
@Component
public class WapOrderDetailPathListener extends WapOrderDetailPathEventDefiner {

    @Resource
    private MallOrderMapper mallOrderMapper;

    @Override
    public WapOrderDetailPathResult handleCallback(WapOrderDetailPathEvent event) {
        WapOrderDetailPathResult result = new WapOrderDetailPathResult();
        String tradeType = event.getPayInfo().getTradeType();
        if(tradeType.equals("mall")){
            MallOrder order = mallOrderMapper.selectOne(new QueryWrapper<MallOrder>().eq("out_trade_no", event.getPayInfo().getOutTradeNo()));
            if(ObjectUtil.isEmpty( order)){
                result.setPath("addon/mall/pages/order/list");
                return result;
            }
            result.setPath("addon/mall/pages/order/detail?order_id="+order.getOrderId());
            return result;
        }
        return null;
    }
}
