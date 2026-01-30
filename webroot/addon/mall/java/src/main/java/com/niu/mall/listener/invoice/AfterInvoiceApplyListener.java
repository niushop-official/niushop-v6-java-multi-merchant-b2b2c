package com.niu.mall.listener.invoice;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.event.order.AfterInvoiceApplyEvent;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.mapper.order.MallOrderMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@EventListen("mall")
@Component
@Slf4j
public class AfterInvoiceApplyListener extends AbstractListener {

    @Resource
    MallOrderMapper mallOrderMapper;

    public void handleEvent(AfterInvoiceApplyEvent event) {
        try {
            if ("mall".equals(event.getTradeType())){
                MallOrder order = mallOrderMapper.selectById(event.getTradeId());
                if(ObjectUtil.isNotEmpty(order)){
                    order.setInvoiceId(event.getInvoiceId());
                    mallOrderMapper.updateById( order);
                }
            }
        }catch (Exception e){
            log.error("AfterInvoiceApplyEvent error: {}", e.getMessage());
        }
    }
}
