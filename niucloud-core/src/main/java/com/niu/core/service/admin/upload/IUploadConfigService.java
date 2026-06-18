package com.niu.core.service.admin.upload;

import com.niu.core.service.core.upload.param.UploadConfigParam;
import com.niu.core.service.core.upload.vo.UploadConfigVo;

public interface IUploadConfigService {

    UploadConfigVo getUploadConfig();
    
    void setUploadConfig(UploadConfigParam param);

}
