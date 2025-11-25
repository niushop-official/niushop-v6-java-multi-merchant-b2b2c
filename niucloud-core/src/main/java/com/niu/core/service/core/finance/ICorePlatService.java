package com.niu.core.service.core.finance;


import com.niu.core.service.core.finance.param.OrderFinanceParam;
import com.niu.core.service.core.finance.param.OrderPlatParam;
import com.niu.core.service.core.finance.param.OrderRefundParam;

/**
 * 订单财务计算
 */
public interface ICorePlatService {

    /**
     * 平台收入订单支付
     * @param orderPlatParam
     */
    void platIncomeOrderPay(OrderPlatParam orderPlatParam);

    /**
     * 平台收入订单退款
     * @param orderRefundParam
     */
    void platIncomeOrderRefund(OrderRefundParam orderRefundParam);

    /**
     * 平台收入订单优惠券
     * @param orderPlatParam
     */
    void platIncomePlatCoupon(OrderPlatParam orderPlatParam);

    /**
     * 平台收入订单退款优惠券
     * @param orderRefundParam
     */
    void platIncomePlatRefundCoupon(OrderRefundParam orderRefundParam);

    /**
     * 平台收入订单
     * @param orderPlatParam
     */
    void platIncomeMallService(OrderPlatParam orderPlatParam);

    /**
     * 平台收入供应商服务
     * @param orderPlatParam
     */
    void platIncomeSupplyService(OrderPlatParam orderPlatParam);

    /**
     * 平台支出订单
     * @param orderPlatParam
     */
    void platOutlaySeparate(OrderPlatParam orderPlatParam);

    /**
     * 平台支出订单退款
     * @param orderRefundParam
     */
    void platOutlayRefundSeparate(OrderRefundParam orderRefundParam);

    /**
     * 平台支出佣金
     * @param orderPlatParam
     */
    void platOutlayCommission(OrderPlatParam orderPlatParam);

    /**
     * 平台支出佣金退款
     * @param orderRefundParam
     */
    void platOutlayRefundCommission(OrderRefundParam orderRefundParam);

    /**
     * 平台支出供应商服务
     * @param orderPlatParam
     */
    void platOutlaySupplyMoney(OrderPlatParam orderPlatParam);

    /**
     * 平台支出供应商服务退款
     * @param orderRefundParam
     */
    void platOutlayRefundSupplyMoney(OrderRefundParam orderRefundParam);

    /**
     * 平台收入（未结算订单退款）
     * @param orderRefundParam
     */
    void platIncomeOrderRefundUnsettleMoney(OrderRefundParam orderRefundParam);


}
