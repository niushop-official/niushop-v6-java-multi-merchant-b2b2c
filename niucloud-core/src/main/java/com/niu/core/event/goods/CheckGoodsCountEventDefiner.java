package com.niu.core.event.goods;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;

/**
 * 按分类统计商品数量（插件扩展点）
 */
public abstract class CheckGoodsCountEventDefiner extends CallbackListener<CheckGoodsCountEventDefiner.CheckGoodsCountEvent> {

    @Data
    public static class CheckGoodsCountEvent extends Event {
        private Integer categoryId;
    }

    @Data
    public static class CheckGoodsCountResult extends EventResult {
        private Long count;
    }

    public abstract CheckGoodsCountResult handleCallback(CheckGoodsCountEvent event);
}
