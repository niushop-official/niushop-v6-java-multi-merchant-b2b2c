package com.niu.core.service.core.recharge.config.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.recharge.config.ICoreRechargeConfigService;
import com.niu.core.service.core.recharge.config.param.RechargeSetConfigParam;
import com.niu.core.service.core.recharge.config.vo.RechargeConfigVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


@Service
public class CoreRechargeConfigServiceImpl implements ICoreRechargeConfigService {

    @Resource
    private ICoreConfigService coreConfigService;

    @Override
    public void setRechargeConfig(RechargeSetConfigParam param) {
        coreConfigService.setConfig(RequestUtils.siteId(), "RECHARGE_CONFIG", JSONUtil.parse(param));
    }

    @Override
    public RechargeConfigVo getRechargeConfig() {
        JSONObject config = coreConfigService.getConfigValue(RequestUtils.defaultSiteId(), "RECHARGE_CONFIG");
        return JSONUtil.toBean(config, RechargeConfigVo.class);
    }
}
