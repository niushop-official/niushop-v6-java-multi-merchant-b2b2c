package com.niu.core.service.core.invoice.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.service.core.invoice.ICoreInvoiceConfigService;
import com.niu.core.service.core.invoice.param.InvoiceConfigParam;
import com.niu.core.service.core.invoice.vo.InvoiceConfigVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.site.basic.param.SiteInvoiceConfigParam;
import com.niu.core.service.site.basic.vo.SiteInvoiceConfigVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 发票设置服务层
 */
@Service
public class CoreInvoiceConfigServiceImpl implements ICoreInvoiceConfigService {

    private static final String INVOICE_KEY = "INVOICE";

    @Resource
    ICoreConfigService coreConfigService;

    @Override
    public InvoiceConfigVo getConfig(Integer siteId) {
        // 获取发票开关配置
        JSONObject invoiceConfig = coreConfigService.getConfigValue(siteId, INVOICE_KEY);
        InvoiceConfigVo vo = new InvoiceConfigVo();
        if (invoiceConfig != null && !invoiceConfig.isEmpty() && invoiceConfig.containsKey("is_invoice")) {
            vo.getInvoice().setIsInvoice(invoiceConfig.getStr("is_invoice"));
        }
        return vo;
    }

    @Override
    public void setConfig(InvoiceConfigParam param) {
        JSONObject invoiceInfo = new JSONObject();
        invoiceInfo.set("is_invoice", param.getIsInvoice());
        coreConfigService.setConfig(param.getSiteId(), INVOICE_KEY, invoiceInfo);
    }

    @Override
    public SiteInvoiceConfigVo getSiteInvoiceConfig(Integer siteId) {
        JSONObject invoiceConfig = coreConfigService.getConfigValue(siteId, INVOICE_KEY);
        SiteInvoiceConfigVo configVo = new SiteInvoiceConfigVo();
        if (invoiceConfig != null && !invoiceConfig.isEmpty()) {
            if (invoiceConfig.containsKey("is_invoice")) {
                configVo.setIsInvoice(invoiceConfig.getStr("is_invoice"));
            }
            if (invoiceConfig.containsKey("invoice_type") && !invoiceConfig.getJSONArray("invoice_type").isEmpty()) {
                configVo.setInvoiceType(JSONUtil.toList(invoiceConfig.getJSONArray("invoice_type"), String.class));
            }
            if (invoiceConfig.containsKey("invoice_content") && !invoiceConfig.getJSONArray("invoice_content").isEmpty()) {
                configVo.setInvoiceContent(JSONUtil.toList(invoiceConfig.getJSONArray("invoice_content"), String.class));
            }
        }
        return configVo;
    }

    @Override
    public void setSiteInvoiceConfig(Integer siteId, SiteInvoiceConfigParam param) {
        JSONObject invoiceConfig = new JSONObject();
        invoiceConfig.set("is_invoice", param.getIsInvoice());
        invoiceConfig.set("invoice_type", ObjectUtil.defaultIfNull(param.getInvoiceType(), new java.util.ArrayList<>()));
        invoiceConfig.set("invoice_content", ObjectUtil.defaultIfNull(param.getInvoiceContent(), new java.util.ArrayList<>()));
        coreConfigService.setConfig(siteId, INVOICE_KEY, invoiceConfig);
    }
}
