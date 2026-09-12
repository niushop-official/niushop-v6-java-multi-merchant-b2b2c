package com.niu.mall.event.goods;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.entity.goods.MallGoods;
import com.niu.core.entity.goods.MallGoodsSku;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class AfterGoodsEditEvent extends Event {

    private Integer goodsId;

    private MallGoods goodsData;

    private List<MallGoodsSku> skuData;
}
