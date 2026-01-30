package com.niu.core.service.admin.sys.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.mapper.sys.SysAreaMapper;
import com.niu.core.service.admin.sys.ISysFabService;
import com.niu.core.service.admin.sys.param.SysFabParam;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.ICoreSysFabService;
import com.niu.core.service.core.sys.vo.FabNavsVo;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 悬浮按钮配置服务层
 */
@Service
@RequiredArgsConstructor
public class SysFabServiceImpl implements ISysFabService {

    @Resource
    private ICoreSysFabService coreSysFabService;
    @Resource
    private ICoreConfigService coreConfigService;
    @Override
    public FabNavsVo getFabNavs() {
        return coreSysFabService.getFabNavs();
    }

    @Override
    public void getFabConfig(SysFabParam param) {
        JSONObject config = new JSONObject();
        config.set("is_show", param.getIsShow());
        config.set("style", JSONUtil.parseObj(param.getStyle()));
        config.set("navs", param.getNavs());
        coreConfigService.setConfig(RequestUtils.defaultSiteId(), "FAB", config);
    }
}
