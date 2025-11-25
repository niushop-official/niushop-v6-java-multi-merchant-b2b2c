package com.niu.mall.service.site.order;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.site.order.param.MallInvoiceParam;
import com.niu.mall.service.site.order.param.MallInvoiceSearchParam;
import com.niu.mall.service.site.order.vo.MallInvoiceInfoVo;
import com.niu.mall.service.site.order.vo.MallInvoiceListVo;

/**
 * 发票服务接口
 */
public interface IMallInvoiceService {


    /**
     * 发票列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopInvoiceListVo>
     */
    PageResult<MallInvoiceListVo> getPage(PageParam pageParam, MallInvoiceSearchParam searchParam);

    /**
     * 发票详情
     * @param id 主键ID
     * @return ShopInvoiceInfoVo
     */
    MallInvoiceInfoVo getInfo(Integer id);

    /**
     * 开票
     * @param id
     * @param mallInvoiceParam
     */
    void invoicing(Integer id, MallInvoiceParam mallInvoiceParam);

}
