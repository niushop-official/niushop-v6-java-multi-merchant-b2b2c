package com.niu.core.controller.api.order;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.common.loader.pay.param.PayParam;
import com.niu.core.enums.basic.InvoiceContentEnum;
import com.niu.core.service.api.order.param.InvoiceApplyParam;
import com.niu.core.service.api.order.param.InvoiceSearchParam;
import com.niu.core.service.api.order.vo.InvoiceInfoVo;
import com.niu.core.service.api.order.vo.InvoiceListVo;
import com.niu.core.service.api.order.IInvoiceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 发票管理
 */
@RestController
@RequestMapping("/api/basic")
@Slf4j
public class InvoiceController {

    @Resource
    private IInvoiceService invoiceService;

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
     * 开票
     */
    @PostMapping("/invoice/apply")
    public Result<Boolean> apply(@Validated @RequestBody InvoiceApplyParam param) {
        invoiceService.apply(param);
        return Result.success(true);
    }

    /**
     * 发票内容列表
     *
     * @return
     */
    @GetMapping("/invoice/content/list")
    public Result contentList() {  return Result.success(InvoiceContentEnum.getMap()); }

    /**
     * 发票详情
     *
     * @return
     */
    @GetMapping("/config/invoice")
    public Result info() {
        return Result.success(invoiceService.getInvoiceConfig());
    }
}
