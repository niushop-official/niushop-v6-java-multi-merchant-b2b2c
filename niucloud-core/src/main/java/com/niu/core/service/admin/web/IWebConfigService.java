package com.niu.core.service.admin.web;

import cn.hutool.json.JSONObject;
import com.niu.core.service.admin.web.vo.WebConfigVo;
import com.niu.core.service.core.weapp.param.WeappConfigParam;

/**
 * PC导航管理服务接口
 */
public interface IWebConfigService {

    JSONObject getLink();

    WebConfigVo getWebConfig();

    void setWebConfig(JSONObject data);



}
