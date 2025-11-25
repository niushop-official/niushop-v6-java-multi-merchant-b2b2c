package com.niu.mall.service.core.order.param;

import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.service.core.order.vo.MallOrderGoodsDeliveryVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 订单发货参数
 */
@Data
public class OrderDeliveryDataParam implements Serializable {
    private static final long serialVersionUID = 1L;

    MallOrder orderData;

    List<MallOrderGoodsDeliveryVo> orderGoodsData;

    OrderDeliveryParam param;

}
