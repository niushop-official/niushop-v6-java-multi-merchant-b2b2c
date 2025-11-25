package com.niu.core.event.verify;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

public abstract class VerifierCheckEventDefiner extends CallbackListener<VerifierCheckEventDefiner.VerifierCheckEvent> {

    @Data
    @Accessors(chain = true)
    public static class VerifierCheckEvent extends Event {

        private Integer verifyMemberId;
        private String type;
        private Map<String, Object> data;
        private Integer siteId;
    }

    @Data
    @Accessors(chain = true)
    public static class VerifierCheckResult extends EventResult {

        private List<Integer> verifyMemberIds;
    }

    public abstract VerifierCheckResult handleCallback(VerifierCheckEvent event);

}

