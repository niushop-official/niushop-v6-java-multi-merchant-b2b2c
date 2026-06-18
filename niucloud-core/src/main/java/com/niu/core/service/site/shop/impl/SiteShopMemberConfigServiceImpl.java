package com.niu.core.service.site.shop.impl;

import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.shop.ICoreShopMemberConfigService;
import com.niu.core.service.core.shop.param.ShopMemberConfigParam;
import com.niu.core.service.core.shop.vo.ShopMemberConfigVo;
import com.niu.core.service.site.shop.ISiteShopMemberConfigService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class SiteShopMemberConfigServiceImpl implements ISiteShopMemberConfigService {

    @Resource
    ICoreShopMemberConfigService coreShopMemberConfigService;

    @Override
    public ShopMemberConfigVo getMemberConfig() {
        return coreShopMemberConfigService.getMemberConfig(RequestUtils.siteId());
    }

    @Override
    public void setMemberConfig(ShopMemberConfigParam param) {
        coreShopMemberConfigService.setMemberConfig(RequestUtils.siteId(), param);
    }
}
