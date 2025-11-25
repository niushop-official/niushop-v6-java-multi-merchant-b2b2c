package com.niu.core.service.admin.web.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.enums.web.WebLinkEnum;
import com.niu.core.service.admin.web.IWebConfigService;
import com.niu.core.service.admin.web.vo.WebConfigVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.weapp.param.WeappConfigParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class WebConfigServiceImpl implements IWebConfigService {

    @Resource
    ICoreConfigService coreConfigService;

    @Override
    public JSONObject getLink() {
        JSONObject links = WebLinkEnum.getLink();
        for (String key : links.keySet()) {
            JSONObject link = links.getJSONObject(key);
            link.put("name", key);
            if(ObjectUtil.isNotEmpty(link.get("child_list")))
            {
                JSONArray childList = link.getJSONArray("child_list");
                for (int i = 0; i < childList.size(); i++) {
                    JSONObject child = childList.getJSONObject(i);
                    child.put("parent", key);
                    childList.set(i, child);
                }

            }
        }
        return links;
    }



    @Override
    public WebConfigVo getWebConfig() {
        JSONObject config = coreConfigService.getConfigValue(RequestUtils.defaultSiteId(), "WEB");
        if (config.isEmpty()) {
            WebConfigVo webConfigVo = new WebConfigVo();
            webConfigVo.setIsShowShop(0);
            webConfigVo.setSite_ids("[]");
            webConfigVo.setShopAdImage("static/resource/images/web/shop_01.png");
            return webConfigVo;
        }
        return JSONUtil.toBean(config, WebConfigVo.class);
    }

    @Override
    public void setWebConfig(JSONObject data) {
        coreConfigService.setConfig(RequestUtils.defaultSiteId(), "WEB", data);
    }

}
