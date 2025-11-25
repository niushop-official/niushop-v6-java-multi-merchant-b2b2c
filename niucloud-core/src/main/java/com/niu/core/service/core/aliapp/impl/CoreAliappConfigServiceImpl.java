package com.niu.core.service.core.aliapp.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.aliapp.ICoreAliappConfigService;
import com.niu.core.service.core.aliapp.param.AliappConfigParam;
import com.niu.core.service.core.aliapp.vo.AliappConfigVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class CoreAliappConfigServiceImpl  implements ICoreAliappConfigService {

    @Resource
    ICoreConfigService coreConfigService;

    @Override
    public AliappConfigVo getAliappConfig() {
        CoreSysConfigVo coreSysConfigVo = coreConfigService.getConfig(RequestUtils.defaultSiteId(), "aliapp");
        AliappConfigVo vo = new AliappConfigVo();
        if(ObjectUtil.isNotNull(coreSysConfigVo.getValueJson()))
        {
            vo = JSONUtil.toBean(coreSysConfigVo.getValueJson(), AliappConfigVo.class);
        }
        return vo;
    }

    @Override
    public void setAliappConfig(AliappConfigParam aliappConfigParam) {
        JSONObject json = JSONUtil.parseObj(aliappConfigParam);
        coreConfigService.setConfig(RequestUtils.defaultSiteId(), "aliapp", json);

    }
}
