package com.niu.mall.event.pintuan;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import com.niu.mall.service.core.order.vo.OrderCreateDataVo;
import lombok.Data;

@Data
public abstract class CheckPintuanOrderCreateEventDefiner extends CallbackListener<CheckPintuanOrderCreateEventDefiner.CheckPintuanOrderreateEvent> {

    @Data
    public static class CheckPintuanOrderreateEvent extends Event {
        private OrderCreateDataVo vo;
    }

    @Data
    public static class CheckPintuanOrderreateResult extends EventResult {
        private String error;
    }

    public abstract CheckPintuanOrderreateResult handleCallback(CheckPintuanOrderreateEvent event);
}
