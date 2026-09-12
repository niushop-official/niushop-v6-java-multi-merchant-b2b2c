package com.niu.mall.service.site.shop;


import com.niu.mall.service.site.shop.param.ShopSettingParam;
import com.niu.mall.service.site.shop.vo.ShopSettingInfoVo;

public interface IShopSettingService {
    ShopSettingInfoVo getBasicInfo();

    void setBasicInfo(ShopSettingParam param);
}