package com.niu.mall.service.core.order.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.entity.basic.Invoice;
import com.niu.core.enums.basic.InvoiceStatusEnum;
import com.niu.core.mapper.basic.InvoiceMapper;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.mall.service.core.order.ICoreInvoiceService;
import com.niu.mall.service.core.order.vo.InvoiceInfoVo;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class CoreInvoiceServiceImpl implements ICoreInvoiceService {

    @Resource
    InvoiceMapper invoiceMapper;
    @Resource
    ICoreConfigService coreConfigService;

    /**
     * 发票生效
     *
     * @param id
     */
    @Override
    public void open(Integer id) {
        Invoice model = new Invoice();
        model.setId(id);
        model.setStatus(InvoiceStatusEnum.OPEN.getStatus());
        invoiceMapper.updateById(model);
    }

    /**
     * 发票失效
     *
     * @param id
     */
    @Override
    public void close(Integer id) {
        Invoice model = new Invoice();
        model.setId(id);
        model.setStatus(InvoiceStatusEnum.WAIT_OPEN.getStatus());
        invoiceMapper.updateById(model);
    }

    @Override
    public InvoiceInfoVo invoice(Integer siteId) {
        InvoiceInfoVo invoiceInfoVo = new InvoiceInfoVo();
        JSONObject invoiceConfig = coreConfigService.getConfigValue(siteId, "INVOICE");
        if (ObjectUtil.isEmpty(invoiceConfig)){
            invoiceInfoVo.setInvoiceContent("");
            invoiceInfoVo.setInvoiceType( "");
            invoiceInfoVo.setIsInvoice("2");
        }else{
            invoiceInfoVo.setInvoiceContent(ObjectUtil.isEmpty(invoiceConfig.getStr("invoice_content"))?"": invoiceConfig.getStr("invoice_content"));
            invoiceInfoVo.setInvoiceType(ObjectUtil.isEmpty(invoiceConfig.getStr("invoice_type"))?"": invoiceConfig.getStr("invoice_type"));
            invoiceInfoVo.setIsInvoice(ObjectUtil.isEmpty(invoiceConfig.getStr("is_invoice"))?"1": invoiceConfig.getStr("is_invoice"));
        }
        return invoiceInfoVo;
    }
}
