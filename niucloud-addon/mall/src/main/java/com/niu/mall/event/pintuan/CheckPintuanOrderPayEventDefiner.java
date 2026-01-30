package com.niu.mall.event.pintuan;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import com.niu.mall.service.core.order.vo.OrderCreateDataVo;
import lombok.Data;

@Data
public abstract class CheckPintuanOrderPayEventDefiner extends CallbackListener<CheckPintuanOrderPayEventDefiner.CheckPintuanOrderPayEvent> {

    @Data
    public static class CheckPintuanOrderPayEvent extends Event {
        private Integer groupId;
    }

    @Data
    public static class CheckPintuanOrderPayResult extends EventResult {
        private String error;
    }

    public abstract CheckPintuanOrderPayResult handleCallback(CheckPintuanOrderPayEvent event);
}
