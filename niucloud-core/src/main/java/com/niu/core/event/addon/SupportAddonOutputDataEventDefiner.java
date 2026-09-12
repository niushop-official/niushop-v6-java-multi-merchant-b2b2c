package com.niu.core.event.addon;

import cn.hutool.json.JSONObject;
import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 插件输出数据事件
 * 对应 PHP: SupportAddonOutputData
 */
public abstract class SupportAddonOutputDataEventDefiner extends CallbackListener<SupportAddonOutputDataEventDefiner.SupportAddonOutputDataEvent> {

    public abstract SupportAddonOutputDataEventResult handleCallback(SupportAddonOutputDataEvent event);

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(chain = true)
    public static class SupportAddonOutputDataEvent extends Event {
        private String key;
        private JSONObject param;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(chain = true)
    public static class SupportAddonOutputDataEventResult extends EventResult {
        private Object data;
    }
}
