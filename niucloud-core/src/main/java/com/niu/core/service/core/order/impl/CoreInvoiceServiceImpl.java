package com.niu.core.service.core.order.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.core.entity.basic.Invoice;
import com.niu.core.enums.basic.InvoiceStatusEnum;
import com.niu.core.mapper.basic.InvoiceMapper;
import com.niu.core.service.core.order.ICoreInvoiceService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 消息管理服务层
 */
@Service
public class CoreInvoiceServiceImpl implements ICoreInvoiceService {

    @Resource
    InvoiceMapper invoiceMapper;

    @Override
    public Boolean open(Integer id) {
        invoiceMapper.update(new Invoice(), new UpdateWrapper<Invoice>().eq("id", id).set("status", InvoiceStatusEnum.OPEN.getStatus()));
        return true;
    }
}
