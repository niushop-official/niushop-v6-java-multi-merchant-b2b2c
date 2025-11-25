package com.niu.core.enums.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 插件状态枚举类
 */
@Getter
@AllArgsConstructor
public enum SeparValueSourceEnum {
    ORDER_TRADE("订单交易", "order_trade"),
    ORDER_REFUND("订单退款", "order_refund"),
    PLAT_SERVICE("支付手续费", "plat_service"),
    PLAT_REFUND_SERVICE("退还手续费", "plat_refund_service"),
    PLAT_COMMISSION("平台佣金", "plat_commission"),
    PLAT_REFUND_COMMISSION("平台退还佣金", "plat_refund_commission"),
    SITE_COMMISSION("商户佣金", "site_commission"),
    SITE_REFUND_COMMISSION("商户退还佣金", "site_refund_commission"),
    PLAT_COUPON("平台优惠券补贴", "plat_coupon"),
    PLAT_REFUND_COUPON("退还平台优惠券补贴", "plat_refund_coupon"),
    PLAT_REFUND("平台退款", "plat_refund"),
    SITE_REFUND("商户退款", "site_refund"),
    SITE_SEPARATE("支付分账", "site_separate"),
    SITE_REFUND_SEPARATE("退款分账", "site_refund_separate"),
    BALANCE_PAY("余额支付", "balance_pay"),
    BALANCE_REFUND("余额退款", "balance_refund"),
    PAY("交易支付", "pay"),
    INCOME("收入","income"),
    OUTLAY("支出","outlay")
    ;
    private final String name;
    private final String source;

    public static Map<String, String> getMap(){
        Map<String, String> map = new HashMap<>();
        for(SeparValueSourceEnum item : SeparValueSourceEnum.values()){
            map.put(item.getSource(), item.getName());
        }
        return map;
    }

    public static String getName(String source){
        return getMap().get(source);
    }
}
