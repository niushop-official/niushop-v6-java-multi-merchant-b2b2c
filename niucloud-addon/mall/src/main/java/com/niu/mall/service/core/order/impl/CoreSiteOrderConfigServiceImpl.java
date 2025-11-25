package com.niu.mall.service.core.order.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import com.niu.mall.service.core.order.ICoreSiteOrderConfigService;
import com.niu.mall.service.core.order.param.SiteOrderConfigParam;
import com.niu.mall.service.core.order.vo.SiteOrderConfigVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class CoreSiteOrderConfigServiceImpl implements ICoreSiteOrderConfigService {

    @Resource
    ICoreConfigService coreConfigService;


    @Override
    public void setConfig(SiteOrderConfigParam orderConfigParam) {
        coreConfigService.setConfig(orderConfigParam.getSiteId(), "MALL_SITE_ORDER_CONFIG", JSONUtil.parseObj(orderConfigParam));

    }

    @Override
    public SiteOrderConfigVo getConfig(Integer siteId) {
        CoreSysConfigVo coreSysConfigVo = coreConfigService.getConfig(siteId, "MALL_SITE_ORDER_CONFIG");
        SiteOrderConfigVo siteOrderConfigVo = new SiteOrderConfigVo();
        if(ObjectUtil.isEmpty(coreSysConfigVo)) return siteOrderConfigVo;
        if (ObjectUtil.isNotNull(coreSysConfigVo.getValueJson())) {
            siteOrderConfigVo = JSONUtil.toBean(coreSysConfigVo.getValueJson(), SiteOrderConfigVo.class);
        }

        return siteOrderConfigVo;
    }
}
