package com.niu.mall.controller.siteapi.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.mall.service.site.goods.IMallGoodsRankService;
import com.niu.mall.service.site.goods.param.*;
import com.niu.mall.service.site.goods.vo.MallGoodsRankInfoVo;
import com.niu.mall.service.core.goods.param.ShopGoodsRankConfigParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 商品排行榜控制器
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("adminapi/mall/site")
class GoodsRankController {

    private final IMallGoodsRankService ShopGoodsRankService;

    @PostMapping("/good/rank/config")
    Result<?> setRankConfig(@RequestBody ShopGoodsRankConfigParam param) {
        ShopGoodsRankService.setGoodsRankConfig(param);
        return Result.success();
    }

    @GetMapping("/good/rank/config")
    Result<?> getRankConfig() {
        return Result.success(ShopGoodsRankService.getGoodsRankConfig());
    }

    @GetMapping("/good/rank")
    Result<?> page(@Validated PageParam pageParam, @Validated MallGoodsRankSearchParam searchParam) {
        return Result.success(ShopGoodsRankService.page(pageParam, searchParam));
    }

    @PostMapping("/good/rank")
    Result<?> add(@Validated @RequestBody MallGoodsRankParam addParam) {
        return Result.success(Map.of("id", ShopGoodsRankService.add(addParam)));
    }

    @PutMapping("/good/rank/{id}")
    Result<?> edit(@PathVariable("id") Integer id, @Validated @RequestBody MallGoodsRankParam editParam) {
        ShopGoodsRankService.edit(id, editParam);
        return Result.success();
    }

    @GetMapping("/good/rank/{id}")
    Result<?> info(@PathVariable("id") Integer id) {
        MallGoodsRankInfoVo info = ShopGoodsRankService.info(id);
        return Result.success(info);
    }

    @DeleteMapping("/good/rank/{id}")
    Result<Object> del(@PathVariable("id") Integer id) {
        ShopGoodsRankService.del(id);
        return Result.success();
    }

    @GetMapping("/good/rank/dict")
    Result<?> getOptionData() {
        return Result.success(ShopGoodsRankService.getOptionData());
    }

    @PutMapping("/good/rank/sort")
    Result<?> editSort(@RequestBody MallGoodsRankEditSortParam param) {
        ShopGoodsRankService.editSort(param);
        return Result.success();
    }

    @PutMapping("/good/rank/batchDelete")
    Result<?> batchDelete(@RequestBody MallGoodsRankBatchDeleteParam param) {
        ShopGoodsRankService.batchDelete(param);
        return Result.success();
    }

    @GetMapping("/good/rank/select")
    Result<?> select(PageParam pageParam, MallGoodsRankSelectPageParam param) {
        return Result.success(ShopGoodsRankService.getSelectPage(pageParam, param));
    }

    @PutMapping("/goods/rank/status")
    Result<?> modifyStatus(@RequestBody MallGoodsRankModifyStatusParam param) {
        ShopGoodsRankService.modifyStatus(param);
        return Result.success();
    }

}
