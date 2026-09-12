package com.niu.core.event.site;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 店铺二维码类型
 * 对应 PHP: SiteQrcodeType 事件
 */
public abstract class SiteQrcodeTypeEventDefiner extends CallbackListener<SiteQrcodeTypeEventDefiner.SiteQrcodeTypeEvent> {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class SiteQrcodeTypeEvent extends Event {
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class SiteQrcodeTypeResult extends EventResult {
        private List<Map<String, String>> types = new ArrayList<>();
    }

    public abstract SiteQrcodeTypeResult handleCallback(SiteQrcodeTypeEvent event);
}
