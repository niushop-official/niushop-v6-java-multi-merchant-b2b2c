package com.niu.core.event.site;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 获取商圈列表事件
 */
public abstract class GetCircleListEventDefiner extends CallbackListener<GetCircleListEventDefiner.GetCircleListEvent> {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class GetCircleListEvent extends Event {
        /**
         * 管理员uid
         */
        private Integer uid;

        /**
         * 商圈名称
         */
        private String circleName;

        /**
         * 状态：1启用、0禁用
         */
        private Integer status;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class GetCircleListResult extends EventResult {
        private List<?> list;
    }

    public abstract GetCircleListResult handleCallback(GetCircleListEvent event);
}
