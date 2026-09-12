package com.niu.core.enums.trade;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SettlementEnum {

    WAIT("待结算", "wait"),
    DEALING("处理中", "dealing"),
    SETTLED("已结算", "settled"),
    FAIL("已结算", "fail")
    ;


    private final String name;
    private final String code;

    public static String getNameByCode(String code) {
        if (code == null || code.isEmpty()) {
            return "";
        }
        for (SettlementEnum settlementEnum : values()) {
            if (settlementEnum.getCode().equals(code)) {
                return settlementEnum.getName();
            }
        }
        return "";
    }

}
