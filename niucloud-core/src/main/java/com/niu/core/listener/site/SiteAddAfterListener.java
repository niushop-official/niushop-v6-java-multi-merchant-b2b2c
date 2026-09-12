package com.niu.core.listener.site;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.event.site.SiteAddAfterEvent;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.admin.diy.IDiyService;
import com.niu.core.service.admin.diy.param.SetDiyDataParam;
import com.niu.core.service.core.delivery_store.ICoreDeliveryStoreService;
import com.niu.core.service.core.delivery_store.param.DeliverySysStoreAddParam;
import com.niu.core.service.core.electronic_sheet.ICoreElectronicSheetService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@EventListen("core")
@Component
public class SiteAddAfterListener extends AbstractListener {

    private IDiyService diyService() {
        return SpringContext.bean(IDiyService.class);
    }

    private ICoreDeliveryStoreService coreDeliveryStoreService() {
        return SpringContext.bean(ICoreDeliveryStoreService.class);
    }

    private ICoreElectronicSheetService coreElectronicSheetService() {
        return SpringContext.bean(ICoreElectronicSheetService.class);
    }

    private SiteMapper siteMapper() {
        return SpringContext.bean(SiteMapper.class);
    }

    /**
     * 监听事件
     */
    public void handleEvent(SiteAddAfterEvent event) {
        SiteGroup siteGroup = event.getSiteGroup();
        JSONArray mainApp = JSONUtil.parseArray(siteGroup.getApp());
        handleDiyData(mainApp);

        coreElectronicSheetService().initDefaultElectronicSheet(event.getSiteId());
        Site site = siteMapper().selectById(event.getSiteId());
        if (ObjectUtil.isNotEmpty(site)) {
            DeliverySysStoreAddParam param = new DeliverySysStoreAddParam();
            BeanUtils.copyProperties(site, param);
            coreDeliveryStoreService().createSysStore(param);
        }
    }

    private void handleDiyData(JSONArray mainApp) {
        int count = mainApp.size();
        JSONArray addonList = new JSONArray();
        addonList.add("");
        addonList.addAll(mainApp);

        for (int i = 0; i < addonList.size(); i++) {
            int isStart;
            if (count > 1) {
                isStart = i == 0 ? 1 : 0;
            } else {
                isStart = i == 0 ? 0 : 1;
            }

            String addon = addonList.getStr(i);
            setDiyData("DIY_INDEX", "index", addon, isStart);
            setDiyData("DIY_MEMBER_INDEX", "member_index", addon, isStart);
        }
    }

    private void setDiyData(String key, String type, String addon, int isStart) {
        SetDiyDataParam setParam = new SetDiyDataParam();
        setParam.setSiteId(0);
        setParam.setKey(key);
        setParam.setType(type);
        setParam.setAddon(addon);
        setParam.setIsStart(isStart);
        diyService().setDiyData(setParam);
    }
}
