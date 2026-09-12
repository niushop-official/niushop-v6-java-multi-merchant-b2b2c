package com.niu.core.service.core.vip_card.config.impl;

import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.vip_card.config.ICoreVipCardConfigService;
import com.niu.core.service.core.vip_card.config.param.VipCardConfigParam;
import com.niu.core.service.core.vip_card.config.vo.VipCardConfigVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoreVipCardConfigServiceImpl implements ICoreVipCardConfigService {

    private static final String CONFIG_KEY = "VIP_CARD_CONFIG";

    @Autowired
    private ICoreConfigService coreConfigService;

    @Override
    public void setVipCardConfig(VipCardConfigParam param) {
        Integer siteId = RequestUtils.defaultSiteId(); // 平台端使用默认站点ID（通常为0）
        JSONObject config = new JSONObject();
        config.set("is_use", param.getIsUse());
        config.set("close_length", param.getCloseLength());
        coreConfigService.setConfig(siteId, CONFIG_KEY, config);
    }

    @Override
    public VipCardConfigVo getVipCardConfig() {
        Integer siteId = RequestUtils.defaultSiteId();
        JSONObject config = coreConfigService.getConfigValue(siteId, CONFIG_KEY);
        VipCardConfigVo vo = new VipCardConfigVo();
        if (config != null && !config.isEmpty()) {
            vo.setIsUse(config.getInt("is_use", 1));
            vo.setCloseLength(config.getInt("close_length", 10));
        } else {
            // 默认值
            vo.setIsUse(1);
            vo.setCloseLength(10);
        }
        return vo;
    }
}