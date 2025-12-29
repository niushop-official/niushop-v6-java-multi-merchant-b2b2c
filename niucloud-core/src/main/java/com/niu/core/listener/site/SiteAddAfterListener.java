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
     *
     * @param event
     */
    public void handleEvent(SiteAddAfterEvent event) {
        SiteGroup siteGroup = event.getSiteGroup();
        JSONArray app = JSONUtil.parseArray(siteGroup.getApp());

        SetDiyDataParam setParam = new SetDiyDataParam();
        setParam.setSiteId(event.getSiteId());
        setParam.setType("index");
        setParam.setKey("DIY_SITE_INDEX");
        setParam.setIsStart(app.size() > 1 ? 1 : 0);
        setParam.setMainApp(app);
        diyService().setDiyData(setParam);

        setParam.setType("member_index");
        setParam.setKey("DIY_MEMBER_INDEX");
        diyService().setDiyData(setParam);

        //初始化电子面单
        coreElectronicSheetService().initDefaultElectronicSheet(event.getSiteId());
        //初始化配送门店
        Site site = siteMapper().selectById(event.getSiteId());
        if(ObjectUtil.isNotEmpty(site)){
            DeliverySysStoreAddParam param = new DeliverySysStoreAddParam();
            BeanUtils.copyProperties(site,param);
            coreDeliveryStoreService().createSysStore(param);
        }
    }

}