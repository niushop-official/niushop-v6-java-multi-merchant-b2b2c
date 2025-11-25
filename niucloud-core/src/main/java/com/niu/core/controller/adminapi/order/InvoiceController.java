package com.niu.core.controller.adminapi.order;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.order.IInvoiceService;
import com.niu.core.service.admin.order.param.InvoiceParam;
import com.niu.core.service.admin.order.param.InvoiceSearchParam;
import com.niu.core.service.admin.order.vo.InvoiceInfoVo;
import com.niu.core.service.admin.order.vo.InvoiceListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 发票控制器
 */

@RestController
@RequestMapping("adminapi/basic/admin")
public class InvoiceController {

    @Resource
    IInvoiceService invoiceService;

    /**
     * 发票列表
     *
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult < MallInvoiceListVo>>
     */
    @GetMapping("/invoice")
    public Result<PageResult<InvoiceListVo>> lists(@Validated PageParam pageParam, InvoiceSearchParam searchParam) {
        PageResult<InvoiceListVo> list = invoiceService.getPage(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 发票详情
     *
     * @return
     */
    @GetMapping("/invoice/{id}")
    public Result<InvoiceInfoVo> info(@Validated @PathVariable("id") Integer id) {
        InvoiceInfoVo info = invoiceService.getInfo(id);
        return Result.success(info);
    }

    /**
     * 发票编辑
     *
     * @param invoiceParam 开票参数
     * @return Result<Object>
     */
    @PutMapping("/invoice/{id}")
    public Result<Object> invoicing(@PathVariable("id") Integer id, @Validated @RequestBody InvoiceParam invoiceParam) {
        invoiceService.invoicing(id, invoiceParam);
        return Result.success();
    }


}
