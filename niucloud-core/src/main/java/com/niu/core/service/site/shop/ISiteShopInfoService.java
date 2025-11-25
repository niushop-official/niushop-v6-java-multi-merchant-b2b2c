package com.niu.core.service.site.shop;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.service.site.shop.vo.SiteShopInfoVo;

/**
 * 店铺扩展服务接口
 */
public interface ISiteShopInfoService {

    /**
     * 店铺扩展详情
     * @return  SiteShopInfoVo
     */
    SiteShopInfoVo getShopInfo(Integer siteId);

    /**
     * 店铺营业状态编辑
     * @return  SiteShopInfoVo
     */
    void editBusinessStatus(Integer businessStatus);

    public boolean check();
}
