package com.niu.core.service.admin.recharge.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.admin.recharge.IRechargeConfigService;
import com.niu.core.service.core.recharge.config.ICoreRechargeConfigService;
import com.niu.core.service.core.recharge.config.param.RechargeSetConfigParam;
import com.niu.core.service.core.recharge.config.vo.RechargeConfigVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;



@Service
public class RechargeConfigServiceImpl implements IRechargeConfigService {

    @Resource
    private ICoreRechargeConfigService coreRechargeConfigService;

    @Override
    public void setRechargeConfig(RechargeSetConfigParam param) {
        coreRechargeConfigService.setRechargeConfig(param);
    }

    @Override
    public RechargeConfigVo getRechargeConfig() {
        return coreRechargeConfigService.getRechargeConfig();
    }
}
