package com.niu.core.service.admin.site.impl;

import cn.hutool.core.util.StrUtil;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.event.site.SiteQrcodeTypeEventDefiner;
import com.niu.core.service.admin.site.ISiteQrcodeService;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.diy.ICoreDiyConfigService;
import com.niu.core.service.core.site.ICoreSiteService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 站点店铺二维码服务
 * 对应 PHP: app\service\admin\site\SiteQrcodeService
 */
@Service
public class SiteQrcodeServiceImpl implements ISiteQrcodeService {

    @Resource
    private ICoreDiyConfigService coreDiyConfigService;

    @Resource
    private ICoreSiteService coreSiteService;

    @Override
    public List<Map<String, String>> getList() {
        SiteQrcodeTypeEventDefiner.SiteQrcodeTypeEvent event = new SiteQrcodeTypeEventDefiner.SiteQrcodeTypeEvent();
        event.setSiteId(RequestUtils.siteId());
        // 对齐 PHP event('SiteQrcodeType')：先聚合全部类型，再按站点已安装应用过滤
        event.setAuthority(false);
        List<EventResult> results = EventAndSubscribeOfPublisher.publishAndCallbackBySite(event);

        List<Map<String, String>> typeList = new ArrayList<>();
        for (EventResult result : results) {
            if (result instanceof SiteQrcodeTypeEventDefiner.SiteQrcodeTypeResult qrcodeResult
                    && qrcodeResult.getTypes() != null) {
                typeList.addAll(qrcodeResult.getTypes());
            }
        }

        List<String> installedAddons = coreSiteService.getAddonKeysBySiteId(RequestUtils.siteId());
        List<Map<String, String>> list = new ArrayList<>();
        for (Map<String, String> item : typeList) {
            String addon = item.getOrDefault("addon", "");
            if (StrUtil.isBlank(addon)) {
                list.add(item);
            } else if (installedAddons.contains(addon)) {
                list.add(item);
            }
        }
        return list;
    }

    @Override
    public Map<String, Object> getSiteQrcodeConfig(String type) {
        return coreDiyConfigService.getSiteQrcodeConfig(RequestUtils.siteId(), type);
    }

    @Override
    public void setSiteQrcodeConfig(Map<String, Object> value, String type) {
        Map<String, Object> data = value != null ? value : new LinkedHashMap<>();
        coreDiyConfigService.setSiteQrcodeConfig(RequestUtils.siteId(), data, type);
    }
}
