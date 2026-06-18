package com.niu.core.enums.site_payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 站点支付申请状态枚举
 */
@Getter
@AllArgsConstructor
public enum SitePaymentStatusEnum {

    ON("on", "正常"),
    OFF("off", "已到期");

    private final String status;
    private final String name;


    /**
     * status
     *
     * @param status
     * @return
     */
    public static String getNameByStatus(String status) {
        for (SitePaymentStatusEnum item : SitePaymentStatusEnum.values()) {
            if (item.getStatus().equals(status)) {
                return item.getName();
            }
        }
        return "";
    }

    public static Map<String, String> getMap() {
        return Arrays.stream(SitePaymentStatusEnum.values())
                .collect(Collectors.toMap(SitePaymentStatusEnum::getStatus, SitePaymentStatusEnum::getName));
    }
}

