package com.niu.core.enums.trade;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 分账明细来源类型
 */
@Getter
@AllArgsConstructor
public enum SpearValueSourceEnum {
    SITE_SEPARATE("site_separate", "商城分账"),
    O2O_SEPARATE("o2o_separate", "O2O分账"),
    SUPPLY_SEPARATE("supply_separate", "供货商分账"),
    WAIMAI_SEPARATE("waimai_separate", "外卖分账")
    ;
    private final String code;
    private final String desc;
}
