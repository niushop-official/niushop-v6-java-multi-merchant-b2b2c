package com.niu.core.service.admin.sys.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.sys.SysUser;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.admin.sys.ISysConfigService;
import com.niu.core.service.admin.sys.ISysUserService;
import com.niu.core.service.admin.sys.param.*;
import com.niu.core.service.admin.sys.vo.*;
import com.niu.core.service.core.site.ICoreSiteService;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import com.niu.core.service.core.sys.vo.SceneDomainVo;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class SysConfigServiceImpl implements ISysConfigService {

    @Resource
    ICoreSysConfigService coreSysConfigService;
    @Resource
    ICoreSiteService coreSiteService;
    @Resource
    ISysUserService sysUserService;

    /**
     * 获取网站信息
     * @return
     */
    @Override
    public SysWebsiteVo getWebSite() {
        return coreSysConfigService.getWebSite(RequestUtils.siteId());
    }

    /**
     * 获取网站信息
     * @return
     */
    @Override
    public void setWebSite(SysWebsiteParam configParam) {
        coreSysConfigService.setWebSite(RequestUtils.siteId(), configParam);
    }

    /**
     * 获取服务信息
     * @return
     */
    @Override
    public SysServiceVo getService() {
        return coreSysConfigService.getService(RequestUtils.siteId());
    }

    @Override
    public SysCopyRightVo getCopyRight() {
        return coreSysConfigService.getCopyRight(RequestUtils.siteId());
    }

    @Override
    public void setCopyRight(SysCopyRightParam configParam) {
        coreSysConfigService.setCopyRight(RequestUtils.siteId(), configParam);
    }

    /**
     * 获取地图配置
     * @return
     */
    public SysMapVo getMap() {
        return coreSysConfigService.getMap(RequestUtils.siteId());
    }

    /**
     * 设置地图配置
     * @param configParam
     */
    public void setMap(SysMapParam configParam) {
        coreSysConfigService.setMap(RequestUtils.siteId(), configParam);
    }

    /**
     * 获取开发者key
     * @return
     */
    public SysDeveloperTokenVo getDeveloperToken() {
        return coreSysConfigService.getDeveloperToken();
    }

    /**
     * 设置开发者key
     * @param configParam
     */
    public void setDeveloperToken(SysDeveloperTokenParam configParam) {
        coreSysConfigService.setDeveloperToken(configParam);
    }

    /**
     * 获取布局风格
     * @return
     */
    public JSONObject getLayout() {
        return coreSysConfigService.getLayout(RequestUtils.siteId());
    }

    /**
     * 设置布局风格
     * @param configParam
     */
    public void setLayout(JSONObject configParam) {
        coreSysConfigService.setLayout(RequestUtils.siteId(), configParam);
    }

    /**
     * 获取布局风格
     * @return
     */
    public JSONObject getThemeColor() {
        return coreSysConfigService.getThemeColor(RequestUtils.siteId());
    }

    /**
     * 设置布局风格
     * @param configParam
     */
    public void setThemeColor(JSONObject configParam) {
        coreSysConfigService.setThemeColor(RequestUtils.siteId(), configParam);
    }

    /**
     * 获取注册与登录设置
     * @return
     */
    public SysLoginConfigVo getLogin() {
        return coreSysConfigService.getLogin();
    }

    /**
     * 设置注册与登录设置
     * @param configParam
     */
    public void setLogin(SysLoginConfigParam configParam) {
        coreSysConfigService.setLogin(configParam);
    }

    /**
     * 获取
     * @return
     */
    public SceneDomainVo getUrl() {
        return coreSysConfigService.getSceneDomain();
    }

    /**
     * 设置消息接收人信息
     * @param param
     */
    @Override
    public void setNoticeRecipientInfo(SysNoticeRecipientParam param) {

        JSONObject data = new JSONObject();
        data.put("uid", param.getUid());
        SysUser user = sysUserService.find(param.getUid());
        if (user == null || user.getMobile() == null || user.getMobile().trim().isEmpty()) {
            throw new RuntimeException("请完善用户手机号");
        }
        coreSysConfigService.setConfig(RequestUtils.siteId(), "NOTICE_RECIPIENT_INFO", data);
    }

    @Override
    public SysNoticeRecipientVo getNoticeRecipientInfo() {
        CoreSysConfigVo config = coreSysConfigService.getConfig(RequestUtils.siteId(), "NOTICE_RECIPIENT_INFO");

        if (config == null || config.getConfigKey() == null) {
            SysNoticeRecipientVo vo = new SysNoticeRecipientVo();
            vo.setUid(null);
            return vo;
        }

        SysNoticeRecipientVo vo = new SysNoticeRecipientVo();
        if (config.getValue() != null) {
            JSONObject valueJson = JSONUtil.parseObj(config.getValue());
            if (valueJson.containsKey("uid")) {
                vo.setUid(valueJson.getInt("uid"));
            } else {
                vo.setUid(null);
            }
        } else {
            vo.setUid(null);
        }
        return vo;
    }

    /**
     * 获取小程序上架设置信息
     * @return SysWeappListSettingVo
     */
    @Override
    public SysWeappListSettingVo getWeappListSetting() {
        CoreSysConfigVo config = coreSysConfigService.getConfig(RequestUtils.defaultSiteId(), "WEAPP_LIST_SETTING");
        
        if (config == null || config.getValue() == null) {
            SysWeappListSettingVo vo = new SysWeappListSettingVo();
            vo.setIsOpen(0);
            return vo;
        }

        JSONObject valueJson = JSONUtil.parseObj(config.getValue());
        SysWeappListSettingVo vo = new SysWeappListSettingVo();
        if (valueJson.containsKey("is_open")) {
            vo.setIsOpen(valueJson.getInt("is_open"));
        } else {
            vo.setIsOpen(0);
        }
        
        return vo;
    }

    /**
     * 修改小程序上架设置信息
     * @param param
     */
    @Override
    public void setWeappListSetting(SysWeappListSettingParam param) {
        JSONObject data = new JSONObject();
        data.put("is_open", param.getIsOpen());
        coreSysConfigService.setConfig(RequestUtils.defaultSiteId(), "WEAPP_LIST_SETTING", data);
    }

    @Override
    public String getServerDomain() {
        SiteInfoVo siteInfo = coreSiteService.getSiteCache(RequestUtils.defaultSiteId());
        return siteInfo.getSiteDomain();
    }

}
