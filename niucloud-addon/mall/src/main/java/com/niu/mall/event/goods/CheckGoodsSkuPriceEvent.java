package com.niu.mall.event.goods;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.entity.goods.MallGoods;
import com.niu.core.entity.goods.MallGoodsSku;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
public class CheckGoodsSkuPriceEvent extends Event {

    private Integer goodsId;

    private Integer status;

    private Map<Integer, BigDecimal> skuData;
}
