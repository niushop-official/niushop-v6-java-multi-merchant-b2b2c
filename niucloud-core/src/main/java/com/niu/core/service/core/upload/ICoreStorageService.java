package com.niu.core.service.core.upload;

import cn.hutool.json.JSONObject;
import com.niu.core.service.core.upload.vo.CoreStorAgeConfigVo;

import java.util.List;

/**
 * 上传服务层
 */
public interface ICoreStorageService {

    /**
     * 获取站点的云存储列表
     * @return
     */
    List<CoreStorAgeConfigVo> getStorageList();

    /**
     * 获取站点的云存储配置
     * @return
     */
    JSONObject getStorageConfig();

    /**
     * 获取当前启用的存储方式以及配置
     * @return
     */
    JSONObject getDefaultStorage();

    /**
     * 通过存储方式获取配置
     * @param siteId
     * @param StorageType
     * @return
     */
    JSONObject getStorageByType(String StorageType);
}
