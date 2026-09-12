package com.niu.core.upgrade.v120;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * site_shop 表 v1.2.0 迁移用字段快照。
 * <p>
 * v120 站点分表后，{@link com.niu.core.entity.shop.SiteShop} 已剥离联系人、资金、提现账户、供货商等旧列；
 * 升级时通过 {@link V120UpgradeHelper#listLegacyShops()} 动态 SELECT 仍存在的列并映射到本类。
 * </p>
 */
@Data
public class SiteShopLegacy {

    /** 站点 id */
    private Integer siteId;

    /** 联系人姓名（迁移至 site.contact_name） */
    private String contactName;

    /** 联系电话（迁移至 site.contact_mobile） */
    private String contactMobile;

    /** 当前余额（迁移至 site.money） */
    private BigDecimal money;

    /** 累计结算（迁移至 site.money_get） */
    private BigDecimal moneyGet;

    /** 提现中金额（迁移至 site.money_cash_outing） */
    private BigDecimal moneyCashOuting;

    /** 营业执照（迁移至 site.business_license） */
    private String businessLicense;

    /** 商户类目 id（迁移至 site.category_ids JSON） */
    private Integer categoryId;

    /** 子级行业类目 */
    private Integer childCategoryId;

    /** 营业状态 */
    private Integer businessStatus;

    /** 店铺关注数（旧列缺失时 supply_follow_num 兜底用） */
    private Integer followNumber;

    /** 店铺关键词 */
    private String keywords;

    /** 佣金比率（迁移至 site_shop.service_rate） */
    private BigDecimal commissionRate;

    /** 服务费比率（新列，旧库可能已有） */
    private BigDecimal serviceRate;

    /** 提现账户类型：1 银行卡 2 支付宝 3 微信 */
    private Integer bankType;

    /** 银行卡户名 */
    private String bankAccountName;

    /** 银行卡号 */
    private String bankAccountNo;

    /** 开户行名称 */
    private String bankName;

    /** 开户行地址 */
    private String bankAddress;

    /** 支付宝姓名 */
    private String alipayName;

    /** 支付宝账号 */
    private String alipayAccountNo;

    /** 支付宝收款码 */
    private String alipayPaymentCode;

    /** 微信姓名 */
    private String wechatName;

    /** 微信账号 */
    private String wechatAccountNo;

    /** 微信收款码 */
    private String wechatPaymentCode;

    /** 供货商服务费比率（迁移至 site_supply.service_rate） */
    private BigDecimal supplyServiceRate;

    /** 供货商商品销量 */
    private Integer supplySaleNum;

    /** 供货商上架商品数 */
    private Integer supplyUpNum;

    /** 供货商关注站点数 */
    private Integer supplyFollowNum;

    /** 供货商商品总数 */
    private Integer supplyGoodsNum;

    /**
     * 将 JDBC 查询行映射为快照对象（列不存在时对应字段为默认值）。
     */
    static SiteShopLegacy fromRow(Map<String, Object> row) {
        SiteShopLegacy legacy = new SiteShopLegacy();
        legacy.setSiteId(intVal(row.get("site_id")));
        legacy.setContactName(strVal(row.get("contact_name")));
        legacy.setContactMobile(strVal(row.get("contact_mobile")));
        legacy.setMoney(decimalVal(row.get("money")));
        legacy.setMoneyGet(decimalVal(row.get("money_get")));
        legacy.setMoneyCashOuting(decimalVal(row.get("money_cash_outing")));
        legacy.setBusinessLicense(strVal(row.get("business_license")));
        legacy.setCategoryId(intVal(row.get("category_id")));
        legacy.setChildCategoryId(intVal(row.get("child_category_id")));
        legacy.setBusinessStatus(intVal(row.get("business_status")));
        legacy.setFollowNumber(intVal(row.get("follow_number")));
        legacy.setKeywords(strVal(row.get("keywords")));
        legacy.setCommissionRate(decimalVal(row.get("commission_rate")));
        legacy.setServiceRate(decimalVal(row.get("service_rate")));
        legacy.setBankType(intVal(row.get("bank_type")));
        legacy.setBankAccountName(strVal(row.get("bank_account_name")));
        legacy.setBankAccountNo(strVal(row.get("bank_account_no")));
        legacy.setBankName(strVal(row.get("bank_name")));
        legacy.setBankAddress(strVal(row.get("bank_address")));
        legacy.setAlipayName(strVal(row.get("alipay_name")));
        legacy.setAlipayAccountNo(strVal(row.get("alipay_account_no")));
        legacy.setAlipayPaymentCode(strVal(row.get("alipay_payment_code")));
        legacy.setWechatName(strVal(row.get("wechat_name")));
        legacy.setWechatAccountNo(strVal(row.get("wechat_account_no")));
        legacy.setWechatPaymentCode(strVal(row.get("wechat_payment_code")));
        legacy.setSupplyServiceRate(decimalVal(row.get("supply_service_rate")));
        legacy.setSupplySaleNum(intVal(row.get("supply_sale_num")));
        legacy.setSupplyUpNum(intVal(row.get("supply_up_num")));
        legacy.setSupplyFollowNum(intVal(row.get("supply_follow_num")));
        legacy.setSupplyGoodsNum(intVal(row.get("supply_goods_num")));
        return legacy;
    }

    private static String strVal(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private static Integer intVal(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static BigDecimal decimalVal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        try {
            return new BigDecimal(String.valueOf(value));
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
