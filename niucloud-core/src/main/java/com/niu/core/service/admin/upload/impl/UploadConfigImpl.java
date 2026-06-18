package com.niu.core.service.admin.upload.impl;

import com.niu.core.service.admin.upload.IUploadConfigService;
import com.niu.core.service.core.upload.ICoreUploadConfigService;
import com.niu.core.service.core.upload.param.UploadConfigParam;
import com.niu.core.service.core.upload.vo.UploadConfigVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class UploadConfigImpl implements IUploadConfigService {

    @Resource
    private ICoreUploadConfigService coreUploadConfigService;

    @Override
    public UploadConfigVo getUploadConfig() {
        return coreUploadConfigService.getUploadConfig();
    }

    @Override
    public void setUploadConfig(UploadConfigParam param) {
        coreUploadConfigService.setUploadConfig(param);
    }
}
