package com.niu.core.service.admin.recharge;


import com.niu.core.service.core.recharge.config.param.RechargeSetConfigParam;
import com.niu.core.service.core.recharge.config.vo.RechargeConfigVo;

public interface IRechargeConfigService {

    /**
     * 设置充值配置
     * @param param
     */
    void setRechargeConfig(RechargeSetConfigParam param);

    /**
     * 获取充值配置
     */
    RechargeConfigVo getRechargeConfig();
}
