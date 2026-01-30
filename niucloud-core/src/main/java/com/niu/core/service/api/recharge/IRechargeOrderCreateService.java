package com.niu.core.service.api.recharge;

import com.niu.core.service.core.recharge.order.param.RechargeOrderCreateParam;
import com.niu.core.service.core.recharge.order.vo.RechargeOrderCreateVo;

public interface IRechargeOrderCreateService {

    /**
     * 充值订单创建
     */
    RechargeOrderCreateVo create(RechargeOrderCreateParam searchParam);
}
