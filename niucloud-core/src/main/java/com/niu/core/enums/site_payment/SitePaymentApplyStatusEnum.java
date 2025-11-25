package com.niu.core.enums.site_payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 站点支付申请状态枚举
 */
@Getter
@AllArgsConstructor
public enum SitePaymentApplyStatusEnum {
    
    DEAFT("deaft", "草稿"),
    WAIT_AUDIT("wait_audit", "待审核"),
    AUDIT_FAIL("audit_fail", "审核失败"),
    AUDIT_PASS("audit_pass", "审核通过"),
    CANCEL("cancel", "取消");

    private final String status;
    private final String name;
}

