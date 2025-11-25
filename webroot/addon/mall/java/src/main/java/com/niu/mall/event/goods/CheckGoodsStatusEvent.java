package com.niu.mall.event.goods;

import com.niu.core.common.component.context.event.Event;
import lombok.Data;

import java.util.List;

@Data
public class CheckGoodsStatusEvent extends Event {

    private List<Integer> goodsIds;
}
