package com.niu.core.service.core.finance;


import com.niu.core.service.core.finance.param.OrderFinanceParam;
import com.niu.core.service.core.finance.param.OrderPayParam;
import com.niu.core.service.core.finance.param.OrderRefundParam;
import com.niu.core.service.core.finance.vo.OrderPayCalcVo;
import com.niu.core.service.core.finance.vo.OrderRefundCalcVo;

/**
 * 订单财务计算
 */
public interface ICoreFinanceService {

    /**
     * 订单支付
     * @param orderParam
     * @return
     */
    void orderPay(OrderFinanceParam orderParam);

    /**
     * 订单完成
     * @param orderFinanceParam
     */
    void orderFinish(OrderFinanceParam orderFinanceParam);

    /**
     * 订单退款
     * @param orderRefundParam
     * @return
     */
    void orderRefund(OrderRefundParam orderRefundParam);


}
