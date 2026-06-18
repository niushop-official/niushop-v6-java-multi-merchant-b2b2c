package com.niu.core.service.api.sys.impl;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.event.sys.InitWapEvent;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.api.diy.IDiyService;
import com.niu.core.service.api.login.ILoginService;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.api.member.param.MemberLevelParam;
import com.niu.core.service.api.sys.ISysConfigService;
import com.niu.core.service.api.sys.ISysInitService;
import com.niu.core.service.api.sys.param.SysConfigMapParam;
import com.niu.core.service.api.sys.vo.SysInitVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.site.ICoreSiteService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SysInitServiceImpl implements ISysInitService {

    @Resource
    private ICoreSiteService coreSiteService;

    @Resource
    private IDiyService diyService;

    @Resource
    private ISysConfigService sysConfigService;

    @Resource
    private ILoginService loginService;

    @Resource
    private IMemberLevelService memberLevelService;

    @Override
    public SysInitVo buildInitData(Integer siteId) {
        long start = System.currentTimeMillis();
        SysInitVo vo = new SysInitVo();

        SiteInfoVo siteInfo = coreSiteService.getSiteCache(siteId);
        vo.setSiteInfo(siteInfo);
        vo.setTabbarList(diyService.getBottomList(siteInfo));
        vo.setMapConfig(sysConfigService.map(new SysConfigMapParam()));
        vo.setLoginConfig(loginService.getLoginConfig());
        vo.setThemeList(diyService.getDiyTheme());
        vo.setMemberLevel(memberLevelService.list(new MemberLevelParam()));

        if (ObjectUtil.isEmpty(siteInfo.getSiteId())) {
            InitWapEvent event = new InitWapEvent();
            event.setSiteId(siteId);
            EventAndSubscribeOfPublisher.publishAll(event);
        }

        log.debug("init siteId={} cost={}ms", siteId, System.currentTimeMillis() - start);
        return vo;
    }
}
