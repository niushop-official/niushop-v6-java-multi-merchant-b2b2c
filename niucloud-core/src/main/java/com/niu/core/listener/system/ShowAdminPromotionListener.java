package com.niu.core.listener.system;

import com.niu.core.common.annotation.EventCallback;
import com.niu.core.enums.sys.AppTypeEnum;
import com.niu.core.event.sys.ShowPromotionEventDefiner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * 平台端营销列表
 */
@Component
@EventCallback("core")
public class ShowAdminPromotionListener extends ShowPromotionEventDefiner {

    @Override
    public ShowPromotionResult handleCallback(ShowPromotionEvent event) {
        ShowPromotionResult result = new ShowPromotionResult();
        result.setList(new ArrayList<>());
        if (event == null || !AppTypeEnum.ADMIN.getName().equals(event.getAppType())) {
            return result;
        }
        result.setList(ShowAppConfigLoader.loadPromotionItems(AppTypeEnum.ADMIN.getName()));
        return result;
    }
}
