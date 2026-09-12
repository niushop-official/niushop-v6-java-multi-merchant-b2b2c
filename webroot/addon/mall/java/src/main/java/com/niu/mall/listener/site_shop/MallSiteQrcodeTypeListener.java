package com.niu.mall.listener.site_shop;

import com.niu.core.common.annotation.EventCallback;
import com.niu.core.event.site.SiteQrcodeTypeEventDefiner;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 商城商家二维码类型
 * 对应 PHP: addon\mall\app\listener\site_shop\SiteQrcodeType
 */
@Component
@EventCallback("mall")
public class MallSiteQrcodeTypeListener extends SiteQrcodeTypeEventDefiner {

    @Override
    public SiteQrcodeTypeResult handleCallback(SiteQrcodeTypeEvent event) {
        Map<String, String> item = new LinkedHashMap<>();
        item.put("name", "商城商家二维码");
        item.put("type", "mall_shop");
        item.put("addon", "mall");
        SiteQrcodeTypeResult result = new SiteQrcodeTypeResult();
        result.setTypes(List.of(item));
        return result;
    }
}
