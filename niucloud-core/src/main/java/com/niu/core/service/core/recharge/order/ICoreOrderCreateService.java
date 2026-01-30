package com.niu.core.service.core.recharge.order;

import com.niu.core.service.core.recharge.order.param.RechargeOrderCreateParam;
import com.niu.core.service.core.recharge.order.vo.RechargeOrderCreateVo;

/**
 * 创建订单
 */
public interface ICoreOrderCreateService {

    /**
     * 创建充值订单
     *
     * @param param
     * @return
     */
    RechargeOrderCreateVo create(RechargeOrderCreateParam param);

}
