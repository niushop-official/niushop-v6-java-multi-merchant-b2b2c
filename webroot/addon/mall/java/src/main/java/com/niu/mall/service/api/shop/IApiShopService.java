package com.niu.mall.service.api.shop;

import com.niu.mall.service.api.shop.vo.ShopBusinessStatusVo;

public interface IApiShopService {

    /**
     * 获取店铺营业状态
     */
    ShopBusinessStatusVo getBusinessStatus(Integer siteId);
}
