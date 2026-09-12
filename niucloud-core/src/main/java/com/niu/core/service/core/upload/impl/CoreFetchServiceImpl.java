package com.niu.core.service.core.upload.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.upload.IUploadProvider;
import com.niu.core.common.component.upload.factory.UploadProviderFactory;
import com.niu.core.common.component.upload.model.FetchModel;
import com.niu.core.common.utils.date.DateFormatUtils;
import com.niu.core.enums.sys.StorageEnum;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.upload.ICoreFetchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoreFetchServiceImpl implements ICoreFetchService {

    @Autowired
    private ICoreConfigService coreConfigService;

    @Override
    public String image(String url, Integer siteId) {
        // 获取站点的上传配置
        ICoreConfigService coreConfigService = SpringContext.bean(ICoreConfigService.class);
        JSONObject rootJSONObject = coreConfigService.getConfigValue(siteId, "STORAGE");
        String defaultValue = rootJSONObject.getStr("default", StorageEnum.LOCAL.getCode());
        JSONObject configObject = rootJSONObject.getJSONObject(defaultValue);
        // 根据配置初始化
        IUploadProvider uploadProvider = UploadProviderFactory.createAndInit(defaultValue, configObject);

        // 获取图片扩展名，取URL最后一个"."后面的部分
        String[] urlParts = url.split("\\.");
        String ext = urlParts.length > 1 ? urlParts[urlParts.length - 1].toLowerCase() : "jpg";
        // 校验扩展名是否为有效的图片格式，无效则默认使用jpg
        if (!ext.matches("^(jpg|jpeg|png|gif|bmp|webp)$")) {
            ext = "jpg";
        }

        FetchModel fetchModel = new FetchModel();
        fetchModel.setUrl(url);
        fetchModel.setUploadFilePath("attachment/image/" + siteId + "/" + DateFormatUtils.getUploadFormat() + "/");
        fetchModel.setUploadFileName(SecureUtil.md5(url) + "." + ext);

        return uploadProvider.getAccessUrl(uploadProvider.fetch(fetchModel));
    }
}
