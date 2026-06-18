package com.niu.core.service.core.shop.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.enums.sys.ConfigKeyEnum;
import com.niu.core.service.core.shop.ICoreShopMemberConfigService;
import com.niu.core.service.core.shop.param.ShopMemberConfigParam;
import com.niu.core.service.core.shop.vo.ShopMemberConfigVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class CoreShopMemberConfigServiceImpl implements ICoreShopMemberConfigService {

    @Resource
    ICoreConfigService coreConfigService;

    @Override
    public ShopMemberConfigVo getMemberConfig(Integer siteId) {
        CoreSysConfigVo coreSysConfigVo = coreConfigService.getConfig(siteId, ConfigKeyEnum.SHOP_MEMBER.getName());
        ShopMemberConfigVo vo = new ShopMemberConfigVo();
        if (ObjectUtil.isNotNull(coreSysConfigVo.getValueJson()) && !coreSysConfigVo.getValueJson().isEmpty()) {
            JSONObject info = coreSysConfigVo.getValueJson();
            vo.setOldMemberLength(info.getInt("old_member_length", 90));
        }
        return vo;
    }

    @Override
    public void setMemberConfig(Integer siteId, ShopMemberConfigParam param) {
        JSONObject config = new JSONObject();
        config.set("old_member_length", ObjectUtil.defaultIfNull(param.getOldMemberLength(), 90));
        coreConfigService.setConfig(siteId, ConfigKeyEnum.SHOP_MEMBER.getName(), config);
    }
}
