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
        JSONObject info = coreConfigService.getConfigValue(RequestUtils.defaultSiteId(), "WEB");
        if (info == null || info.isEmpty()) {
            info = new JSONObject();
        }
        
        WebConfigVo vo = new WebConfigVo();
        vo.setIsShowShop(info.getInt("is_show_shop", 0));
        vo.setShopAdImage(info.getStr("shop_ad_image", "static/resource/images/web/shop_01.png"));
        // 处理 shop_ad_link：如果是空字符串或不存在，返回空对象
        Object shopAdLinkObj = info.get("shop_ad_link");
        if (ObjectUtil.isEmpty(shopAdLinkObj) || (shopAdLinkObj instanceof String && ((String) shopAdLinkObj).isEmpty())) {
            vo.setShopAdLink("{}");
        } else {
            vo.setShopAdLink(JSONUtil.toJsonStr(shopAdLinkObj));
        }
        vo.setSite_ids(ObjectUtil.isNotEmpty(info.get("site_ids")) ? JSONUtil.toJsonStr(info.get("site_ids")) : "[]");
        vo.setIsShowTop(info.getInt("is_show_top", 1));
        vo.setTopImage(info.getStr("top_image", ""));
        // 处理 top_link：如果是空字符串或不存在，返回空对象
        Object topLinkObj = info.get("top_link");
        if (ObjectUtil.isEmpty(topLinkObj) || (topLinkObj instanceof String && ((String) topLinkObj).isEmpty())) {
            vo.setTopLink("{}");
        } else {
            vo.setTopLink(JSONUtil.toJsonStr(topLinkObj));
        }
        vo.setCloseType(info.getStr("close_type", "auto"));
        vo.setCloseTime(info.getInt("close_time", 10));
        
        return vo;
    }

    @Override
    public void setWebConfig(JSONObject data) {
        JSONObject config = new JSONObject();
        config.set("is_show_shop", data.getInt("is_show_shop", 0));
        config.set("shop_ad_image", data.getStr("shop_ad_image", ""));
        // 处理 shop_ad_link：直接使用传入的值（可能是对象或字符串）
        Object shopAdLink = data.get("shop_ad_link");
        if (shopAdLink == null || (shopAdLink instanceof String && ((String) shopAdLink).isEmpty())) {
            config.set("shop_ad_link", "");
        } else {
            config.set("shop_ad_link", shopAdLink);
        }
        config.set("site_ids", data.get("site_ids") != null ? data.get("site_ids") : new JSONArray());
        config.set("is_show_top", data.getInt("is_show_top", 1));
        config.set("top_image", data.getStr("top_image", ""));
        // 处理 top_link：直接使用传入的值（可能是对象或字符串）
        Object topLink = data.get("top_link");
        if (topLink == null || (topLink instanceof String && ((String) topLink).isEmpty())) {
            config.set("top_link", "");
        } else {
            config.set("top_link", topLink);
        }
        config.set("close_type", data.getStr("close_type", "auto"));
        config.set("close_time", data.getInt("close_time", 10));
        
        coreConfigService.setConfig(RequestUtils.defaultSiteId(), "WEB", config);
    }

}
