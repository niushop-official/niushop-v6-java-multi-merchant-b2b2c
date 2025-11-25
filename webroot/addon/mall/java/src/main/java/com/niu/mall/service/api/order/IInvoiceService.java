package com.niu.mall.service.api.order;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.api.order.param.MallInvoiceParam;
import com.niu.mall.service.api.order.param.MallInvoiceSearchParam;
import com.niu.mall.service.api.order.vo.MallInvoiceInfoVo;
import com.niu.mall.service.api.order.vo.MallInvoiceListVo;

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
    PageResult<MallInvoiceListVo> getPage(PageParam pageParam, MallInvoiceSearchParam searchParam);

    /**
     * 发票详情
     * @param id 主键ID
     * @return ShopInvoiceInfoVo
     */
    MallInvoiceInfoVo getInfo(Integer id);

}
