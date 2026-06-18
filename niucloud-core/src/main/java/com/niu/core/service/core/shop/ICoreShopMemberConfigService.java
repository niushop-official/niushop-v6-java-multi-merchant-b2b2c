package com.niu.core.service.core.shop;

import com.niu.core.service.core.shop.param.ShopMemberConfigParam;
import com.niu.core.service.core.shop.vo.ShopMemberConfigVo;

/**
 * 店铺会员设置
 */
public interface ICoreShopMemberConfigService {

    ShopMemberConfigVo getMemberConfig(Integer siteId);

    void setMemberConfig(Integer siteId, ShopMemberConfigParam param);
}
