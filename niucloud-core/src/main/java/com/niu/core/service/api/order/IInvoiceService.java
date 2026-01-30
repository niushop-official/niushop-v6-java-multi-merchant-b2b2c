package com.niu.core.service.api.order;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.api.order.param.InvoiceApplyParam;
import com.niu.core.service.api.order.param.InvoiceSearchParam;
import com.niu.core.service.api.order.vo.InvoiceConfigVo;
import com.niu.core.service.api.order.vo.InvoiceInfoVo;
import com.niu.core.service.api.order.vo.InvoiceListVo;

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
     * @return InvoiceInfoVo
     */
    InvoiceInfoVo getInfo(Integer id);

    /**
     * 查询发票配置
     * @return InvoiceConfigVo
     */
    InvoiceConfigVo getInvoiceConfig();

    /**
     * 申请补开发票
     */
    void apply(InvoiceApplyParam param);
}
