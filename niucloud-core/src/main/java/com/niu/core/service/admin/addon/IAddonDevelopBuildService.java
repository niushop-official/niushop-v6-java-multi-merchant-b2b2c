package com.niu.core.service.admin.addon;

/**
 * 插件打包
 */
public interface IAddonDevelopBuildService {

    /**
     * 插件打包
     * @param addon
     */
    void build(String addon);

    /**
     * 下载
     *
     * @param key
     * @return
     */
    String download(String key);
}
