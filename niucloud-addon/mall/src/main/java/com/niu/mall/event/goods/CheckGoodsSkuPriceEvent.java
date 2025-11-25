package com.niu.mall.event.goods;

import com.niu.core.common.component.context.event.Event;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.entity.goods.MallGoodsSku;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class CheckGoodsSkuPriceEvent extends Event {

    private Integer goodsId;

    private Integer status;

    private Map<Integer, BigDecimal> skuData;
}
