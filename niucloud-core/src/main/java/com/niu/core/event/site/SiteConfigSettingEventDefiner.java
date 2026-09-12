package com.niu.core.event.site;

import cn.hutool.json.JSONObject;
import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 店铺通用设置事件
 * 对应 PHP: SiteConfigSetting
 */
public abstract class SiteConfigSettingEventDefiner extends CallbackListener<SiteConfigSettingEventDefiner.SiteConfigSettingEvent> {

    public abstract SiteConfigSettingEventResult handleCallback(SiteConfigSettingEvent event);

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(chain = true)
    public static class SiteConfigSettingEvent extends Event {
        private String tradeType;
        private JSONObject configData;
        /** get | set */
        private String operation;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(chain = true)
    public static class SiteConfigSettingEventResult extends EventResult {
        private JSONObject data;
    }
}
