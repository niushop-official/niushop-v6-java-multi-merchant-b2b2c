package com.niu.core.listener.system;

import com.niu.core.common.annotation.EventCallback;
import com.niu.core.enums.sys.AppTypeEnum;
import com.niu.core.event.sys.ShowAppEventDefiner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * 平台端应用列表
 */
@Component
@EventCallback("core")
public class ShowAdminAppListener extends ShowAppEventDefiner {

    @Override
    public ShowAppResult handleCallback(ShowAppEvent event) {
        ShowAppResult result = new ShowAppResult();
        result.setList(new ArrayList<>());
        if (event == null || !AppTypeEnum.ADMIN.getName().equals(event.getAppType())) {
            return result;
        }
        result.setList(ShowAppConfigLoader.loadShowAppItems(AppTypeEnum.ADMIN.getName()));
        return result;
    }
}
