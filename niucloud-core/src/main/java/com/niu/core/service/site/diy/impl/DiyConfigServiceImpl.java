package com.niu.core.service.site.diy.impl;

import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.service.admin.diy.param.DiyConfigParam;
import com.niu.core.service.admin.diy.param.SetBottomConfigParam;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.diy.ICoreDiyConfigService;
import com.niu.core.service.core.diy.vo.BottomConfigVo;
import com.niu.core.service.core.site.ICoreSiteService;
import com.niu.core.service.site.diy.IDiyConfigService;
import jakarta.annotation.Resource;
import org.apache.commons.math3.analysis.function.Add;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiyConfigServiceImpl implements IDiyConfigService {

    @Resource
    ICoreDiyConfigService coreDiyConfigService;

    @Resource
    ICoreSiteService coreSiteService;

    @Override
    public List<BottomConfigVo> getBottomList() {
        List<BottomConfigVo> list = coreDiyConfigService.getSiteBottomList();
        SiteInfoVo siteInfoVo = coreSiteService.getSiteCache(RequestUtils.siteId());

        List<Addon> apps = siteInfoVo.getApps();

        // 提取底部导航列表中的key
        List<String> bottomListKeys = list.stream()
                .map(BottomConfigVo::getKey)
                .collect(Collectors.toList());

        List<String> appKeys = apps.stream().map(Addon::getKey).collect(Collectors.toList());

        // 过滤掉没有底部导航的应用
        List<String> filteredAppKeys = appKeys.stream()
                .filter(bottomListKeys::contains)
                .collect(Collectors.toList());

        // 单应用，排除系统底部导航设置
        if (list.size() > 1 && filteredAppKeys.size() == 1) {
            list = list.stream()
                    .filter(item -> !"app".equals(item.getKey()))
                    .collect(Collectors.toList());
        }

        return list;
    }

    @Override
    public BottomConfigVo getBottomConfig(String key) {
        return coreDiyConfigService.getBottomConfig(RequestUtils.siteId(), key);
    }

    @Override
    public void setBottomConfig(SetBottomConfigParam param) {
        coreDiyConfigService.setBottomConfig(RequestUtils.siteId(), param.getValue(), param.getKey());
    }
}
