package com.niu.core.service.site.shop;

import com.niu.core.service.core.shop.param.ShopMemberConfigParam;
import com.niu.core.service.core.shop.vo.ShopMemberConfigVo;

/**
 * 店铺会员设置
 */
public interface ISiteShopMemberConfigService {

    ShopMemberConfigVo getMemberConfig();

    void setMemberConfig(ShopMemberConfigParam param);
}
