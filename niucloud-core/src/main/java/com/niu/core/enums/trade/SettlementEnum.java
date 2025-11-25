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

}
