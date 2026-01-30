package com.niu.core.service.api.recharge;


import com.niu.core.service.core.recharge.config.param.RechargeSetConfigParam;
import com.niu.core.service.core.recharge.config.vo.RechargeConfigVo;

public interface IRechargeConfigService {

    /**
     * 获取充值配置
     */
    RechargeConfigVo getRechargeConfig();
}
