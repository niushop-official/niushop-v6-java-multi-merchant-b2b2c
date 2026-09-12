package com.niu.core.service.admin.vip_card.config;

import com.niu.core.service.core.vip_card.config.param.VipCardConfigParam;
import com.niu.core.service.core.vip_card.config.vo.VipCardConfigVo;

public interface IAdminVipCardConfigService {

    /**
     * 设置付费会员卡配置
     */
    void setVipCardConfig(VipCardConfigParam param);

    /**
     * 获取付费会员卡配置
     */
    VipCardConfigVo getVipCardConfig();
}
