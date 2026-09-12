package com.niu.core.upgrade.v120;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.upgrade.IUpgradeProvider;
import com.niu.core.common.component.upgrade.annotation.UpgradeProvider;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.goods.GoodsService;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.entity.shop.SiteShopApply;
import com.niu.core.entity.shop.SiteShopCategory;
import com.niu.core.entity.shop.SiteShopMember;
import com.niu.core.entity.shop.SiteSupply;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteApply;
import com.niu.core.entity.site.SiteCategory;
import com.niu.core.entity.site.SiteSettAccount;
import com.niu.core.entity.sys.SysConfig;
import com.niu.core.enums.addon.AddonActionEnum;
import com.niu.core.enums.addon.AddonStatusEnum;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.goods.GoodsServiceMapper;
import com.niu.core.mapper.shop.SiteShopApplyMapper;
import com.niu.core.mapper.shop.SiteShopCategoryMapper;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.mapper.shop.SiteShopMemberMapper;
import com.niu.core.mapper.shop.SiteSupplyMapper;
import com.niu.core.mapper.site.SiteApplyMapper;
import com.niu.core.mapper.site.SiteCategoryMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.mapper.site.SiteSettAccountMapper;
import com.niu.core.mapper.sys.SysConfigMapper;
import com.niu.core.service.core.shop.ICoreShopSiteGroupService;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 核心框架 v1.2.0 数据迁移。
 * <p>
 * 在 {@code 1.2.0.sql} 执行 DDL 之后运行，完成站点分表架构下的历史数据搬迁与清理。
 * 各步骤均幂等：源表/源列不存在或目标数据已存在时自动跳过。
 * </p>
 * <p>
 * 执行顺序须与 PHP 一致；{@link #handleDelDiscardTableAndField()} 必须最后执行。
 * </p>
 * <p>
 * 仅清理平台级 {@code diy_bottom_app}（site_id=0）。
 * 站点级及各插件 {@code diy_bottom_{key}} 不删，商户在后台保存的底栏配置会保留。
 * </p>
 */
@UpgradeProvider(addon = "core", version = "1.2.0")
public class Upgrade extends IUpgradeProvider {

    @Override
    public void handle() {
        // 顺序对齐
        handleGoodsServiceData();
        handleSiteCategoryData();
        handleSiteSettAccountData();
        handleSiteData();
        handleSiteSupplyData();
        handleSiteShopData();
        handleSiteShopApplyData();
        handleSiteShopMemberData();
        handleAppDiyBottom();
        handleAppGroupData();
        handleSiteShopDataDelOnlySupply();
        handleDelDiscardTableAndField();
    }

    /**
     * 空 addon 写入 JSON 数组，默认含 mall，已装 supply 时追加。
     */
    public void handleGoodsServiceData() {
        List<String> addonList = new ArrayList<>();
        addonList.add("mall");
        if (V120UpgradeHelper.isSupplyInstalled()) {
            addonList.add("supply");
        }
        String addonValue = JSONUtil.toJsonStr(addonList);
        GoodsServiceMapper goodsServiceMapper = SpringContext.bean(GoodsServiceMapper.class);
        goodsServiceMapper.update(new GoodsService(), new LambdaUpdateWrapper<GoodsService>()
                .and(w -> w.eq(GoodsService::getAddon, "").or().isNull(GoodsService::getAddon))
                .set(GoodsService::getAddon, addonValue)
        );
    }

    /**
     * 旧表 site_shop_category 行业数据迁入 site_category。
     */
    public void handleSiteCategoryData() {
        if (!V120UpgradeHelper.tableExists("site_shop_category")) {
            return;
        }
        SiteShopCategoryMapper shopCategoryMapper = SpringContext.bean(SiteShopCategoryMapper.class);
        SiteCategoryMapper siteCategoryMapper = SpringContext.bean(SiteCategoryMapper.class);
        List<SiteShopCategory> list = shopCategoryMapper.selectList(new LambdaQueryWrapper<SiteShopCategory>()
                .gt(SiteShopCategory::getCategoryId, 0));
        if (list.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis() / 1000;
        for (SiteShopCategory item : list) {
            Long count = siteCategoryMapper.selectCount(new LambdaQueryWrapper<SiteCategory>()
                    .eq(SiteCategory::getCategoryId, item.getCategoryId()));
            if (count != null && count > 0) {
                continue;
            }
            SiteCategory category = new SiteCategory();
            category.setCategoryId(item.getCategoryId());
            category.setName(item.getCategoryName());
            category.setLevel(1);
            category.setPid(0);
            category.setSort(item.getSort() != null ? item.getSort() : 0);
            category.setStatus(1);
            category.setAddon("mall");
            category.setServiceRate(item.getCommissionRate() != null ? item.getCommissionRate() : BigDecimal.ZERO);
            category.setCreateTime(item.getCreateTime() != null && item.getCreateTime() > 0 ? item.getCreateTime() : now);
            category.setUpdateTime(item.getUpdateTime() != null && item.getUpdateTime() > 0 ? item.getUpdateTime() : now);
            siteCategoryMapper.insert(category);
        }
    }

    /**
     * 按 bank_type(1银行卡/2支付宝/3微信) 迁入 site_sett_account。
     */
    public void handleSiteSettAccountData() {
        if (!V120UpgradeHelper.hasColumn("site_shop", "bank_type")) {
            return;
        }
        SiteSettAccountMapper settAccountMapper = SpringContext.bean(SiteSettAccountMapper.class);
        long now = System.currentTimeMillis() / 1000;
        for (SiteShopLegacy shop : V120UpgradeHelper.listLegacyShops()) {
            if (shop.getSiteId() == null || shop.getSiteId() <= 0 || shop.getBankType() == null || shop.getBankType() <= 0) {
                continue;
            }
            Long exists = settAccountMapper.selectCount(new LambdaQueryWrapper<SiteSettAccount>()
                    .eq(SiteSettAccount::getMainType, "site")
                    .eq(SiteSettAccount::getMainId, shop.getSiteId())
                    .eq(SiteSettAccount::getType, shop.getBankType()));
            if (exists != null && exists > 0) {
                continue;
            }
            SiteSettAccount account = buildSettAccount(shop, now);
            if (account == null) {
                continue;
            }
            settAccountMapper.insert(account);
        }
    }

    /** 姓名为空时返回 null（与 PHP continue 逻辑一致） */
    private SiteSettAccount buildSettAccount(SiteShopLegacy shop, long now) {
        String name = "";
        String accountNo = "";
        String accountImg = "";
        String bankName = "";
        String bankAddress = "";
        int bankType = shop.getBankType();
        if (bankType == 1) {
            if (!StringUtils.hasText(shop.getBankAccountName())) {
                return null;
            }
            name = shop.getBankAccountName();
            accountNo = shop.getBankAccountNo();
            bankName = shop.getBankName();
            bankAddress = shop.getBankAddress();
        } else if (bankType == 2) {
            if (!StringUtils.hasText(shop.getAlipayName())) {
                return null;
            }
            name = shop.getAlipayName();
            accountNo = shop.getAlipayAccountNo();
            accountImg = shop.getAlipayPaymentCode();
        } else if (bankType == 3) {
            if (!StringUtils.hasText(shop.getWechatName())) {
                return null;
            }
            name = shop.getWechatName();
            accountNo = shop.getWechatAccountNo();
            accountImg = shop.getWechatPaymentCode();
        } else {
            return null;
        }
        SiteSettAccount account = new SiteSettAccount();
        account.setMainType("site");
        account.setMainId(shop.getSiteId());
        account.setType(bankType);
        account.setName(name);
        account.setAccountNo(accountNo);
        account.setAccountImg(accountImg);
        account.setBankName(bankName);
        account.setBankAddress(bankAddress);
        account.setCreateTime(now);
        account.setUpdateTime(now);
        return account;
    }

    /**
     * 补全 site 新字段，并从 site_shop 回填联系人/资金/执照/category_ids。
     * 旧列已剥离时，仅尝试从 site_shop.category_id 补写 category_ids。
     */
    public void handleSiteData() {
        V120UpgradeHelper.ensureSiteColumns();
        SiteMapper siteMapper = SpringContext.bean(SiteMapper.class);
        boolean hasLegacyContact = V120UpgradeHelper.hasColumn("site_shop", "contact_name");
        if (hasLegacyContact) {
            for (SiteShopLegacy shop : V120UpgradeHelper.listLegacyShops()) {
                if (shop.getSiteId() == null || shop.getSiteId() <= 0) {
                    continue;
                }
                Site update = new Site();
                update.setSiteId(shop.getSiteId());
                update.setContactName(shop.getContactName());
                update.setContactMobile(shop.getContactMobile());
                update.setMoney(shop.getMoney());
                update.setMoneyGet(shop.getMoneyGet());
                update.setMoneyCashOuting(shop.getMoneyCashOuting());
                update.setBusinessLicense(shop.getBusinessLicense());
                if (shop.getCategoryId() != null && shop.getCategoryId() > 0) {
                    update.setCategoryIds(JSONUtil.toJsonStr(List.of(shop.getCategoryId())));
                } else {
                    update.setCategoryIds("");
                }
                siteMapper.updateById(update);
            }
            return;
        }
        // 旧列已剥离：仅从 site_shop.category_id 补写 site.category_ids
        SiteShopMapper siteShopMapper = SpringContext.bean(SiteShopMapper.class);
        List<SiteShop> shopList = siteShopMapper.selectList(new LambdaQueryWrapper<SiteShop>()
                .gt(SiteShop::getSiteId, 0));
        for (SiteShop shop : shopList) {
            if (shop.getSiteId() == null || shop.getSiteId() <= 0) {
                continue;
            }
            Site site = siteMapper.selectById(shop.getSiteId());
            if (site == null) {
                continue;
            }
            if (shop.getCategoryId() == null || shop.getCategoryId() <= 0) {
                continue;
            }
            if (StringUtils.hasText(site.getCategoryIds())) {
                continue;
            }
            Site update = new Site();
            update.setSiteId(site.getSiteId());
            update.setCategoryIds(JSONUtil.toJsonStr(List.of(shop.getCategoryId())));
            siteMapper.updateById(update);
        }
    }

    /**
     * 已装 supply 且 app 含 supply 的站点，迁入 site_supply 扩展表。
     */
    public void handleSiteSupplyData() {
        if (!V120UpgradeHelper.isSupplyInstalled()) {
            return;
        }
        SiteMapper siteMapper = SpringContext.bean(SiteMapper.class);
        SiteSupplyMapper siteSupplyMapper = SpringContext.bean(SiteSupplyMapper.class);
        List<Site> supplySites = siteMapper.selectList(new LambdaQueryWrapper<Site>()
                .gt(Site::getSiteId, 0)
                .like(Site::getApp, "%\"supply\"%"));
        if (supplySites.isEmpty()) {
            return;
        }
        Map<Integer, Site> siteMap = new HashMap<>();
        for (Site site : supplySites) {
            siteMap.put(site.getSiteId(), site);
        }
        boolean hasSupplyCols = V120UpgradeHelper.hasColumn("site_shop", "supply_service_rate");
        for (SiteShopLegacy shop : V120UpgradeHelper.listLegacyShops()) {
            if (shop.getSiteId() == null || !siteMap.containsKey(shop.getSiteId())) {
                continue;
            }
            Long count = siteSupplyMapper.selectCount(new LambdaQueryWrapper<SiteSupply>()
                    .eq(SiteSupply::getSiteId, shop.getSiteId()));
            if (count != null && count > 0) {
                continue;
            }
            Site site = siteMap.get(shop.getSiteId());
            SiteSupply supply = new SiteSupply();
            supply.setSiteId(shop.getSiteId());
            if (hasSupplyCols) {
                supply.setServiceRate(shop.getSupplyServiceRate());
                supply.setSupplySaleNum(shop.getSupplySaleNum());
                supply.setSupplyUpNum(shop.getSupplyUpNum());
                supply.setSupplyFollowNum(shop.getSupplyFollowNum());
                supply.setSupplyGoodsNum(shop.getSupplyGoodsNum());
            } else {
                // supply_* 列已删时用 service_rate、follow_number 兜底
                supply.setServiceRate(shop.getServiceRate() != null ? shop.getServiceRate() : BigDecimal.ZERO);
                supply.setSupplySaleNum(0);
                supply.setSupplyUpNum(0);
                supply.setSupplyFollowNum(shop.getFollowNumber() != null ? shop.getFollowNumber() : 0);
                supply.setSupplyGoodsNum(0);
            }
            supply.setBusinessStatus(shop.getBusinessStatus() != null ? shop.getBusinessStatus() : 1);
            supply.setCategoryId(shop.getCategoryId() != null ? shop.getCategoryId() : 0);
            supply.setChildCategoryId(shop.getChildCategoryId() != null ? shop.getChildCategoryId() : 0);
            String keywords = V120UpgradeHelper.getSiteKeywords(site.getSiteId());
            if (!StringUtils.hasText(keywords)) {
                keywords = shop.getKeywords();
            }
            supply.setKeywords(keywords != null ? keywords : "");
            siteSupplyMapper.insert(supply);
        }
    }

    /**
     * app 含 mall 的站点，commission_rate 写入 service_rate，keywords 从 site 迁入。
     */
    public void handleSiteShopData() {
        SiteMapper siteMapper = SpringContext.bean(SiteMapper.class);
        SiteShopMapper siteShopMapper = SpringContext.bean(SiteShopMapper.class);
        List<Site> mallSites = siteMapper.selectList(new LambdaQueryWrapper<Site>()
                .gt(Site::getSiteId, 0)
                .like(Site::getApp, "%\"mall\"%"));
        if (mallSites.isEmpty()) {
            return;
        }
        Map<Integer, Site> siteMap = new HashMap<>();
        for (Site site : mallSites) {
            siteMap.put(site.getSiteId(), site);
        }
        boolean hasCommissionRate = V120UpgradeHelper.hasColumn("site_shop", "commission_rate");
        boolean hasSiteKeywords = V120UpgradeHelper.hasColumn("site", "keywords");
        for (SiteShopLegacy shop : V120UpgradeHelper.listLegacyShops()) {
            if (shop.getSiteId() == null || !siteMap.containsKey(shop.getSiteId())) {
                continue;
            }
            SiteShop update = new SiteShop();
            update.setSiteId(shop.getSiteId());
            boolean needUpdate = false;
            if (hasCommissionRate && shop.getCommissionRate() != null) {
                update.setServiceRate(shop.getCommissionRate());
                needUpdate = true;
            }
            if (hasSiteKeywords) {
                String keywords = V120UpgradeHelper.getSiteKeywords(shop.getSiteId());
                if (StringUtils.hasText(keywords)) {
                    update.setKeywords(keywords);
                    needUpdate = true;
                }
            }
            if (!needUpdate) {
                continue;
            }
            siteShopMapper.update(update, new LambdaUpdateWrapper<SiteShop>()
                    .eq(SiteShop::getSiteId, shop.getSiteId()));
        }
    }

    /**
     *site_shop_apply 迁入 site_apply，addon=mall，apply_type=member。
     */
    public void handleSiteShopApplyData() {
        if (!V120UpgradeHelper.tableExists("site_shop_apply")) {
            return;
        }
        SiteShopApplyMapper shopApplyMapper = SpringContext.bean(SiteShopApplyMapper.class);
        SiteApplyMapper siteApplyMapper = SpringContext.bean(SiteApplyMapper.class);
        List<SiteShopApply> list = shopApplyMapper.selectList(new LambdaQueryWrapper<SiteShopApply>()
                .gt(SiteShopApply::getApplyId, 0));
        if (list.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis() / 1000;
        for (SiteShopApply item : list) {
            Long count = siteApplyMapper.selectCount(new LambdaQueryWrapper<SiteApply>()
                    .eq(SiteApply::getApplyId, item.getApplyId()));
            if (count != null && count > 0) {
                continue;
            }
            SiteApply apply = new SiteApply();
            apply.setApplyId(item.getApplyId());
            apply.setSiteId(item.getSiteId());
            apply.setMemberId(item.getMemberId());
            apply.setSiteName(item.getSiteName());
            apply.setUserName(item.getUserName());
            apply.setUserMobile(item.getUserMobile());
            apply.setCategoryId(item.getCategoryId() != null ? item.getCategoryId() : 0);
            apply.setChildCategoryId(0);
            apply.setBusinessLicense(item.getBusinessLicense());
            apply.setStatus(item.getStatus());
            apply.setCreateTime(item.getCreateTime() != null && item.getCreateTime() > 0 ? item.getCreateTime() : now);
            apply.setUpdateTime(item.getUpdateTime() != null && item.getUpdateTime() > 0 ? item.getUpdateTime() : now);
            apply.setAuditTime(item.getAuditTime() != null && item.getAuditTime() > 0 ? item.getAuditTime() : now);
            apply.setRefuseReason(item.getRefuseReason());
            apply.setOpenStatus(item.getOpenStatus() != null ? item.getOpenStatus() : 1);
            apply.setAccount(item.getAccount());
            apply.setPassword(item.getPassword());
            apply.setUid(item.getUid() != null ? item.getUid() : 0);
            apply.setAddon("mall");
            apply.setApplyType("member");
            apply.setFormRecordId(item.getFormRecordId() != null ? item.getFormRecordId() : 0);
            apply.setCircleId(0);
            siteApplyMapper.insert(apply);
        }
    }

    /**
     * 空 addon 的店铺会员标记为 mall。
     */
    public void handleSiteShopMemberData() {
        SiteShopMemberMapper siteShopMemberMapper = SpringContext.bean(SiteShopMemberMapper.class);
        siteShopMemberMapper.update(new SiteShopMember(), new LambdaUpdateWrapper<SiteShopMember>()
                .gt(SiteShopMember::getSiteId, 0)
                .and(w -> w.eq(SiteShopMember::getAddon, "").or().isNull(SiteShopMember::getAddon))
                .set(SiteShopMember::getAddon, "mall")
        );
    }

    /**
     * 对齐 PHP handleAppDiyBottom：删除平台级旧底栏配置 {@code diy_bottom_app}（site_id=0）。
     * <p>
     * 删后平台 TabBar 回退 {@code diy/bottom.json} 默认（含 v120 新增 Tab 结构）。
     * 仅影响平台总后台改过的「系统」底栏；不删 site_id&gt;0 的站点底栏，也不删 {@code diy_bottom_mall} 等插件键。
     * </p>
     */
    public void handleAppDiyBottom() {
        SysConfigMapper sysConfigMapper = SpringContext.bean(SysConfigMapper.class);
        sysConfigMapper.delete(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getSiteId, 0)
                .eq(SysConfig::getConfigKey, "diy_bottom_app")
        );
    }

    /**
     * Java 独有：为已安装应用补齐 create_by=app 默认套餐，并同步系统商户套餐。
     */
    public void handleAppGroupData() {
        ICoreShopSiteGroupService coreShopSiteGroupService = SpringContext.bean(ICoreShopSiteGroupService.class);
        AddonMapper addonMapper = SpringContext.bean(AddonMapper.class);
        List<Addon> apps = addonMapper.selectList(new LambdaQueryWrapper<Addon>()
                .eq(Addon::getStatus, AddonStatusEnum.ON.getCode())
                .eq(Addon::getType, AddonActionEnum.APP.getCode())
                .select(Addon::getKey));
        for (Addon app : apps) {
            coreShopSiteGroupService.createAppGroup(app.getKey());
        }
        coreShopSiteGroupService.updateSysGroup();
    }

    /**
     * app 仅为 ["supply"] 的站点删除 site_shop 行（须在数据迁移之后）。
     */
    public void handleSiteShopDataDelOnlySupply() {
        SiteMapper siteMapper = SpringContext.bean(SiteMapper.class);
        SiteShopMapper siteShopMapper = SpringContext.bean(SiteShopMapper.class);
        String supplyApp = JSONUtil.toJsonStr(List.of("supply"));
        List<Site> supplyOnlySites = siteMapper.selectList(new LambdaQueryWrapper<Site>()
                .gt(Site::getSiteId, 0)
                .eq(Site::getApp, supplyApp));
        if (supplyOnlySites.isEmpty()) {
            return;
        }
        List<Integer> siteIds = supplyOnlySites.stream().map(Site::getSiteId).toList();
        siteShopMapper.delete(new LambdaQueryWrapper<SiteShop>()
                .in(SiteShop::getSiteId, siteIds));
    }

    /**
     * 删除 site_shop_apply/site_shop_category 表及 site、site_shop 废弃列。
     * 必须放在最后一步执行。
     */
    public void handleDelDiscardTableAndField() {
        V120UpgradeHelper.dropTableIfExists("site_shop_apply");
        V120UpgradeHelper.dropTableIfExists("site_shop_category");
        V120UpgradeHelper.dropColumnIfExists("site", "keywords");
        V120UpgradeHelper.dropSiteShopLegacyColumns();
    }
}
