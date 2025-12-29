package com.niu.core.service.api.sys;

import cn.hutool.json.JSONObject;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.admin.sys.vo.SysCopyRightVo;
import com.niu.core.service.admin.sys.vo.SysMapVo;
import com.niu.core.service.admin.sys.vo.SysWeappListSettingVo;
import com.niu.core.service.api.sys.param.SysConfigCopyrightParam;
import com.niu.core.service.api.sys.param.SysConfigMapParam;
import com.niu.core.service.api.sys.param.SysConfigSceneDomainParam;
import com.niu.core.service.api.sys.param.SysConfigSiteParam;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import com.niu.core.service.core.sys.vo.SceneDomainVo;

public interface ISysConfigService {

    /**
     * 版权信息
     *
     * @param param
     * @return
     */
    SysCopyRightVo copyright(SysConfigCopyrightParam param);

    /**
     * 站点信息
     *
     * @param param
     * @return
     */
    SiteInfoVo site(SysConfigSiteParam param);

    /**
     * 场景域名
     *
     * @param param
     * @return
     */
    SceneDomainVo sceneDomain(SysConfigSceneDomainParam param);

    /**
     * 获取地图设置
     *
     * @param param
     * @return
     */
    SysMapVo map(SysConfigMapParam param);

    /**
     * 获取服务信息
     *
     * @return
     */
    CoreSysConfigVo service();

    /**
     * 获取服务信息
     *
     * @return
     */
    String getIndexQrcode();

    SysWeappListSettingVo getWeappListSetting();
}
