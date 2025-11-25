package com.niu.mall.service.core.order;

import com.niu.mall.service.core.order.vo.InvoiceInfoVo;

public interface ICoreInvoiceService {

    /**
     * 发票生效
     *
     * @param id
     */
    void open(Integer id);

    /**
     * 发票失效
     *
     * @param id
     */
    void close(Integer id);

    /**
     * 发票信息
     *
     * @param siteId
     */
    InvoiceInfoVo invoice(Integer siteId);
}
