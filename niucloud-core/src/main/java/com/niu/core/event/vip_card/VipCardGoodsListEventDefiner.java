package com.niu.core.event.vip_card;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import com.niu.core.service.api.vip_card.card.vo.ApiVipCardGoodsVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

public abstract class VipCardGoodsListEventDefiner extends CallbackListener<VipCardGoodsListEventDefiner.VipCardGoodsListEvent> {

    public abstract VipCardGoodsListEventDefiner.VipCardGoodsListEventResult handleCallback(VipCardGoodsListEventDefiner.VipCardGoodsListEvent vipCardGoodsListEvent);


    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(chain = true)
    public static class VipCardGoodsListEvent extends Event {
        /**
         * 数量（或其它业务参数）
         */
        private Integer num;

        /**
         * 分组标识
         */
        private String group;
    }

    /**
     * 事件结果
     * <p>
     * 返回数据类型
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(chain = true)
    public static class VipCardGoodsListEventResult extends EventResult {

        /**
         * 返回的商品列表（或其它数据）
         */
        private List<ApiVipCardGoodsVo> resultData;
    }
}