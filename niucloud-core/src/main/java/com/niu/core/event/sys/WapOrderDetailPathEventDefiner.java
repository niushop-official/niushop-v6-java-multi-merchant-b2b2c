package com.niu.core.event.sys;

import cn.hutool.json.JSONObject;
import com.niu.core.common.component.context.event.DefaultEventResult;
import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import com.niu.core.entity.pay.Pay;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 事件定义者： 核销操作
 */
public abstract class WapOrderDetailPathEventDefiner extends CallbackListener<WapOrderDetailPathEventDefiner.WapOrderDetailPathEvent> {

    /**
     * 事件主题： 核销创建
     */
    @Data
    @Accessors(chain = true)
    public static class WapOrderDetailPathEvent extends Event {
        private Pay payInfo;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class WapOrderDetailPathResult extends EventResult {
        private String path;
    }

    // 事件响应
    public abstract WapOrderDetailPathResult handleCallback(WapOrderDetailPathEvent verifyEvent);

}
