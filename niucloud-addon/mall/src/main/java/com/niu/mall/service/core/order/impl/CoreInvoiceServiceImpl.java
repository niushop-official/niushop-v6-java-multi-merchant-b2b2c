package com.niu.mall.service.core.order.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.entity.basic.Invoice;
import com.niu.core.enums.basic.InvoiceContentEnum;
import com.niu.core.enums.basic.InvoiceStatusEnum;
import com.niu.core.mapper.basic.InvoiceMapper;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.mall.service.core.order.ICoreInvoiceService;
import com.niu.mall.service.core.order.vo.InvoiceInfoVo;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.Map;

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
        Map<String, String> invoiceContentMap = InvoiceContentEnum.getMap();
        if (ObjectUtil.isEmpty(invoiceConfig)){
            invoiceInfoVo.setInvoiceContent(JSONUtil.toJsonStr(invoiceContentMap));
            invoiceInfoVo.setIsInvoice("2");
        }else{
            invoiceInfoVo.setInvoiceContent(JSONUtil.toJsonStr(invoiceContentMap));
            invoiceInfoVo.setIsInvoice(ObjectUtil.isEmpty(invoiceConfig.getStr("is_invoice"))?"1": invoiceConfig.getStr("is_invoice"));
        }
        return invoiceInfoVo;
    }
}
