package com.niu.core.service.admin.install;

/**
 * 系统安装
 */
public interface IInstallArticleService {


    /**
     * 安装
     *
     */
    void install(Integer siteId);

    /**
     * 安装数据
     *
     */
    void installArticle(Integer siteId);

}
