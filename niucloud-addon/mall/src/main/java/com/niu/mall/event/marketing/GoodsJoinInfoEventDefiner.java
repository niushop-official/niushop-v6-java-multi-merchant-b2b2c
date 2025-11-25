
package com.niu.mall.event.marketing;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import com.niu.mall.service.core.order.vo.OrderCreateDataVo;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 获取商品参与的营销活动
 */
@Data
public abstract class GoodsJoinInfoEventDefiner extends CallbackListener<GoodsJoinInfoEventDefiner.GoodsJoinInfoEvent> {

    @Data
    public static class GoodsJoinInfoEvent extends Event {
        //商品ID列表
        private List<Integer> goodsIds;
        //是否获取数量
        private Integer isGetCount =0;
        //是否全选
        private  Integer isAll =0;
        //查询条件
        private Map<String, Object> where;
    }
    @Data
    public static class GoodsJoinInfoResult extends EventResult {
        private Map<Integer, Map<String, Object>> info;
        private Integer count;
    }

    public abstract GoodsJoinInfoResult handleCallback(GoodsJoinInfoEvent event);

}
