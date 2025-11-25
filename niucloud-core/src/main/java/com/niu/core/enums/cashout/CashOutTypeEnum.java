package com.niu.core.enums.cashout;

/**
 * 提现类型
 */
public enum CashOutTypeEnum {

    MEMBER_CASH_OUT("member_cash_out"),
    SHOP_CASH_OUT("shop_cash_out");
    String name;
    CashOutTypeEnum(String name){
        this.name=name;
    }

    public String getName() {
        return name;
    }
}
