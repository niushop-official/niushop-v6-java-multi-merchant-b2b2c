package com.niu.mall.controller.siteapi.order;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.mall.service.site.order.IMallInvoiceService;
import com.niu.mall.service.site.order.param.MallInvoiceParam;
import com.niu.mall.service.site.order.param.MallInvoiceSearchParam;
import com.niu.mall.service.site.order.vo.MallInvoiceInfoVo;
import com.niu.mall.service.site.order.vo.MallInvoiceListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 发票控制器
 */

@RestController
@RequestMapping("adminapi/mall/site")
public class InvoiceController {

    @Resource
    IMallInvoiceService mallInvoiceService;

    /**
     * 发票列表
     *
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult < InvoiceListVo>>
     */
    @GetMapping("/invoice")
    public Result<PageResult<MallInvoiceListVo>> lists(@Validated PageParam pageParam, MallInvoiceSearchParam searchParam) {
        PageResult<MallInvoiceListVo> list = mallInvoiceService.getPage(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 发票详情
     *
     * @return
     */
    @GetMapping("/invoice/{id}")
    public Result<MallInvoiceInfoVo> info(@Validated @PathVariable("id") Integer id) {
        MallInvoiceInfoVo info = mallInvoiceService.getInfo(id);
        return Result.success(info);
    }

    /**
     * 发票编辑
     *
     * @param mallInvoiceParam 开票参数
     * @return Result<Object>
     */
    @PutMapping("/invoice/{id}")
    public Result<Object> invoicing(@PathVariable("id") Integer id, @Validated @RequestBody MallInvoiceParam mallInvoiceParam) {
        mallInvoiceService.invoicing(id, mallInvoiceParam);
        return Result.success();
    }


}
