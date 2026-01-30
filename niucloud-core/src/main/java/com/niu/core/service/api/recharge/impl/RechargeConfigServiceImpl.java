package com.niu.core.service.api.recharge.impl;

import com.niu.core.service.api.recharge.IRechargeConfigService;
import com.niu.core.service.core.recharge.config.ICoreRechargeConfigService;
import com.niu.core.service.core.recharge.config.vo.RechargeConfigVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;



@Service
public class RechargeConfigServiceImpl implements IRechargeConfigService {

    @Resource
    private ICoreRechargeConfigService coreRechargeConfigService;

    @Override
    public RechargeConfigVo getRechargeConfig() {
        return coreRechargeConfigService.getRechargeConfig();
    }
}
