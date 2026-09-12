package com.niu.core.listener.site;

import com.niu.core.common.annotation.EventCallback;
import com.niu.core.event.site.SiteQrcodeTypeEventDefiner;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 买单二维码类型
 * 对应 PHP: app\listener\site\SiteQrcodeType
 */
@Component
@EventCallback("core")
public class CashSiteQrcodeTypeListener extends SiteQrcodeTypeEventDefiner {

    @Override
    public SiteQrcodeTypeResult handleCallback(SiteQrcodeTypeEvent event) {
        Map<String, String> item = new LinkedHashMap<>();
        item.put("name", "买单二维码");
        item.put("type", "cash");
        item.put("addon", "");
        SiteQrcodeTypeResult result = new SiteQrcodeTypeResult();
        result.setTypes(List.of(item));
        return result;
    }
}
