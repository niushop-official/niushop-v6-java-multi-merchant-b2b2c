package com.niu.core.service.site.shop;


import com.niu.core.service.admin.shop.param.SiteShopAccountInfoParam;
import com.niu.core.service.admin.shop.param.SiteShopBasicInfoParam;
import com.niu.core.service.admin.shop.vo.SiteShopInShopVo;
import com.niu.core.service.admin.shop.vo.SiteShopInfoVo;
import com.niu.core.service.site.shop.vo.ShopSiteInfoVo;

/**
 * 店铺扩展服务接口
 */
public interface ISiteShopSettingService {

    /**
     * 店铺扩展详情
     * @return  SiteShopInfoVo
     */
    SiteShopInfoVo getBasicInfo();

    void setBasicInfo(SiteShopBasicInfoParam siteShopBasicInfoParam);

    ShopSiteInfoVo getShopInfo();

    /**
     * 获取店铺账户信息
     * @return
     */
    SiteShopInShopVo getShopAccountInfo();

    /**
     * 编辑店铺账户信息
     * @param siteShopAccountParam
     */
    void editShopAccount(SiteShopAccountInfoParam siteShopAccountParam);


}
