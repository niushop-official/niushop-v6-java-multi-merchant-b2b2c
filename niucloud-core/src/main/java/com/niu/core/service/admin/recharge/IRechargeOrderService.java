package com.niu.core.service.admin.recharge;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.recharge.param.RechargeOrderSearchParam;
import com.niu.core.service.admin.recharge.vo.RechargeOrderInfoVo;
import com.niu.core.service.admin.recharge.vo.RechargeOrderListVo;
import com.niu.core.service.admin.recharge.vo.RechargeOrderStatVo;

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

    /**
     * 充值订单统计
     *
     * @param searchParam
     * @return
     */
    RechargeOrderStatVo stat(RechargeOrderSearchParam searchParam);
}
