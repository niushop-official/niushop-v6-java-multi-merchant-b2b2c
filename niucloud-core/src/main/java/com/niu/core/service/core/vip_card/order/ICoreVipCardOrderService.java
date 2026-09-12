package com.niu.core.service.core.vip_card.order;


import com.niu.core.entity.vip_card.MemberVipCardOrder;

public interface ICoreVipCardOrderService {

    /**
     * 根据订单ID获取订单信息
     *
     * @param orderId 订单ID
     * @return 订单实体，不存在返回null
     */
    MemberVipCardOrder orderInfo(Integer orderId);

    /**
     * 根据支付流水号获取订单信息
     *
     * @param outTradeNo 支付流水号
     * @return 订单实体，不存在返回null
     */
    MemberVipCardOrder orderInfoByOutTradeNo(String outTradeNo);
}