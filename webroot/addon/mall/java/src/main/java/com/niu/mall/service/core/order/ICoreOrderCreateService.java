package com.niu.mall.service.core.order;

import com.niu.mall.service.core.order.param.OrderCalculateParam;
import com.niu.mall.service.core.order.param.OrderCreateParam;
import com.niu.mall.service.core.order.vo.MallOrderCreateDataVo;
import com.niu.mall.service.core.order.vo.OrderCouponListVo;
import com.niu.mall.service.core.order.vo.OrderCreateDataVo;
import com.niu.mall.service.core.order.vo.OrderCreateResultVo;

import java.util.List;

public interface ICoreOrderCreateService {

    /**
     * 计算
     *
     * @param param
     */
    OrderCreateDataVo calculate(OrderCalculateParam param, MallOrderCreateDataVo mallOrderCreateDataVo);

    /**
     * 创建
     *
     * @return
     */
    OrderCreateResultVo create(OrderCreateDataVo vo);

    /**
     * 查询订单可用优惠券
     * @param orderKey
     */
    List<OrderCouponListVo> getCoupon(String orderKey);
}
