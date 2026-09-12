package com.niu.core.event.fenxiao;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * 分销会员订单列表统计
 * 对应 PHP: FenxiaoMemberOrderList
 */
public abstract class FenxiaoMemberOrderListEventDefiner extends CallbackListener<FenxiaoMemberOrderListEventDefiner.FenxiaoMemberOrderListEvent> {

    @Override
    public abstract FenxiaoMemberOrderListEventResult handleCallback(FenxiaoMemberOrderListEvent event);

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class FenxiaoMemberOrderListEvent extends Event {
        /** 会员ID列表 */
        private List<Integer> memberIds;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class FenxiaoMemberOrderListEventResult extends EventResult {
        /** 会员订单统计列表 */
        private List<Map<String, Object>> orderList;
    }
}
