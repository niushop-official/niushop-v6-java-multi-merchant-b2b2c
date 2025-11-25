package com.niu.core.event.site_payment;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.entity.site.SitePaymentApply;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 站点支付申请审核后事件
 * 对应 PHP 的 event('SitePaymentAuditAfter', ...)
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SitePaymentAuditAfterEvent extends Event {

    /**
     * 站点支付申请信息
     */
    private SitePaymentApply sitePaymentApply;

    public SitePaymentAuditAfterEvent(SitePaymentApply sitePaymentApply) {
        this.sitePaymentApply = sitePaymentApply;
    }
}
