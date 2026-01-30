package com.niu.core.event.local_delivery;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryOrder;
import com.niu.core.service.api.delivery.vo.TrackPointsVo;
import lombok.Data;

import java.util.List;

public abstract class GetLocalDeliveryTrackEventDefiner extends CallbackListener<GetLocalDeliveryTrackEventDefiner.GetLocalDeliveryTrackEvent> {

    /**
     * 事件主题定义
     */
    @Data
    public static class GetLocalDeliveryTrackEvent extends Event {
        private DeliveryLocalDeliveryOrder localDeliveryOrder;
        private Integer memberId;

    }

    /**
     * 事件结果定义
     */
    @Data
    public static class GetLocalDeliveryTrackResult extends EventResult {
        private List<TrackPointsVo> points;

    }

    // 事件响应
    public abstract GetLocalDeliveryTrackResult handleCallback(GetLocalDeliveryTrackEvent event);
}
