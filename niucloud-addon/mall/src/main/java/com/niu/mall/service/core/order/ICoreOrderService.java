package com.niu.mall.service.core.order;

import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.service.core.order.param.DeliverySearchParam;
import com.niu.mall.service.core.order.param.OrderCloseParam;
import com.niu.mall.service.core.order.param.OrderPayParam;
import com.niu.mall.service.core.order.vo.DeliveryTraceVo;

public interface ICoreOrderService {

    /**
     * 获取订单信息
     *
     * @param orderId
     * @return
     */
    MallOrder orderInfo(Integer orderId);


    /**
     * 查询物流信息
     *
     * @param param
     */
    DeliveryTraceVo deliverySearch(DeliverySearchParam param);
}
