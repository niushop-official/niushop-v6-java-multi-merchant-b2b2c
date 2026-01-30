package com.niu.core.service.admin.recharge;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.recharge.param.RechargeRefundSearchParam;
import com.niu.core.service.admin.recharge.vo.RechargeRefundInfoVo;
import com.niu.core.service.admin.recharge.vo.RechargeRefundListVo;
import com.niu.core.service.admin.recharge.vo.RechargeRefundStatVo;

public interface IRechargeRefundService {

    /**
     * 退款列表
     *
     * @param pageParam
     * @param searchParam
     * @return
     */
    PageResult<RechargeRefundListVo> list(PageParam pageParam, RechargeRefundSearchParam searchParam);

    /**
     * 发起退款
     */
    void refund(Integer orderId);

    /**
     * 充值退款详情
     *
     * @param refundId
     * @return
     */
    RechargeRefundInfoVo detail(Integer refundId);

    /**
     * 充值退款统计
     *
     * @param searchParam
     * @return
     */
    RechargeRefundStatVo stat(RechargeRefundSearchParam searchParam);
}
