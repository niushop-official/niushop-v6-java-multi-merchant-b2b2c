package com.niu.core.service.admin.shop.impl;

import com.niu.core.service.admin.shop.IShopConfigService;
import com.niu.core.service.core.shop.ICoreShopConfigService;
import com.niu.core.service.core.shop.param.ShopApplyConfigParam;
import com.niu.core.service.core.shop.param.ShopCashOutConfigParam;
import com.niu.core.service.core.shop.vo.ShopApplyConfigInfoVo;
import com.niu.core.service.core.shop.vo.ShopCashOutConfigVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ShopConfigServiceImpl implements IShopConfigService {

    @Resource
    ICoreShopConfigService coreShopConfigService;

    @Override
    public ShopCashOutConfigVo getCashOutConfig() {
        return coreShopConfigService.getCashOutConfig();
    }

    @Override
    public void setCashOutConfig(ShopCashOutConfigParam shopCashOutConfigParam) {
        coreShopConfigService.setCashOutConfig(shopCashOutConfigParam);
    }

    @Override
    public ShopApplyConfigInfoVo getShopApplyConfig() {
        return coreShopConfigService.getShopApplyConfig();
    }

    @Override
    public void setShopApplyConfig(ShopApplyConfigParam param) {
        coreShopConfigService.setShopApplyConfig(param);
    }
}
