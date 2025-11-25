package com.niu.core.service.api.channel.impl;

import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.api.channel.IH5Service;
import com.niu.core.service.core.channel.ICoreH5Service;
import com.niu.core.service.core.channel.vo.H5ConfigVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class H5ServiceImpl implements IH5Service {

    @Resource
    private ICoreH5Service coreH5Service;
    @Override
    public H5ConfigVo getH5() {
        return coreH5Service.getH5(RequestUtils.defaultSiteId());
    }
}
