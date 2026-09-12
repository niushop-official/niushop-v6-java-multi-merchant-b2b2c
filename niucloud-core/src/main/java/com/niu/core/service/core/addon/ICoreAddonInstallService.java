package com.niu.core.service.core.addon;

import cn.hutool.json.JSONObject;

public interface ICoreAddonInstallService {

    /**
     * 安装插件
     *
     * @param addon
     * @param mode 安装方式：local 本地安装，cloud 云安装
     * @return tips 数组（空数组表示无额外提示），对齐 PHP 返回值
     */
    Object install(String addon, String mode);

    /**
     * 获取安装任务
     */
    JSONObject getInstallTask();

    /**
     * 取消安装
     */
    void cancleInstall();

    /**
     * 插件安装检测
     *
     * @param addon
     */
    JSONObject installCheck(String addon);

    /**
     * 安装插件
     *
     * @param addon
     * @return
     */
    JSONObject uninstall(String addon);

    /**
     * 插件安装检测
     *
     * @param addon
     */
    JSONObject uninstallCheck(String addon);

    /**
     * 重启之后
     *
     * @param result
     * @param addon
     * @param message
     */
    void installResult(boolean result, String addon, String message);

    /**
     * 云安装进度
     *
     * @param addon
     * @return
     */
    JSONObject cloudInstallLog(String addon);
}
