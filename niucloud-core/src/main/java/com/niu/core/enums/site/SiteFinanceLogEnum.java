package com.niu.core.enums.site;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 站点财务日志字典枚举
 */
@Getter
@AllArgsConstructor
public enum SiteFinanceLogEnum {
    /**************************************************** 业务类型 ************************************************************/

    ORDER_TRADE("订单交易", "order_trade"),
    ORDER_REFUND("订单退款", "order_refund"),

    /**************************************************** 平台手续费（商户） ************************************************************/
    PLAT_SERVICE("支付手续费", "plat_service"),
    PLAT_REFUND_SERVICE("退还手续费", "plat_refund_service"),

    /**************************************************** 平台佣金（平台） ************************************************************/
    PLAT_COMMISSION("平台佣金", "plat_commission"),
    PLAT_REFUND_COMMISSION("平台退还佣金", "plat_refund_commission"),
    CIRCLE_COMMISSION("商圈佣金", "circle_commission"),
    CIRCLE_REFUND_COMMISSION("退还商圈佣金", "circle_refund_commission"),

    /**************************************************** 商户佣金（商户） ************************************************************/
    SITE_COMMISSION("商户佣金", "site_commission"),
    SITE_REFUND_COMMISSION("商户退还佣金", "site_refund_commission"),

    /***************************************************** 平台优惠券补贴（平台） ***********************************************************/
    PLAT_COUPON("平台优惠券补贴", "plat_coupon"),
    PLAT_REFUND_COUPON("退还平台优惠券补贴", "plat_refund_coupon"),

    /***************************************************** 退款金额 ***********************************************************/
    PLAT_REFUND("平台退款", "plat_refund"),
    SITE_REFUND("商户退款", "site_refund"),
    SUPPLY_REFUND("供货商退款", "supply_refund"),

    /***************************************************** 商户分账（平台） ***********************************************************/
    SITE_SEPARATE("支付分账(商城)", "site_separate"),
    SITE_REFUND_SEPARATE("退款分账(商城)", "site_refund_separate"),

    SUPPLY_SEPARATE("支付分账(供货商)", "supply_separate"),
    SUPPLY_REFUND_SEPARATE("退款分账(供货商)", "supply_refund_separate"),

    O2O_SEPARATE("支付分账(o2o)", "o2o_separate"),//支付分账(o2o)
    O2O_REFUND_SEPARATE("退款分账(o2o)","o2o_refund_separate"),//退款分账(o2o)

    PLAT_SEPARATE("支付分账(平台)", "plat_separate"),

    /***************************************************** 供货商货款（平台） ***********************************************************/
    PLAT_SUPPLY_MONEY("平台支付供货商货款", "plat_supply_money"),
    PLAT_SUPPLY_REFUND_MONEY("平台退还供货商货款", "plat_supply_refund_money"),

    /***************************************************** 供货商货款（商户） ***********************************************************/
    SITE_SUPPLY_MONEY("商户支付供货商货款", "site_supply_money"),
    SITE_SUPPLY_REFUND_MONEY("商户退还供货商货款", "site_supply_refund_money"),

    /***************************************************** 余额流水（平台） ***********************************************************/
    BALANCE_PAY("余额支付", "balance_pay"),
    BALANCE_REFUND("余额退款", "balance_refund"),

    /***************************************************** 公共 ***********************************************************/
    PAY("交易支付", "pay"),
    INCOME("收入", "income"),
    OUTLAY("支出", "outlay");
    private final String name;
    private final String source;

    public static Map<String, String> getMap(){
        Map<String, String> map = new HashMap<>();
        for(SiteFinanceLogEnum item : SiteFinanceLogEnum.values()){
            map.put(item.getSource(), item.getName());
        }
        return map;
    }

    public static Map<String, String> getType(){
        Map<String, String> map = new HashMap<>();
        for(SiteFinanceLogEnum item : SiteFinanceLogEnum.values()){
            if (item.getSource().equals(SiteFinanceLogEnum.ORDER_TRADE.getSource()) ||
                    item.getSource().equals(SiteFinanceLogEnum.ORDER_REFUND.getSource()) ||
                    item.getSource().equals(SiteFinanceLogEnum.INCOME.getSource()) || item.getSource().equals(SiteFinanceLogEnum.OUTLAY.getSource())){
                continue;
            }
            map.put(item.getSource(), item.getName());
        }
        return map;
    }

    public static Map<String, String> getTradeType(){
        Map<String, String> map = new HashMap<>();
        map.put(SiteFinanceLogEnum.ORDER_TRADE.getSource(), SiteFinanceLogEnum.ORDER_TRADE.getName());
        map.put(SiteFinanceLogEnum.ORDER_REFUND.getSource(), SiteFinanceLogEnum.ORDER_REFUND.getName());
        return map;
    }

    public static Map<String, String> getRaeType(){
        Map<String, String> map = new HashMap<>();
        map.put(SiteFinanceLogEnum.INCOME.getSource(), SiteFinanceLogEnum.INCOME.getName());
        map.put(SiteFinanceLogEnum.OUTLAY.getSource(), SiteFinanceLogEnum.OUTLAY.getName());
        return map;
    }

    public static String getName(String source){
        return getMap().get(source);
    }
}
