package com.niu.core.service.core.site;

import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.pay.PayRefund;
import com.niu.core.entity.pay.PayTransfer;
import com.niu.core.entity.site.SiteFinanceLog;

public interface ICoreSiteFinanceService {

    /**
     * 添加账户流水
     * @param siteFinanceLog
     */
    void addLog(SiteFinanceLog siteFinanceLog);

    /**
     * 添加平台流水
     * @param siteFinanceLog
     */
    void addPlatLog(SiteFinanceLog siteFinanceLog);
}
