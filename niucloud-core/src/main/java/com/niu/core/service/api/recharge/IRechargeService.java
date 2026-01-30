package com.niu.core.service.api.recharge;

import com.niu.core.service.api.recharge.vo.RechargeListVo;

import java.util.List;

public interface IRechargeService {

    /**
     * 获取充值套餐列表
     * @return
     */
    List<RechargeListVo> list();
}
