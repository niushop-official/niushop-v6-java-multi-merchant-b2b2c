package com.niu.mall.event.goods;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.entity.goods.MallGoods;
import com.niu.core.entity.goods.MallGoodsSku;
import lombok.Data;

import java.util.List;

@Data
public class AfterPickGoodsEvent extends Event {

    private Integer supplyId;
    private  Integer goodId;
    private  Integer skuList;
    private  Integer selectNum;
    private  Integer selectUpNum;

}
