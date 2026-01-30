package com.niu.core.event.sys;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

public abstract class WapIndexEventDefiner extends CallbackListener<WapIndexEventDefiner.WapIndexEvent> {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class WapIndexEvent extends Event {
        private List<String> siteAddon;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class WapIndexResult extends EventResult {
        private JSONObject result;
    }

}
