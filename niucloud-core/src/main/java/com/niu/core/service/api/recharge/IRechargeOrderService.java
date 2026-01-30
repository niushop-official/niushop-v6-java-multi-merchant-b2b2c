package com.niu.core.service.api.recharge;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.api.recharge.param.RechargeOrderSearchParam;
import com.niu.core.service.api.recharge.vo.RechargeOrderInfoVo;
import com.niu.core.service.api.recharge.vo.RechargeOrderListVo;

public interface IRechargeOrderService {

    /**
     * 订单列表
     *
     * @param pageParam
     * @param searchParam
     * @return
     */
    PageResult<RechargeOrderListVo> list(PageParam pageParam, RechargeOrderSearchParam searchParam);

    /**
     * 充值订单详情
     *
     * @param orderId
     * @return
     */
    RechargeOrderInfoVo detail(Integer orderId);
}
