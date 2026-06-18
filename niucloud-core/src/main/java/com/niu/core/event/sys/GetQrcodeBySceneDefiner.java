package com.niu.core.event.sys;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * 按业务场景解析小程序页与场景参数（对齐 PHP GetQrcodeByScene 事件）
 */
public abstract class GetQrcodeBySceneDefiner extends CallbackListener<GetQrcodeBySceneDefiner.GetQrcodeBySceneEvent> {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class GetQrcodeBySceneEvent extends Event {
        private String scene;
        private Map<String, Object> param;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class GetQrcodeBySceneResult extends EventResult {
        private String page;
        /**
         * 与 PHP 一致：每项含 key、value（如 goods_id）
         */
        private List<Map<String, Object>> sceneData;
    }

    public abstract GetQrcodeBySceneResult handleCallback(GetQrcodeBySceneEvent event);
}
