package com.niu.core.service.api.sys.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.QrcodeUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.admin.sys.vo.SysCopyRightVo;
import com.niu.core.service.admin.sys.vo.SysMapVo;
import com.niu.core.service.admin.sys.vo.SysWeappListSettingVo;
import com.niu.core.service.api.sys.ISysConfigService;
import com.niu.core.service.api.sys.param.SysConfigCopyrightParam;
import com.niu.core.service.api.sys.param.SysConfigMapParam;
import com.niu.core.service.api.sys.param.SysConfigSceneDomainParam;
import com.niu.core.service.api.sys.param.SysConfigSiteParam;
import com.niu.core.service.core.site.ICoreSiteService;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import com.niu.core.service.core.sys.vo.SceneDomainVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class SysConfigServiceImpl implements ISysConfigService {

    @Autowired
    private ICoreSysConfigService coreSysConfigService;

    @Autowired
    private ICoreSiteService coreSiteService;

    @Override
    public SysCopyRightVo copyright(SysConfigCopyrightParam param) {
        return coreSysConfigService.getCopyRight(param.siteId());
    }

    @Override
    public SiteInfoVo site(SysConfigSiteParam param) {
        return coreSiteService.getSiteCache(param.siteId());
    }

    @Override
    public SceneDomainVo sceneDomain(SysConfigSceneDomainParam param) {
        return coreSysConfigService.getSceneDomain();
    }

    @Override
    public SysMapVo map(SysConfigMapParam param) {
        return coreSysConfigService.getMap(param.siteId());
    }

    /**
     * 获取服务信息（与PHP代码逻辑保持一致）
     *
     * @return JSONObject
     */
    @Override
    public CoreSysConfigVo service() {
        CoreSysConfigVo info = coreSysConfigService.getConfig(RequestUtils.defaultSiteId(), "SERVICE_INFO");

        // 如果配置为空，返回带有默认值的配置对象
        if (info == null) {
            JSONObject defaultValue = JSONUtil.createObj();
            defaultValue.set("wechat_code", "");
            defaultValue.set("enterprise_wechat", "");
            defaultValue.set("tel", "");

            info = new CoreSysConfigVo();
            info.setValue(defaultValue.toString()); // 假设 CoreSysConfigVo 的 value 字段是 String 类型
        }

        return info;
    }

    @Override
    public String getIndexQrcode() {
        Map<String, Object> urlData = Map.of("key", "", "value", "");
        // 生成二维码
        String url = QrcodeUtils.qrcodeToFile(
                RequestUtils.defaultSiteId(),
                "weapp",
                coreSysConfigService.getSceneDomain().getWapUrl(),
                "app/pages/index/index",
                urlData,
                "upload/qrcode/index"
        );
        return url;
    }

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

}
