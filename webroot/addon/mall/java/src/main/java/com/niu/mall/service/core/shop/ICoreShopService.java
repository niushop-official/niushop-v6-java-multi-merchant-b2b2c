package com.niu.mall.service.core.shop;

import com.niu.mall.service.core.shop.vo.ShopInfoOfOrderCreateVo;

public interface ICoreShopService {

    /**
     * 获取站点信息用于订单创建
     *
     * @param siteId 站点ID
     * @return 站点及店铺信息
     */
    ShopInfoOfOrderCreateVo getShopInfoOfOrderCreate(Integer siteId);
}
