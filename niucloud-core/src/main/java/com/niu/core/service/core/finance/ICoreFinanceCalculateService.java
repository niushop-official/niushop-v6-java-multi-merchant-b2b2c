package com.niu.core.service.core.finance;


import com.niu.core.service.core.finance.param.OrderFinanceParam;
import com.niu.core.service.core.finance.param.OrderPayParam;
import com.niu.core.service.core.finance.param.OrderRefundParam;
import com.niu.core.service.core.finance.vo.CoreFinanceCalculateVo;
import com.niu.core.service.core.finance.vo.OrderPayCalcVo;
import com.niu.core.service.core.finance.vo.OrderRefundCalcVo;

/**
 * 订单财务计算
 */
public interface ICoreFinanceCalculateService {

    /**
     * 支付计算
     * @param orderPayCalcParam
     * @return
     */
    CoreFinanceCalculateVo orderPayCalc(OrderFinanceParam orderPayCalcParam);

    /**
     * 退款计算
     * @param orderRefundParam
     * @return
     */
    OrderRefundCalcVo orderRefundCalc(OrderRefundParam orderRefundParam);


}
