package com.niu.mall.controller.api.order;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.mall.service.api.order.IInvoiceService;
import com.niu.mall.service.api.order.param.MallInvoiceSearchParam;
import com.niu.mall.service.api.order.vo.MallInvoiceInfoVo;
import com.niu.mall.service.api.order.vo.MallInvoiceListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 发票控制器
 */

@RestController
@RequestMapping("api/mall")
public class InvoiceController {

    @Resource
    IInvoiceService invoiceService;

    /**
     * 发票列表
     *
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult < ShopInvoiceListVo>>
     */
    @GetMapping("/invoice")
    public Result<PageResult<MallInvoiceListVo>> lists(@Validated PageParam pageParam, MallInvoiceSearchParam searchParam) {
        PageResult<MallInvoiceListVo> list = invoiceService.getPage(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 发票详情
     *
     * @return
     */
    @GetMapping("/invoice/{id}")
    public Result<MallInvoiceInfoVo> info(@Validated @PathVariable("id") Integer id) {
        MallInvoiceInfoVo info = invoiceService.getInfo(id);
        return Result.success(info);
    }

}
