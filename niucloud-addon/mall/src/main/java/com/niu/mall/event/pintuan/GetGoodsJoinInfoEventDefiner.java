package com.niu.mall.event.pintuan;

import cn.hutool.json.JSONObject;
import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import com.niu.mall.service.core.order.vo.OrderCreateDataVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

public abstract class GetGoodsJoinInfoEventDefiner extends CallbackListener<GetGoodsJoinInfoEventDefiner.GetGoodsJoinInfoEvent> {

    /**
     * 事件主题定义
     */
    @Data
    @EqualsAndHashCode(callSuper=false)
    public static class GetGoodsJoinInfoEvent extends Event {

        private Integer[] goodsIds;
        private Integer isGetCount = 0;
        private Integer isAll = 0;

    }

    /**
     * 事件结果定义
     */
    @Data
    public static class GetGoodsJoinInfoResult extends EventResult {
        private JSONObject data;
        private Integer count = 0;
    }

    // 事件响应
    public abstract GetGoodsJoinInfoResult handleCallback(GetGoodsJoinInfoEvent event);
}
