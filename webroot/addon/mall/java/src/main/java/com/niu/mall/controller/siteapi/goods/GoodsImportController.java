package com.niu.mall.controller.siteapi.goods;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.mall.service.site.goods.IMallGoodsImportService;
import com.niu.mall.service.site.goods.param.MallGoodsImportParam;
import com.niu.mall.service.site.goods.vo.MallGoodsImportRowVo;
import com.niu.mall.service.site.goods.vo.MallGoodsImportVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("adminapi/mall/site/goods/import")
public class GoodsImportController {

    @Resource
    private IMallGoodsImportService importService;

    @GetMapping("/page")
    @SaCheckLogin
    public Result<PageResult<MallGoodsImportVo>> page(@Validated PageParam pageParam) {
        return Result.success(importService.getPage(pageParam));
    }

    @PutMapping("/detail/{recordId}")
    @SaCheckLogin
    public Result<PageResult<MallGoodsImportRowVo>> detail(@PathVariable("recordId") Integer recordId,
                                                           @Validated PageParam pageParam,
                                                           @RequestParam(value = "status", required = false) String status,
                                                           @RequestParam(value = "search_text", required = false) String searchText) {
        Map<String, Object> params = new HashMap<>();
        if (status != null) {
            params.put("status", status);
        }
        if (searchText != null) {
            params.put("search_text", searchText);
        }
        return Result.success(importService.getDetail(recordId, pageParam, params));
    }

    @PostMapping("/add")
    @SaCheckLogin
    public Result<?> add(@Validated @RequestBody MallGoodsImportParam addParam) {
        return Result.success(importService.add(addParam));
    }

    @PostMapping("/exec")
    @SaCheckLogin
    public Result<?> exec(@Validated @RequestBody MallGoodsImportParam param) {
        return Result.success(importService.execute(param.getRecordId()));
    }

    @GetMapping("/goodsTypeAll")
    @SaCheckLogin
    public Result<?> getGoodsTypeAll() {
        return Result.success(importService.getGoodsTypeAll());
    }

    @GetMapping("/demo")
    @SaCheckLogin
    public Result<?> demo() {
        return Result.success(importService.demo());
    }
}
