package com.niu.core.listener.addon;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.event.addon.UninstallAddonAfterEvent;
import com.niu.core.service.core.shop.ICoreShopSiteGroupService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 插件卸载成功后清理应用默认套餐（对齐 PHP UninstallAddonAfterListener）
 */
@EventListen("core")
@Component
public class UninstallAddonAfterListener extends AbstractListener {

    @Resource
    private ICoreShopSiteGroupService coreShopSiteGroupService;

    public void handleEvent(UninstallAddonAfterEvent event) {
        String addon = event.getAddon();
        if (!StringUtils.hasText(addon)) {
            return;
        }
        coreShopSiteGroupService.deleteAppGroup(addon);
        coreShopSiteGroupService.updateSysGroup();
    }
}
