package com.niu.mall.upgrade.v120;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.upgrade.IUpgradeProvider;
import com.niu.core.common.component.upgrade.annotation.UpgradeProvider;
import com.niu.core.entity.diy.DiyPage;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.entity.site.SiteCategory;
import com.niu.core.mapper.diy.DiyPageMapper;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.mapper.site.SiteCategoryMapper;
import com.niu.core.service.admin.diy.IDiyService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@UpgradeProvider(addon = "mall", version = "1.2.0")
public class Upgrade extends IUpgradeProvider {

    private static final List<String> CATEGORY_NAMES = Arrays.asList(
            "数码家电", "家装建材", "日用百货", "生鲜水果", "母婴玩具", "食品酒水"
    );

    @Override
    public void handle() {
        handleDiyPageData();
        handleSiteCategoryData();
    }

    /**
     * M-07: 初始化商城 DIY 页
     */
    public void handleDiyPageData() {
        IDiyService diyService = SpringContext.bean(IDiyService.class);
        diyService.initDefaultPage(0, "DIY_MALL_INDEX", "mall", "");
        diyService.initDefaultPage(0, "DIY_MALL_MEMBER_INDEX", "mall", "");

        DiyPageMapper diyPageMapper = SpringContext.bean(DiyPageMapper.class);
        SiteShopMapper siteShopMapper = SpringContext.bean(SiteShopMapper.class);
        List<SiteShop> shopList = siteShopMapper.selectList(new LambdaQueryWrapper<SiteShop>()
                .gt(SiteShop::getSiteId, 0)
                .select(SiteShop::getSiteId)
        );
        for (SiteShop shop : shopList) {
            Integer siteId = shop.getSiteId();
            if (siteId == null || siteId <= 0) {
                continue;
            }
            Long count = diyPageMapper.selectCount(new LambdaQueryWrapper<DiyPage>()
                    .eq(DiyPage::getSiteId, siteId)
                    .eq(DiyPage::getIsDefault, 1)
                    .in(DiyPage::getName, "DIY_MALL_SITE_INDEX", "DIY_SITE_INDEX")
            );
            if (count == null || count == 0) {
                diyService.initDefaultPage(siteId, "DIY_MALL_SITE_INDEX", "mall", "");
            }
        }
    }

    /**
     * M-07: mall 行业类目种子
     */
    public void handleSiteCategoryData() {
        SiteCategoryMapper siteCategoryMapper = SpringContext.bean(SiteCategoryMapper.class);
        long now = System.currentTimeMillis() / 1000;
        for (String name : CATEGORY_NAMES) {
            Long count = siteCategoryMapper.selectCount(new LambdaQueryWrapper<SiteCategory>()
                    .eq(SiteCategory::getName, name)
                    .eq(SiteCategory::getAddon, "mall")
            );
            if (count != null && count > 0) {
                continue;
            }
            SiteCategory category = new SiteCategory();
            category.setName(name);
            category.setLevel(1);
            category.setPid(0);
            category.setStatus(1);
            category.setAddon("mall");
            category.setServiceRate(BigDecimal.ZERO);
            category.setCreateTime(now);
            category.setUpdateTime(now);
            siteCategoryMapper.insert(category);
        }
    }
}
