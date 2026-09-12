package com.niu.core.enums.cash;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 买单订单日志操作人类型
 */
@Getter
@AllArgsConstructor
public enum CashOrderLogEnum {

    ADMIN("admin"),
    MEMBER("member"),
    SYSTEM("system");

    private final String code;
}
