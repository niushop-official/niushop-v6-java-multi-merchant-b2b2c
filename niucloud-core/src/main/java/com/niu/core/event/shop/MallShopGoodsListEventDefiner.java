package com.niu.core.event.shop;


import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import com.niu.core.service.api.shop.vo.MallGoodsListVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
public abstract class MallShopGoodsListEventDefiner  extends CallbackListener<MallShopGoodsListEventDefiner.MallShopGoodsListEvent> {




    @Override
    public abstract MallShopGoodsListEventDefiner.MallShopGoodsListEventResult handleCallback(MallShopGoodsListEventDefiner.MallShopGoodsListEvent event);



    /**
     * 事件结果定义
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class MallShopGoodsListEventResult extends EventResult {

        //店铺街
        private List<MallGoodsListVo> goods;
        //首页-甄选好店
        private List<MallGoodsListVo> goodsList;
    }

    /**
     * 事件主题定义
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class MallShopGoodsListEvent extends Event {

        private Integer num = 3;
    }
}
