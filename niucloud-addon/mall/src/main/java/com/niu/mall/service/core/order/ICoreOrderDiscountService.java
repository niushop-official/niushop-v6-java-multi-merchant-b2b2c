package com.niu.mall.service.core.order;

import com.niu.mall.entity.order.MallOrderDiscount;
import com.niu.mall.service.core.order.vo.OrderDiscountDetailsVo;

import java.math.BigDecimal;
import java.util.List;

public interface ICoreOrderDiscountService {

    /**
     * 添加订单优惠
     *
     * @param list
     */
    void inserAll(List<MallOrderDiscount> list);

    /**
     * 获取单个订单的优惠记录
     */
    List<MallOrderDiscount> getDiscountRecords(Integer orderId, List<String> type, List<String> discountType, Integer siteId, Integer status);

    /**
     * 批量获取多个订单的优惠记录
     */
    List<MallOrderDiscount> batchGetDiscountRecords(List<Integer> orderIds, List<String> type, List<String> discountType, Integer siteId, Integer status);

    /**
     * 获取订单优惠明细（店铺优惠+平台优惠，含总额和列表）
     *
     * @param orderId          订单ID
     * @param discountMoney    店铺优惠金额
     * @param mallCouponMoney  平台优惠券补贴
     */
    OrderDiscountDetailsVo getOrderDiscountDetails(Integer orderId, BigDecimal discountMoney, BigDecimal mallCouponMoney);

    /**
     * 获取订单优惠明细（含积分抵扣兜底）
     */
    OrderDiscountDetailsVo getOrderDiscountDetails(Integer orderId, BigDecimal discountMoney, BigDecimal mallCouponMoney,
                                                   BigDecimal pointDeductMoney, Integer usePoint);
}
