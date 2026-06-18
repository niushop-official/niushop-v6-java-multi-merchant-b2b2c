package com.niu.core.enums.site_payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 站点支付申请状态枚举
 */
@Getter
@AllArgsConstructor
public enum SitePaymentSettleEnum {

    SETTLE_WAIT(1, "wait", "待审核"),
    SETTLE_PENDING(2, "pending", "审核中"),
    SETTLE_SUCCESS(3, "success", "审核通过"),
    SETTLE_FAIL(-1, "fail", "审核失败");

    private final Integer status;
    private final String name;
    private final String namec;


    /**
     * status
     *
     * @param status
     * @return name
     */
    public static String getNameByStatus(Integer status) {
        for (SitePaymentSettleEnum item : SitePaymentSettleEnum.values()) {
            if (item.getStatus().equals(status)) {
                return item.getName();
            }
        }
        return "";
    }

    public static List<Map<String, Object>> getStatusAndNameCMap() {
        return Arrays.stream(SitePaymentSettleEnum.values())
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", e.getStatus());
                    map.put("name", e.getNamec());
                    return map;
                })
                .toList();
    }
}

