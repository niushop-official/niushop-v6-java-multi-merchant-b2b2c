package com.niu.mall.service.core.refund;

import com.niu.mall.service.api.refund.param.ApplyRefundParam;
import com.niu.mall.service.api.refund.param.CloseRefundParam;
import com.niu.mall.service.core.refund.param.ActiveRefundParam;
import com.niu.mall.service.site.refund.param.AuditApplyParam;
import com.niu.mall.service.site.refund.param.AuditRefundGoodsParam;

public interface ICoreRefundActionService {

    /**
     * 主动退款
     * @param param
     */
    void shopActiveRefund(ActiveRefundParam param);

    /**
     * 订单维权审核
     *
     * @param param
     */
    void auditApply(AuditApplyParam param);

    /**
     * 订单退货审核
     *
     * @param param
     */
    void auditRefundGoods(AuditRefundGoodsParam param);

    /**
     * 取消退款
     *
     * @param param
     */
    void close(CloseRefundParam param);

    /**
     * 退款申请
     *
     * @param param
     */
    void apply(ApplyRefundParam param);
}
