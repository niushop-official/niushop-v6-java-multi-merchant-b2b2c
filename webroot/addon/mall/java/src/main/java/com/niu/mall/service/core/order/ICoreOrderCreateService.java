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
    OrderCreateDataVo calculate(OrderCalculateParam param, MallOrderCreateDataVo mallOrderCreateDataVo); // 入参 param 驱动优惠/运费变更；返回单站点 OrderCreateDataVo 并写 Redis 缓存

    /**
     * 创建
     *
     * @return
     */
    OrderCreateResultVo create(OrderCreateDataVo vo); // 入参 vo 为 calculate 快照；返回 OrderCreateResultVo（含 orderId/orderNo 等落库结果）

    /**
     * 提交订单前按当前时间重算满减/满赠
     */
    void recheckFullActiveBeforeCreate(OrderCreateDataVo vo); // void 原地修改 vo.basic 满减/满赠字段，create 前校验活动仍有效

    /**
     * 查询订单可用优惠券
     * @param orderKey
     */
    List<OrderCouponListVo> getCoupon(String orderKey); // 入参 orderKey 定位缓存订单；返回 List<OrderCouponListVo> 含 isNormal/error 可用性标记
}
