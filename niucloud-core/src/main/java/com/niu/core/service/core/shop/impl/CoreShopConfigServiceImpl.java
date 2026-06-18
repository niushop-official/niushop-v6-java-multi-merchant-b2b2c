package com.niu.core.service.core.shop.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.enums.sys.ConfigKeyEnum;
import com.niu.core.service.core.shop.ICoreShopConfigService;
import com.niu.core.service.core.shop.param.ShopApplyConfigParam;
import com.niu.core.service.core.shop.param.ShopCashOutConfigParam;
import com.niu.core.service.core.shop.vo.ShopApplyConfigInfoVo;
import com.niu.core.service.core.shop.vo.ShopCashOutConfigVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class CoreShopConfigServiceImpl implements ICoreShopConfigService {

    @Resource
    ICoreConfigService coreConfigService;


    @Override
    public ShopCashOutConfigVo getCashOutConfig() {
        CoreSysConfigVo coreSysConfigVo = coreConfigService.getConfig(RequestUtils.defaultSiteId(), ConfigKeyEnum.SHOP_CASH_OUT.getName());
        ShopCashOutConfigVo vo = new ShopCashOutConfigVo();
        if (ObjectUtil.isNotNull(coreSysConfigVo.getValueJson())) {
            vo = JSONUtil.toBean(coreSysConfigVo.getValueJson(), ShopCashOutConfigVo.class);
        }
        return vo;
    }

    @Override
    public void setCashOutConfig(ShopCashOutConfigParam shopCashOutConfigParam) {
        coreConfigService.setConfig(RequestUtils.defaultSiteId(), ConfigKeyEnum.SHOP_CASH_OUT.getName(), JSONUtil.parseObj(shopCashOutConfigParam));
    }

    @Override
    public ShopApplyConfigInfoVo getShopApplyConfig() {
        CoreSysConfigVo coreSysConfigVo = coreConfigService.getConfig(RequestUtils.defaultSiteId(), ConfigKeyEnum.SHOP_APPLY.getName());
        ShopApplyConfigInfoVo vo = new ShopApplyConfigInfoVo();
        vo.setApplyTitle("");
        vo.setApplyBanner("addon/mall/member/merchant_bg.png");
        if (ObjectUtil.isNotNull(coreSysConfigVo.getValueJson())) {
            vo = JSONUtil.toBean(coreSysConfigVo.getValueJson(), ShopApplyConfigInfoVo.class);
        }
        return vo;
    }

    @Override
    public void setShopApplyConfig(ShopApplyConfigParam param) {
        coreConfigService.setConfig(RequestUtils.defaultSiteId(), ConfigKeyEnum.SHOP_APPLY.getName(), JSONUtil.parseObj(param));
    }
}
