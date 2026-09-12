package com.niu.core.service.admin.sys;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.service.admin.sys.param.*;
import com.niu.core.service.admin.sys.vo.*;
import com.niu.core.service.core.sys.vo.SceneDomainVo;

/**
 * 系统设置接口服务层
 */
public interface ISysConfigService {

    /**
     * 获取网站信息
     * @return
     */
    SysWebsiteVo getWebSite();

    /**
     * 设置网站信息
     * @return
     */
    void setWebSite(SysWebsiteParam configParam);

    /**
     * 获取服务信息
     * @return
     */
    SysServiceVo getService();

    /**
     * 获取版权信息
     * @return
     */
    SysCopyRightVo getCopyRight();

    /**
     * 设置版权信息
     * @param configParam
     */
    void setCopyRight(SysCopyRightParam configParam);

    /**
     * 获取地图配置
     * @return
     */
    SysMapVo getMap(Boolean needEncrypt);

    /**
     * 设置地图配置
     * @param configParam
     */
    void setMap(SysMapParam configParam);

    /**
     * 获取开发者key
     * @return
     */
    SysDeveloperTokenVo getDeveloperToken();

    /**
     * 设置开发者key
     * @param configParam
     */
    void setDeveloperToken(SysDeveloperTokenParam configParam);

    /**
     * 获取布局风格
     * @return
     */
    JSONObject getLayout();

    /**
     * 设置布局风格
     * @param configParam
     */
    void setLayout(JSONObject configParam);

    /**
     * 获取布局风格
     * @return
     */
    JSONObject getThemeColor();

    /**
     * 设置布局风格
     * @param configParam
     */
    void setThemeColor(JSONObject configParam);


    /**
     * 获取注册与登录设置
     * @return
     */
    SysLoginConfigVo getLogin();

    /**
     * 设置注册与登录设置
     * @param configParam
     */
    void setLogin(SysLoginConfigParam configParam);

    /**
     * 获取域名配置
     * @return
     */
    SceneDomainVo getUrl();

    /**
     * 设置消息接收人信息
     * @param param
     */
    void setNoticeRecipientInfo(SysNoticeRecipientParam param);

    SysNoticeRecipientVo getNoticeRecipientInfo();

    /**
     * 获取小程序上架设置信息
     * @return
     */
    SysWeappListSettingVo getWeappListSetting();

    /**
     * 修改小程序上架设置信息
     * @param param
     */
    void setWeappListSetting(SysWeappListSettingParam param);

    /**
     * 获取服务器域名
     */
    String getServerDomain();

    /**
     * 获取平台首页列表
     */
    JSONArray getAdminIndexList();

    /**
     * 设置平台首页
     */
    void setAdminIndex(SysAdminIndexParam param);

}
