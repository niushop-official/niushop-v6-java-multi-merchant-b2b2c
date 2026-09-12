package com.niu.core.listener.addon;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.event.addon.InstallAddonAfterEvent;
import com.niu.core.service.core.shop.ICoreShopSiteGroupService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 插件安装成功后创建应用默认套餐（对齐 PHP InstallAddonAfterListener）
 */
@EventListen("core")
@Component
public class InstallAddonAfterListener extends AbstractListener {

    @Resource
    private ICoreShopSiteGroupService coreShopSiteGroupService;

    public void handleEvent(InstallAddonAfterEvent event) {
        String addon = event.getAddon();
        if (!StringUtils.hasText(addon)) {
            return;
        }
        coreShopSiteGroupService.createAppGroup(addon);
        coreShopSiteGroupService.updateSysGroup();
    }
}
