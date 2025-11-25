package com.niu.core.controller.siteapi.basic;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.basic.ISiteInvoiceService;
import com.niu.core.service.basic.param.SiteInvoiceParam;
import com.niu.core.service.basic.param.SiteInvoiceSubmitParam;
import com.niu.core.service.basic.param.SiteInvoiceConfigParam;
import com.niu.core.service.basic.vo.SiteInvoiceListVo;
import com.niu.core.service.basic.vo.SiteInvoiceConfigVo;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("adminapi/basic/site/invoice")
public class SiteInvoiceController {
    @Resource
    ISiteInvoiceService iSiteInvoiceService;

    /**
     * 获取发票分页列表
     */
    @GetMapping("")
    public Result<PageResult<SiteInvoiceListVo>> lists(@Validated PageParam pageParam, @Validated SiteInvoiceParam param) {
        return Result.success(iSiteInvoiceService.getPage(pageParam, param));
    }

    /**
     * 发票详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<SiteInvoiceListVo> info(@Validated @PathVariable("id") Integer id) {
        SiteInvoiceListVo info = iSiteInvoiceService.getInfo(id);
        return Result.success(info);
    }

    /**
     * 开票
     * @param id
     * @param param
     * @return
     */
    @PutMapping("/{id}")
    public Result<Object> invoicing(@PathVariable("id") Integer id, @Validated @RequestBody SiteInvoiceSubmitParam param) {
        iSiteInvoiceService.invoicing(id, param);
        return Result.success();
    }

    /**
     * 获取发票配置
     */
    @GetMapping("/config")
    public Result<Map<String, Object>> getConfig() {
        Map<String, Object> result = new HashMap<>();
        result.put("invoice", iSiteInvoiceService.getConfig());
        return Result.success(result);
    }

    /**
     * 设置发票配置
     */
    @PostMapping("/config")
    public Result<Map<String, Object>> setConfig(@Validated @RequestBody SiteInvoiceConfigParam param) {
        iSiteInvoiceService.setConfig(param);
        Map<String, Object> result = new HashMap<>();
        result.put("invoice", iSiteInvoiceService.getConfig());
        return Result.success(result);

    }
}