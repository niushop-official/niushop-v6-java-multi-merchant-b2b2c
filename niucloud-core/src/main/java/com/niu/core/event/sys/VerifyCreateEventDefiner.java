package com.niu.core.event.sys;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.experimental.Accessors;

import com.niu.core.service.core.verify.vo.CoreVerifyCreateCodeVo;

import java.util.List;
import java.util.Map;

/**
 * 事件定义者
 */
public abstract class VerifyCreateEventDefiner extends CallbackListener<VerifyCreateEventDefiner.VerifyCreateEvent> {

    /**
     * 事件主题： 核销创建
     */
    @Data
    @Accessors(chain = true)
    public static class VerifyCreateEvent extends Event {

        private String type;

        private Integer memberId;

        private Map<String, Object> data;

    }

    /**
     * 事件结果
     */
    @Data
    @Accessors(chain = true)
    public static class VerifyCreateEventResult extends EventResult {

        private Integer count;

        private String data;

        private String body;

        private String relateTag;

        private Long expireTime;

        /**
         * 是否使用业务自定义核销码
         */
        private Integer isDiy;

        /**
         * 自定义核销码列表
         */
        private List<CoreVerifyCreateCodeVo> diyVerifyCode;

    }

    // 事件响应
    public abstract VerifyCreateEventResult handleCallback(VerifyCreateEvent verifyCreateEvent);

}
