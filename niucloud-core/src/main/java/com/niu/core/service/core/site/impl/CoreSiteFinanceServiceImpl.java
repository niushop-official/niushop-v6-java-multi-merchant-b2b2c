package com.niu.core.service.core.site.impl;

import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.site.SiteFinanceLog;
import com.niu.core.mapper.site.SiteFinanceLogMapper;
import com.niu.core.service.core.site.ICoreSiteFinanceService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class CoreSiteFinanceServiceImpl implements ICoreSiteFinanceService {

    @Resource
    SiteFinanceLogMapper siteFinanceLogMapper;

    @Override
    public void addLog(SiteFinanceLog siteFinanceLog) {
        siteFinanceLog.setCreateTime(System.currentTimeMillis()/1000);
        siteFinanceLogMapper.insert(siteFinanceLog);
    }

    @Override
    public void addPlatLog(SiteFinanceLog siteFinanceLog) {
        siteFinanceLog.setSiteId(RequestUtils.defaultSiteId());
        addLog(siteFinanceLog);
    }
}
