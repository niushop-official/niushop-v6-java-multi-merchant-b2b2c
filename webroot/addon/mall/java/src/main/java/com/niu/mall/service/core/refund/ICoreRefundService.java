package com.niu.mall.service.core.refund;

import com.niu.core.entity.pay.PayRefund;
import com.niu.mall.entity.order.MallOrderRefund;
import com.niu.mall.service.api.refund.param.CloseRefundParam;
import com.niu.mall.service.core.refund.param.AddRefundLogParam;
import com.niu.mall.service.core.refund.param.RefundFinishParam;
import com.niu.mall.service.core.refund.vo.OrderRefundMoneyVo;

import java.math.BigDecimal;

public interface ICoreRefundService {

    /**
     * 添加退款记录
     *
     * @param data
     */
    void addLog(AddRefundLogParam data);

    /**
     * 退款完成
     *
     */
    void finish(RefundFinishParam param);

    void refundFinishAfter(MallOrderRefund refund);

    void AfterMallOrderRefundClose(MallOrderRefund refund,CloseRefundParam param);

    /**
     * 转账
     * @param refund
     */
    void toTransfer(MallOrderRefund refund);

    /**
     * 退款转账完成
     *
     */
    void refundTransferSuccess(PayRefund refund);

    /**
     * 退款校验
     * @param orderGoodsIds
     * @param applyRefundMoney
     */
    OrderRefundMoneyVo refundCheck(Integer[] orderGoodsIds, BigDecimal applyRefundMoney);
}
