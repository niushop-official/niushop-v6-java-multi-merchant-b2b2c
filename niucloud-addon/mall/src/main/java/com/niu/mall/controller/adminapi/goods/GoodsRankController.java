package com.niu.mall.controller.adminapi.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.mall.service.admin.goods.IMallGoodsRankService;
import com.niu.mall.service.admin.goods.param.*;
import com.niu.mall.service.admin.goods.vo.MallGoodsRankInfoVo;
import com.niu.mall.service.core.goods.param.ShopGoodsRankConfigParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 商品排行榜控制器
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("adminapi/mall/admin")
class GoodsRankController {

    private final IMallGoodsRankService mallGoodsRankService;

    @PostMapping("/good/rank/config")
    Result<?> setRankConfig(@RequestBody ShopGoodsRankConfigParam param) {
        mallGoodsRankService.setGoodsRankConfig(param);
        return Result.success();
    }

    @GetMapping("/good/rank/config")
    Result<?> getRankConfig() {
        return Result.success(mallGoodsRankService.getGoodsRankConfig());
    }

    @GetMapping("/good/rank")
    Result<?> page(@Validated PageParam pageParam, @Validated MallGoodsRankSearchParam searchParam) {
        return Result.success(mallGoodsRankService.page(pageParam, searchParam));
    }

    @PostMapping("/good/rank")
    Result<?> add(@Validated @RequestBody MallGoodsRankParam addParam) {
        mallGoodsRankService.add(addParam);
        return Result.success();
    }

    @PutMapping("/good/rank/{id}")
    Result<?> edit(@PathVariable("id") Integer id, @Validated @RequestBody MallGoodsRankParam editParam) {
        mallGoodsRankService.edit(id, editParam);
        return Result.success();
    }

    @GetMapping("/good/rank/{id}")
    Result<?> info(@PathVariable("id") Integer id) {
        MallGoodsRankInfoVo info = mallGoodsRankService.info(id);
        return Result.success(info);
    }

    @DeleteMapping("/good/rank/{id}")
    Result<Object> del(@PathVariable("id") Integer id) {
        mallGoodsRankService.del(id);
        return Result.success();
    }

    @GetMapping("/good/rank/dict")
    Result<?> getOptionData() {
        return Result.success(mallGoodsRankService.getOptionData());
    }

    @PutMapping("/good/rank/sort")
    Result<?> editSort(@RequestBody MallGoodsRankEditSortParam param) {
        mallGoodsRankService.editSort(param);
        return Result.success();
    }

    @PutMapping("/good/rank/batchDelete")
    Result<?> batchDelete(@RequestBody MallGoodsRankBatchDeleteParam param) {
        mallGoodsRankService.batchDelete(param);
        return Result.success();
    }

    @GetMapping("/good/rank/select")
    Result<?> select(PageParam pageParam, MallGoodsRankSelectPageParam param) {
        return Result.success(mallGoodsRankService.getSelectPage(pageParam, param));
    }

    @PutMapping("/goods/rank/status")
    Result<?> modifyStatus(@RequestBody MallGoodsRankModifyStatusParam param) {
        mallGoodsRankService.modifyStatus(param);
        return Result.success();
    }

}
