package com.niu.core.upgrade.v002;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.upgrade.IUpgradeProvider;
import com.niu.core.common.component.upgrade.annotation.UpgradeProvider;
import com.niu.core.entity.delivery.DeliveryLocalDelivery;
import com.niu.core.entity.delivery.DeliveryStore;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteAddress;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryMapper;
import com.niu.core.mapper.delivery.DeliveryStoreMapper;
import com.niu.core.mapper.site.SiteAddressMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.core.delivery_store.ICoreDeliveryStoreService;
import com.niu.core.service.core.delivery_store.param.DeliverySysStoreAddParam;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@UpgradeProvider(addon = "core", version = "0.0.2")
public class Upgrade extends IUpgradeProvider {

    @Override
    public void handle() {
        handleSiteData();
        handleStoreData();
        handleSysStoreData();
    }

    //处理站点数据
    public  void handleSiteData(){
        SiteMapper siteMapper = SpringContext.bean(SiteMapper.class);
        siteMapper.update(new Site(),new UpdateWrapper<Site>().gt("site_id",0).eq("is_express",0).set("is_express",1));
        siteMapper.update(new Site(),new UpdateWrapper<Site>().gt("site_id",0).eq("is_local_delivery",0).set("is_local_delivery",2));
        siteMapper.update(new Site(),new UpdateWrapper<Site>().gt("site_id",0).eq("is_store",0).set("is_store",2));
    }

    //处理门店数据
    public void handleStoreData(){
        SiteAddressMapper siteAddressMapper = SpringContext.bean(SiteAddressMapper.class);
        DeliveryLocalDeliveryMapper deliveryLocalDeliveryMapper = SpringContext.bean(DeliveryLocalDeliveryMapper.class);
        DeliveryStoreMapper deliveryStoreMapper = SpringContext.bean(DeliveryStoreMapper.class);
        List<SiteAddress> siteAddressList = siteAddressMapper.selectList(new UpdateWrapper<SiteAddress>().gt("site_id",0).ne("shop_no",""));
        List<SiteAddress> defaultSiteAddressList = siteAddressMapper.selectList(new UpdateWrapper<SiteAddress>().gt("site_id",0)
                .eq("is_delivery_address",1).eq("is_default_delivery",1).ne("shop_no",""));

        siteAddressList.addAll(defaultSiteAddressList);
        if(siteAddressList.isEmpty()){
            return;
        }
        List<Integer> siteIdList = siteAddressList.stream().map(SiteAddress::getSiteId).distinct().toList();

        List<DeliveryLocalDelivery> deliveryLocalDeliveryList = deliveryLocalDeliveryMapper.selectList(new QueryWrapper<DeliveryLocalDelivery>().in("site_id",siteIdList));
        List<DeliveryStore> storeData = new ArrayList<>();
        for (SiteAddress siteAddress : siteAddressList){
            List<DeliveryLocalDelivery> localThis = deliveryLocalDeliveryList.stream().filter(item -> item.getSiteId().equals(siteAddress.getSiteId())).toList();
            DeliveryStore store = new DeliveryStore();
            BeanUtils.copyProperties(siteAddress, store);
            store.setStoreLogo("");
            store.setLatitude(siteAddress.getLat());
            store.setLongitude(siteAddress.getLng());
            store.setTradeTime(null);
            store.setTimeWeek(null);
            store.setTradeTimeJson(null);
            store.setTimeInterval(null);
            store.setSupportLocalDelivery(0);
            store.setSupportStore(0);
            store.setStatus(1);
            store.setArea( null);
            if(siteAddress.getIsDeliveryAddress().equals(1) && siteAddress.getIsDefaultDelivery().equals(1)){
                store.setSupportLocalDelivery(1);
                if(!localThis.isEmpty() && StringUtils.isNotEmpty(localThis.get(0).getArea())){
                    store.setArea(localThis.get(0).getArea());
                }

            }
            storeData.add( store);
        }
        deliveryStoreMapper.insert(storeData);
    }

    //处理系统门店数据
    public void handleSysStoreData(){
        SiteMapper siteMapper = SpringContext.bean(SiteMapper.class);
        ICoreDeliveryStoreService coreDeliveryStoreService = SpringContext.bean(ICoreDeliveryStoreService.class);
        List<Site> siteList = siteMapper.selectList(new QueryWrapper<Site>().gt("site_id",0));
        if(siteList.isEmpty()){
           return;
        }
        for (Site site : siteList){
            DeliverySysStoreAddParam param = new DeliverySysStoreAddParam();
            BeanUtils.copyProperties(site,param);
            coreDeliveryStoreService.createSysStore(param);
        }
    }
}
