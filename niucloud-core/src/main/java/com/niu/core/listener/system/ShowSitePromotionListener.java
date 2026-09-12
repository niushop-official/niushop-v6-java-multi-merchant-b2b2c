package com.niu.core.listener.system;

import com.niu.core.common.annotation.EventCallback;
import com.niu.core.enums.sys.AppTypeEnum;
import com.niu.core.event.sys.ShowPromotionEventDefiner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * 站点端营销列表
 */
@Component
@EventCallback("core")
public class ShowSitePromotionListener extends ShowPromotionEventDefiner {

    @Override
    public ShowPromotionResult handleCallback(ShowPromotionEvent event) {
        ShowPromotionResult result = new ShowPromotionResult();
        result.setList(new ArrayList<>());
        if (event == null || !AppTypeEnum.SITE.getName().equals(event.getAppType())) {
            return result;
        }
        result.setList(ShowAppConfigLoader.loadPromotionItems(AppTypeEnum.SITE.getName()));
        return result;
    }
}
