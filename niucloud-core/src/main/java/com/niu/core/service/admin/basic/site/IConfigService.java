package com.niu.core.service.admin.basic.site;

import com.niu.core.service.admin.basic.site.param.SiteConfigParam;
import com.niu.core.service.admin.basic.site.vo.SiteConfigVo;

/**
 * 通用设置服务
 */
public interface IConfigService {

    /**
     * 获取通用设置
     */
    SiteConfigVo getConfig();

    /**
     * 设置通用配置
     */
    void setConfig(SiteConfigParam param);
}