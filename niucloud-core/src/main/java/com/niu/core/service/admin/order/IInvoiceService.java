package com.niu.core.service.admin.order;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.order.param.InvoiceParam;
import com.niu.core.service.admin.order.param.InvoiceSearchParam;
import com.niu.core.service.admin.order.vo.InvoiceInfoVo;
import com.niu.core.service.admin.order.vo.InvoiceListVo;

/**
 * 发票服务接口
 */
public interface IInvoiceService {


    /**
     * 发票列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopInvoiceListVo>
     */
    PageResult<InvoiceListVo> getPage(PageParam pageParam, InvoiceSearchParam searchParam);

    /**
     * 发票详情
     * @param id 主键ID
     * @return ShopInvoiceInfoVo
     */
    InvoiceInfoVo getInfo(Integer id);

    /**
     * 开票
     * @param id
     * @param invoiceParam
     */
    void invoicing(Integer id, InvoiceParam invoiceParam);

}
