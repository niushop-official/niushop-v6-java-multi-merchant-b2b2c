package com.niu.core.service.admin.market.impl;

import com.niu.core.common.utils.RequestUtils;
import com.niu.core.enums.market.MarketTypeAdminEnum;
import com.niu.core.enums.sys.AppTypeEnum;
import com.niu.core.service.admin.market.IMarketService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MarketServiceImpl implements IMarketService {
    @Override
    public Map getMarketingType() {
        if (RequestUtils.appType().equals(AppTypeEnum.ADMIN.getName()))
            return MarketTypeAdminEnum.getMap();
        if (RequestUtils.appType().equals(AppTypeEnum.SITE.getName()))
            return Map.of();
        return Map.of();
    }
}
