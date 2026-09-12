package com.niu.core.event.verify;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 根据核销码从业务表加载核销信息（缓存 miss 时回退）
 * 对应 PHP: VerifyInfoByCode
 */
public abstract class VerifyInfoByCodeEventDefiner extends CallbackListener<VerifyInfoByCodeEventDefiner.VerifyInfoByCodeEvent> {

    @Data
    @Accessors(chain = true)
    public static class VerifyInfoByCodeEvent extends Event {

        private String code;
    }

    @Data
    @Accessors(chain = true)
    public static class VerifyInfoByCodeResult extends EventResult {

        private Map<String, Object> verifyData;
    }

    public abstract VerifyInfoByCodeResult handleCallback(VerifyInfoByCodeEvent event);
}
