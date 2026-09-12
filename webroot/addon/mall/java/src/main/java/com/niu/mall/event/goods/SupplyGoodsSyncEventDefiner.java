package com.niu.mall.event.goods;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;

@Data
public abstract class SupplyGoodsSyncEventDefiner extends CallbackListener<SupplyGoodsSyncEventDefiner.SupplyGoodsSyncEvent> {

    @Data
    public static class SupplyGoodsSyncEvent extends Event {
        private Integer goodsId;
    }

    @Data
    public static class SupplyGoodsSyncResult extends EventResult {
        private String goodsName;
        private String subTitle;
        private String goodsCover;
        private String goodsImage;
        /** 商品视频（ENABLE_SYNC_FIELD） */
        private String goodsVideo;
        private String goodsDesc;
    }

    public abstract SupplyGoodsSyncResult handleCallback(SupplyGoodsSyncEvent event);
}
