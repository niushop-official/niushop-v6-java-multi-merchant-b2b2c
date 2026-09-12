package com.niu.core.event.sys;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * 获取应用列表事件
 * 对应 PHP: ShowApp
 */
public abstract class ShowAppEventDefiner extends CallbackListener<ShowAppEventDefiner.ShowAppEvent> {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ShowAppEvent extends Event {
        private String appType;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ShowAppResult extends EventResult {
        private List<Map<String, String>> list;
    }

    public abstract ShowAppResult handleCallback(ShowAppEvent event);
}
