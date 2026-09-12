package com.niu.core.upgrade.v122;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.upgrade.IUpgradeProvider;
import com.niu.core.common.component.upgrade.annotation.UpgradeProvider;
import com.niu.core.entity.diy.DiyPage;
import com.niu.core.entity.separ_account.SeparAccount;
import com.niu.core.entity.separ_account.SeparAccountDetail;
import com.niu.core.entity.shop.SiteO2o;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.entity.shop.SiteSupply;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteCategory;
import com.niu.core.mapper.diy.DiyPageMapper;
import com.niu.core.mapper.separ_account.SeparAccountDetailMapper;
import com.niu.core.mapper.separ_account.SeparAccountMapper;
import com.niu.core.mapper.shop.SiteO2oMapper;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.mapper.shop.SiteSupplyMapper;
import com.niu.core.mapper.site.SiteCategoryMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.core.poster.ICorePosterService;
import com.niu.core.upgrade.v120.V120UpgradeHelper;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 核心框架 v1.2.2 数据迁移。
 * <p>
 * 在 {@code 1.2.2.sql} 执行 DDL 之后运行；执行顺序须与 PHP v121 一致。
 * </p>
 */
@UpgradeProvider(addon = "core", version = "1.2.2")
public class Upgrade extends IUpgradeProvider {

    @Override
    public void handle() {
        ensureColumns();
        handleSiteCategoryData();
        handleSiteSupplyData();
        handleSiteO2oSplitData();
        handleDiyData();
        handleSiteBusinessStatusData();
        handleDiyPageData();
    }

    /**
     * 幂等补全 v1.2.0 可能已添加的列（兼容跳过 1.2.0 升级的环境）。
     */
    private void ensureColumns() {
        String separTable = V120UpgradeHelper.table("separ_account");
        V120UpgradeHelper.addColumnIfNotExists("separ_account", "trade_scene",
                "ALTER TABLE `" + separTable + "` ADD COLUMN `trade_scene` varchar(32) NOT NULL DEFAULT '' COMMENT '订单来源 订单表的order_from'");
        String siteTable = V120UpgradeHelper.table("site");
        V120UpgradeHelper.addColumnIfNotExists("site", "business_state",
                "ALTER TABLE `" + siteTable + "` ADD COLUMN `business_state` tinyint NOT NULL DEFAULT 1 COMMENT '营业状态（0已打烊，1营业中）'");
    }

    /**
     * 将所有 site 的 category_ids 规范为字符串 JSON 数组。
     */
    public void handleSiteCategoryData() {
        SiteMapper siteMapper = SpringContext.bean(SiteMapper.class);
        List<Site> siteList = siteMapper.selectList(new LambdaQueryWrapper<Site>()
                .gt(Site::getSiteId, 0));
        for (Site site : siteList) {
            List<String> categoryIds = normalizeCategoryIds(site.getCategoryIds());
            Site update = new Site();
            update.setSiteId(site.getSiteId());
            update.setCategoryIds(JSONUtil.toJsonStr(categoryIds));
            siteMapper.updateById(update);
        }
    }

    /**
     * 将供货商误用 mall 类目的 category_id 改为 supply 类目，并更新 site.category_ids。
     */
    public void handleSiteSupplyData() {
        SiteSupplyMapper siteSupplyMapper = SpringContext.bean(SiteSupplyMapper.class);
        SiteCategoryMapper siteCategoryMapper = SpringContext.bean(SiteCategoryMapper.class);
        SiteMapper siteMapper = SpringContext.bean(SiteMapper.class);

        List<Integer> usedCategoryIds = siteSupplyMapper.selectList(new LambdaQueryWrapper<SiteSupply>()
                        .gt(SiteSupply::getCategoryId, 0)
                        .select(SiteSupply::getCategoryId))
                .stream()
                .map(SiteSupply::getCategoryId)
                .distinct()
                .collect(Collectors.toList());
        if (usedCategoryIds.isEmpty()) {
            return;
        }

        List<Integer> mallCategoryIds = siteCategoryMapper.selectList(new LambdaQueryWrapper<SiteCategory>()
                        .in(SiteCategory::getCategoryId, usedCategoryIds)
                        .eq(SiteCategory::getAddon, "mall")
                        .select(SiteCategory::getCategoryId))
                .stream()
                .map(SiteCategory::getCategoryId)
                .collect(Collectors.toList());
        if (mallCategoryIds.isEmpty()) {
            return;
        }

        SiteCategory supplyCategory = siteCategoryMapper.selectOne(new LambdaQueryWrapper<SiteCategory>()
                .eq(SiteCategory::getAddon, "supply")
                .last("LIMIT 1"));
        if (supplyCategory == null || supplyCategory.getCategoryId() == null || supplyCategory.getCategoryId() <= 0) {
            return;
        }
        Integer supplyCategoryId = supplyCategory.getCategoryId();

        siteSupplyMapper.update(null, new LambdaUpdateWrapper<SiteSupply>()
                .in(SiteSupply::getCategoryId, mallCategoryIds)
                .set(SiteSupply::getCategoryId, supplyCategoryId));

        List<Integer> siteIds = siteSupplyMapper.selectList(new LambdaQueryWrapper<SiteSupply>()
                        .eq(SiteSupply::getCategoryId, supplyCategoryId)
                        .select(SiteSupply::getSiteId))
                .stream()
                .map(SiteSupply::getSiteId)
                .distinct()
                .collect(Collectors.toList());
        if (siteIds.isEmpty()) {
            return;
        }

        List<Site> siteList = siteMapper.selectList(new LambdaQueryWrapper<Site>()
                .in(Site::getSiteId, siteIds));
        for (Site site : siteList) {
            Set<String> merged = new LinkedHashSet<>(normalizeCategoryIds(site.getCategoryIds()));
            merged.add(String.valueOf(supplyCategoryId));
            Site update = new Site();
            update.setSiteId(site.getSiteId());
            update.setCategoryIds(JSONUtil.toJsonStr(new ArrayList<>(merged)));
            siteMapper.updateById(update);
        }
    }

    /**
     * O2O 分账 separ_data 中 site_separate 改为 o2o_separate，并更新分账明细 value_source。
     */
    public void handleSiteO2oSplitData() {
        String separTable = V120UpgradeHelper.table("separ_account");
        V120UpgradeHelper.execute("UPDATE `" + separTable + "` SET `separ_data` = REPLACE(`separ_data`, '\"site_separate\"', '\"o2o_separate\"') WHERE `trade_type` = 'o2o_order_trade'");

        SeparAccountMapper separAccountMapper = SpringContext.bean(SeparAccountMapper.class);
        SeparAccountDetailMapper separAccountDetailMapper = SpringContext.bean(SeparAccountDetailMapper.class);

        List<Integer> separIds = separAccountMapper.selectList(new LambdaQueryWrapper<SeparAccount>()
                        .eq(SeparAccount::getTradeType, "o2o_order_trade")
                        .select(SeparAccount::getId))
                .stream()
                .map(SeparAccount::getId)
                .collect(Collectors.toList());
        if (separIds.isEmpty()) {
            return;
        }

        separAccountDetailMapper.update(null, new LambdaUpdateWrapper<SeparAccountDetail>()
                .in(SeparAccountDetail::getSeparId, separIds)
                .ne(SeparAccountDetail::getMainId, 0)
                .set(SeparAccountDetail::getValueSource, "o2o_separate"));
    }

    /**
     * 初始化买单海报。
     */
    public void handleDiyData() {
        ICorePosterService corePosterService = SpringContext.bean(ICorePosterService.class);
        corePosterService.initDefaultPoster(0, "cash", "");
    }

    /**
     * 聚合 shop/o2o/supply 的 business_status 写入 site.business_state。
     */
    public void handleSiteBusinessStatusData() {
        SiteMapper siteMapper = SpringContext.bean(SiteMapper.class);
        SiteShopMapper siteShopMapper = SpringContext.bean(SiteShopMapper.class);
        SiteO2oMapper siteO2oMapper = SpringContext.bean(SiteO2oMapper.class);
        SiteSupplyMapper siteSupplyMapper = SpringContext.bean(SiteSupplyMapper.class);

        List<Site> siteList = siteMapper.selectList(new LambdaQueryWrapper<Site>()
                .gt(Site::getSiteId, 0));
        for (Site site : siteList) {
            Integer siteId = site.getSiteId();
            int businessStatus = 0;

            SiteShop shop = siteShopMapper.selectOne(new LambdaQueryWrapper<SiteShop>()
                    .eq(SiteShop::getSiteId, siteId)
                    .last("LIMIT 1"));
            if (shop != null && shop.getBusinessStatus() != null) {
                businessStatus = Math.max(shop.getBusinessStatus(), businessStatus);
            }

            SiteO2o o2oShop = siteO2oMapper.selectOne(new LambdaQueryWrapper<SiteO2o>()
                    .eq(SiteO2o::getSiteId, siteId)
                    .last("LIMIT 1"));
            if (o2oShop != null && o2oShop.getBusinessStatus() != null) {
                businessStatus = Math.max(o2oShop.getBusinessStatus(), businessStatus);
            }

            SiteSupply supply = siteSupplyMapper.selectOne(new LambdaQueryWrapper<SiteSupply>()
                    .eq(SiteSupply::getSiteId, siteId)
                    .last("LIMIT 1"));
            if (supply != null && supply.getBusinessStatus() != null) {
                businessStatus = Math.max(supply.getBusinessStatus(), businessStatus);
            }

            Site update = new Site();
            update.setSiteId(siteId);
            update.setBusinessState(businessStatus);
            siteMapper.updateById(update);
        }
    }

    /**
     * DIY 页 MemberLevel 组件 style-5 改为 style-3。
     */
    public void handleDiyPageData() {
        DiyPageMapper diyPageMapper = SpringContext.bean(DiyPageMapper.class);
        List<DiyPage> list = diyPageMapper.selectList(new LambdaQueryWrapper<DiyPage>()
                .ne(DiyPage::getValue, "")
                .isNotNull(DiyPage::getValue)
                .like(DiyPage::getType, "MEMBER_INDEX"));
        for (DiyPage page : list) {
            if (!StringUtils.hasText(page.getValue())) {
                continue;
            }
            JSONObject diyData = JSONUtil.parseObj(page.getValue());
            JSONArray components = diyData.getJSONArray("value");
            if (components == null || components.isEmpty()) {
                continue;
            }
            boolean changed = false;
            for (int i = 0; i < components.size(); i++) {
                JSONObject item = components.getJSONObject(i);
                if (item == null || !"MemberLevel".equals(item.getStr("componentName"))) {
                    continue;
                }
                if ("style-5".equals(item.getStr("style"))) {
                    item.set("style", "style-3");
                    changed = true;
                }
                if ("风格5".equals(item.getStr("styleName"))) {
                    item.set("styleName", "风格3");
                    changed = true;
                }
            }
            if (changed) {
                DiyPage update = new DiyPage();
                update.setId(page.getId());
                update.setValue(diyData.toString());
                diyPageMapper.updateById(update);
            }
        }
    }

    private List<String> normalizeCategoryIds(String categoryIdsJson) {
        if (!StringUtils.hasText(categoryIdsJson)) {
            return new ArrayList<>();
        }
        try {
            JSONArray array = JSONUtil.parseArray(categoryIdsJson);
            List<String> result = new ArrayList<>();
            for (int i = 0; i < array.size(); i++) {
                result.add(String.valueOf(array.get(i)));
            }
            return result;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
