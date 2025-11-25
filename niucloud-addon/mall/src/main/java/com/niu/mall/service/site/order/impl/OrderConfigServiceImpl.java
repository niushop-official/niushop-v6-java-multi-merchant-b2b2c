package com.niu.mall.service.site.order.impl;

import com.niu.core.common.utils.RequestUtils;
import com.niu.mall.service.core.order.ICoreSiteOrderConfigService;
import com.niu.mall.service.core.order.param.SiteOrderConfigParam;
import com.niu.mall.service.core.order.vo.SiteOrderConfigVo;
import com.niu.mall.service.site.order.IOrderConfigService;
import com.niu.mall.service.core.order.ICoreOrderConfigService;
import com.niu.mall.service.core.order.param.OrderConfigParam;
import com.niu.mall.service.core.order.vo.OrderConfigVo;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class OrderConfigServiceImpl implements IOrderConfigService {

    @Resource
    private ICoreSiteOrderConfigService coreOrderConfigService;

    @Override
    public void setConfig(SiteOrderConfigParam orderConfigParam) {
        orderConfigParam.setSiteId(RequestUtils.siteId());
       coreOrderConfigService.setConfig(orderConfigParam);
    }

    @Override
    public SiteOrderConfigVo getConfig() {
       return coreOrderConfigService.getConfig(RequestUtils.siteId());
    }
}
