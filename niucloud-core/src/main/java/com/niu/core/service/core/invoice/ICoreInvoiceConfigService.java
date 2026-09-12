package com.niu.core.service.core.invoice;

import com.niu.core.service.core.invoice.param.InvoiceConfigParam;
import com.niu.core.service.core.invoice.vo.InvoiceConfigVo;
import com.niu.core.service.site.basic.param.SiteInvoiceConfigParam;
import com.niu.core.service.site.basic.vo.SiteInvoiceConfigVo;

/**
 * 发票设置服务
 */
public interface ICoreInvoiceConfigService {

    /**
     * 获取发票配置
     */
    InvoiceConfigVo getConfig(Integer siteId);

    /**
     * 设置发票开关
     */
    void setConfig(InvoiceConfigParam param);

    /**
     * 获取站点发票完整配置
     */
    SiteInvoiceConfigVo getSiteInvoiceConfig(Integer siteId);

    /**
     * 设置站点发票完整配置
     */
    void setSiteInvoiceConfig(Integer siteId, SiteInvoiceConfigParam param);
}
