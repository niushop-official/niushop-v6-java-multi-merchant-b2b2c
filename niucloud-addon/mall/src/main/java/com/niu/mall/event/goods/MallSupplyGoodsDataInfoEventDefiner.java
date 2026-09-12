package com.niu.mall.event.goods;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import com.niu.mall.service.site.supply_goods.vo.MallSupplyGoodsInfoVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public abstract class MallSupplyGoodsDataInfoEventDefiner extends CallbackListener<MallSupplyGoodsDataInfoEventDefiner.MallSupplyGoodsDataInfoEvent> {

    @Data
    @EqualsAndHashCode(callSuper=false)
    public static class MallSupplyGoodsDataInfoEvent extends Event {
        private Integer goodsId;
    }

    @Data
    public static class MallSupplyGoodsDataResult extends EventResult {
        MallSupplyGoodsInfoVo result;
    }

    public abstract MallSupplyGoodsDataResult handleCallback(MallSupplyGoodsDataInfoEvent event);
}
