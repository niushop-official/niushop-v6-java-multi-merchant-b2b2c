package com.niu.mall.service.site.shop.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.service.admin.shop.ISiteShopService;
import com.niu.core.service.admin.shop.param.SiteShopBasicInfoParam;
import com.niu.mall.service.site.shop.IShopSettingService;
import com.niu.mall.service.site.shop.param.ShopSettingParam;
import com.niu.mall.service.site.shop.vo.ShopSettingInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShopSettingServiceImpl implements IShopSettingService {

    @Autowired
    private SiteShopMapper shopMapper;
    @Autowired
    private ISiteShopService siteShopService;

    private Integer getCurrentSiteId() {
        return RequestUtils.siteId();
    }

    @Override
    public ShopSettingInfoVo getBasicInfo() {
        Integer siteId = getCurrentSiteId();
        MPJQueryWrapper<SiteShop> wrapper = new MPJQueryWrapper<>();
        wrapper.setAlias("sp")
                .leftJoin("?_site s ON sp.site_id = s.site_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category c ON sp.category_id = c.category_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_category cc ON sp.child_category_id = cc.category_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site_group g ON s.group_id = g.group_id".replace("?_", GlobalConfig.tablePrefix))
                .select("sp.site_id, s.site_name, s.front_end_name, s.front_end_logo, sp.keywords, " +
                        "s.logo, s.icon, s.desc, s.status, s.latitude, s.longitude, s.province_id, s.city_id, " +
                        "s.district_id, s.address, s.full_address, s.phone, s.business_hours, s.group_id, " +
                        "s.app, s.addons, s.site_domain, s.business_license, s.contact_name, sp.is_recommend, " +
                        "sp.is_self, sp.category_id, sp.child_category_id, sp.service_rate, sp.business_status, " +
                        "sp.background_h5, s.create_time, " +
                        "c.name as category_name, cc.name as child_category_name, g.group_name")
                .eq("sp.site_id", siteId);

        ShopSettingInfoVo vo = shopMapper.selectJoinOne(ShopSettingInfoVo.class, wrapper);
        if (vo == null) {
            throw new CommonException("店铺信息不存在");
        }
        return vo;
    }

    @Override
    @Transactional
    public void setBasicInfo(ShopSettingParam param) {
        SiteShopBasicInfoParam editParam = new SiteShopBasicInfoParam();
        BeanUtil.copyProperties(param, editParam);
        siteShopService.editBasicInfo(getCurrentSiteId(), editParam);
    }
}
