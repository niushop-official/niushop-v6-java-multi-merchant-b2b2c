package com.niu.core.enums.site_payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 站点支付申请状态枚举
 */
@Getter
@AllArgsConstructor
public enum SitePaymentApplyStatusEnum {

    DEAFT("deaft", "草稿", 0),
    WAIT_AUDIT("wait_audit", "待审核", 1),
    SETTLE_PENDING("pending_audit", "审核中", 2),
    AUDIT_PASS("audit_pass", "审核通过", 3),
    AUDIT_FAIL("audit_fail", "审核失败", -1),
    CANCEL("cancel", "取消", -2);

    private final String status;
    private final String name;
    private final Integer code;

    public static String getNameByStatus(String status) {
        return Arrays.stream(SitePaymentApplyStatusEnum.values())
                .filter(e -> e.getStatus().equals(status))
                .map(SitePaymentApplyStatusEnum::getName)
                .findFirst()
                .orElse("");
    }

    public static SitePaymentApplyStatusEnum getEnumByCode(Integer code) {
        return Arrays.stream(SitePaymentApplyStatusEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }
}

