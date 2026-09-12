package com.niu.core.service.admin.vip_card.config.impl;

import com.niu.core.service.admin.vip_card.config.IAdminVipCardConfigService;
import com.niu.core.service.core.vip_card.config.ICoreVipCardConfigService;
import com.niu.core.service.core.vip_card.config.param.VipCardConfigParam;
import com.niu.core.service.core.vip_card.config.vo.VipCardConfigVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminVipCardConfigServiceImpl implements IAdminVipCardConfigService {

    @Autowired
    private ICoreVipCardConfigService coreVipCardConfigService;

    @Override
    public void setVipCardConfig(VipCardConfigParam param) {
        coreVipCardConfigService.setVipCardConfig(param);
    }

    @Override
    public VipCardConfigVo getVipCardConfig() {
        return coreVipCardConfigService.getVipCardConfig();
    }
}