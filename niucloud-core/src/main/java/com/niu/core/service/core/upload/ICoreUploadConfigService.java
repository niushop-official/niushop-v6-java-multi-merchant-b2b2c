package com.niu.core.service.core.upload;

import com.niu.core.service.core.upload.param.UploadConfigParam;
import com.niu.core.service.core.upload.vo.UploadConfigVo;

public interface ICoreUploadConfigService {

    UploadConfigVo getUploadConfig();

    void setUploadConfig(UploadConfigParam param);
}
