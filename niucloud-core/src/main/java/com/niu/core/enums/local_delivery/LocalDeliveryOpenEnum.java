package com.niu.core.enums.local_delivery;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 同城配送开通状态
 */
@Getter
@AllArgsConstructor
public enum LocalDeliveryOpenEnum {

    OPEN_PASS(1, "通过"),
    OPEN_REFUND(2, "未通过");

    private final Integer status;
    private final String value;


}