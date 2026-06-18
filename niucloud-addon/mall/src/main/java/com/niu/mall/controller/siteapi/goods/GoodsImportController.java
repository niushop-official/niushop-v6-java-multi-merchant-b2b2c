package com.niu.mall.controller.siteapi.goods;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.member.param.MemberImportAddParam;
import com.niu.mall.service.site.goods.IMallGoodsImportService;
import com.niu.mall.service.site.goods.param.MallGoodsImportParam;
import com.niu.mall.service.site.goods.vo.MallGoodsImportRowVo;
import com.niu.mall.service.site.goods.vo.MallGoodsImportVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("adminapi/mall/site/goods/import")
public class GoodsImportController {

    @Resource
    private IMallGoodsImportService importService;

    @GetMapping("/page")
    @SaCheckLogin
    public Result<PageResult<MallGoodsImportVo>> getMemberImportPage(@Validated PageParam pageParam) {
        PageResult<MallGoodsImportVo> result = importService.getPage(pageParam);
        return Result.success(result);
    }

    @PostMapping("/detail/{id}")
    @SaCheckLogin
    public Result<PageResult<MallGoodsImportRowVo>> detail(@Validated @PathVariable("id") MallGoodsImportParam param, @Validated PageParam pageParam) {
        PageResult<MallGoodsImportRowVo> result = importService.getDetail(param.getRecordId(), pageParam, null);
        return Result.success(result);
    }

    @PostMapping("/add")
    @SaCheckLogin
    public Result<?> add(@Validated @RequestBody MallGoodsImportParam addParam) {
        return Result.success(importService.add(addParam));
    }

    @PostMapping("/exec")
    @SaCheckLogin
    public Result<?> exec(@Validated @RequestBody MallGoodsImportParam param) {
        importService.execute(param.getRecordId());
        return Result.success();
    }

    @GetMapping("/goodsTypeAll")
    @SaCheckLogin
    public Result<?> getGoodsTypeAll() {
        return Result.success(importService.getGoodsTypeAll());
    }

    @GetMapping("/demo")
    @SaCheckLogin
    public Result<?> demo(@Validated @RequestBody MemberImportAddParam addParam) {
//        memberImportService.memberImport(addParam.getData());
        return Result.success();
    }
}
