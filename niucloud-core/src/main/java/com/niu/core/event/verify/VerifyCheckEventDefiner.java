package com.niu.core.event.verify;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

public abstract class VerifyCheckEventDefiner extends CallbackListener<VerifyCheckEventDefiner.VerifyCheckEvent> {

    @Data
    @Accessors(chain = true)
    public static class VerifyCheckEvent extends Event {

        private Map<String, Object> verifyData;
    }

    @Data
    @Accessors(chain = true)
    public static class VerifyCheckResult extends EventResult {

        private Map<String, Object> processedData;
    }

    public abstract VerifyCheckResult handleCallback(VerifyCheckEvent event);

}

