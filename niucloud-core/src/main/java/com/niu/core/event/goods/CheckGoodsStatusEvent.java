package com.niu.core.event.goods;

import com.niu.core.common.component.context.event.Event;
import lombok.Data;

import java.util.List;

/**
 * 上架商品校验商品状态事件参数
 */
@Data
public class CheckGoodsStatusEvent extends Event {

    private List<Integer> goodsIds;
}
