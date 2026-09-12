package com.niu.core.upgrade.v120;

import cn.hutool.json.JSONUtil;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.entity.site.Site;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * v1.2.0 核心升级辅助工具。
 * <p>
 * 负责表前缀解析、information_schema 检测、幂等 DDL，以及读取 site_shop 旧列数据。
 * 所有 DDL/查询均按列/表是否存在做防护，以支持重复执行升级与不同阶段的库结构。
 * </p>
 */
public final class V120UpgradeHelper {

    /**
     * v120 结束后从 site_shop 剥离的废弃列（对齐 PHP handleDelDiscardTableAndField）
     */
    private static final String[] SITE_SHOP_LEGACY_COLUMNS = {
            "contact_name", "contact_mobile", "commission_rate", "money", "money_get", "money_cash_outing",
            "bank_type", "bank_account_name", "bank_account_no", "bank_name", "bank_address",
            "alipay_name", "alipay_account_no", "alipay_payment_code",
            "wechat_name", "wechat_account_no", "wechat_payment_code", "business_license",
            "supply_service_rate", "supply_sale_num", "supply_up_num", "supply_follow_num", "supply_goods_num"
    };

    private V120UpgradeHelper() {
    }

    /** 拼接带前缀的物理表名，如 nc_site */
    public static String table(String baseName) {
        String prefix = GlobalConfig.tablePrefix;
        if (!prefix.endsWith("_")) {
            prefix = prefix + "_";
        }
        return prefix + baseName;
    }

    public static JdbcTemplate jdbc() {
        return SpringContext.bean(JdbcTemplate.class);
    }

    /** 当前库中是否存在指定表 */
    public static boolean tableExists(String baseName) {
        String sql = "SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ?";
        Integer count = jdbc().queryForObject(sql, Integer.class, table(baseName));
        return count != null && count > 0;
    }

    /** 指定表是否存在某列 */
    public static boolean hasColumn(String baseName, String column) {
        String sql = "SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?";
        Integer count = jdbc().queryForObject(sql, Integer.class, table(baseName), column);
        return count != null && count > 0;
    }

    public static void execute(String sql) {
        jdbc().execute(sql);
    }

    /** 列不存在时执行 ADD COLUMN */
    public static void addColumnIfNotExists(String baseName, String column, String ddl) {
        if (!hasColumn(baseName, column)) {
            execute(ddl);
        }
    }

    /** 列存在时执行 DROP COLUMN */
    public static void dropColumnIfExists(String baseName, String column) {
        if (hasColumn(baseName, column)) {
            execute("ALTER TABLE `" + table(baseName) + "` DROP COLUMN `" + column + "`");
        }
    }

    /** 表存在时执行 DROP TABLE */
    public static void dropTableIfExists(String baseName) {
        if (tableExists(baseName)) {
            execute("DROP TABLE IF EXISTS `" + table(baseName) + "`");
        }
    }

    /**
     * 幂等补全 site 表 v120 新增字段（对齐 PHP handleSiteData 中的 ALTER TABLE site）
     */
    public static void ensureSiteColumns() {
        String siteTable = table("site");
        addColumnIfNotExists("site", "contact_name",
                "ALTER TABLE `" + siteTable + "` ADD COLUMN `contact_name` varchar(255) NOT NULL DEFAULT '' COMMENT '联系人姓名'");
        addColumnIfNotExists("site", "contact_mobile",
                "ALTER TABLE `" + siteTable + "` ADD COLUMN `contact_mobile` varchar(30) NOT NULL DEFAULT '' COMMENT '联系电话'");
        addColumnIfNotExists("site", "money",
                "ALTER TABLE `" + siteTable + "` ADD COLUMN `money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '当前余额'");
        addColumnIfNotExists("site", "money_get",
                "ALTER TABLE `" + siteTable + "` ADD COLUMN `money_get` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '累计结算'");
        addColumnIfNotExists("site", "money_cash_outing",
                "ALTER TABLE `" + siteTable + "` ADD COLUMN `money_cash_outing` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '提现中'");
        addColumnIfNotExists("site", "business_license",
                "ALTER TABLE `" + siteTable + "` ADD COLUMN `business_license` varchar(2000) NOT NULL DEFAULT '' COMMENT '营业执照'");
        addColumnIfNotExists("site", "category_ids",
                "ALTER TABLE `" + siteTable + "` ADD COLUMN `category_ids` varchar(255) NOT NULL DEFAULT '' COMMENT '扩展表中所选行业类目集合'");
    }

    /** 批量删除 site_shop 上已迁移的废弃列 */
    public static void dropSiteShopLegacyColumns() {
        for (String column : SITE_SHOP_LEGACY_COLUMNS) {
            dropColumnIfExists("site_shop", column);
        }
    }

    /** 等价 PHP check_addon('supply') */
    public static boolean isSupplyInstalled() {
        ICoreAddonService coreAddonService = SpringContext.bean(ICoreAddonService.class);
        Map<String, InstallAddonListVo> installAddonList = coreAddonService.getInstallAddonList();
        return installAddonList != null && installAddonList.containsKey("supply");
    }

    /** site.app JSON 是否包含指定应用 key */
    public static boolean siteAppContains(Site site, String appKey) {
        if (site == null || !StringUtils.hasText(site.getApp())) {
            return false;
        }
        return site.getApp().contains("\"" + appKey + "\"");
    }

    /** site.app 是否与给定应用列表 JSON 完全一致 */
    public static boolean siteAppEquals(Site site, List<String> apps) {
        if (site == null || !StringUtils.hasText(site.getApp())) {
            return false;
        }
        return site.getApp().equals(JSONUtil.toJsonStr(apps));
    }

    /**
     * 读取 site_shop 迁移用数据：仅 SELECT 当前库仍存在的列，映射为 {@link SiteShopLegacy}。
     */
    public static List<SiteShopLegacy> listLegacyShops() {
        if (!tableExists("site_shop")) {
            return Collections.emptyList();
        }
        List<String> columns = new ArrayList<>();
        columns.add("site_id");
        columns.add("category_id");
        columns.add("child_category_id");
        columns.add("business_status");
        columns.add("follow_number");
        columns.add("keywords");
        columns.add("service_rate");
        for (String column : SITE_SHOP_LEGACY_COLUMNS) {
            if (hasColumn("site_shop", column) && !columns.contains(column)) {
                columns.add(column);
            }
        }
        String sql = "SELECT " + String.join(", ", columns.stream().map(c -> "`" + c + "`").toList())
                + " FROM `" + table("site_shop") + "` WHERE `site_id` > 0";
        List<Map<String, Object>> rows = jdbc().queryForList(sql);
        List<SiteShopLegacy> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            result.add(SiteShopLegacy.fromRow(row));
        }
        return result;
    }

    /**
     * 读取 site.keywords（v120 清理前从 site 表迁移至各扩展表）
     */
    public static String getSiteKeywords(Integer siteId) {
        if (siteId == null || siteId <= 0 || !hasColumn("site", "keywords")) {
            return "";
        }
        String sql = "SELECT `keywords` FROM `" + table("site") + "` WHERE `site_id` = ? LIMIT 1";
        List<Map<String, Object>> rows = jdbc().queryForList(sql, siteId);
        if (rows.isEmpty() || rows.get(0).get("keywords") == null) {
            return "";
        }
        return String.valueOf(rows.get(0).get("keywords"));
    }
}
