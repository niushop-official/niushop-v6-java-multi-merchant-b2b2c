package com.niu.core.service.core.shop;


import com.niu.core.entity.shop.SiteShopCashOut;
import com.niu.core.service.core.shop.param.ShopTransferParam;

import java.math.BigDecimal;

public interface ICoreShopCashOutService {

    void transferFinish(String transferNo);

    void give(SiteShopCashOut siteShopCashOut);

    SiteShopCashOut find(Integer id);

    void transfer(Integer id, ShopTransferParam shopTransferParam);

}
