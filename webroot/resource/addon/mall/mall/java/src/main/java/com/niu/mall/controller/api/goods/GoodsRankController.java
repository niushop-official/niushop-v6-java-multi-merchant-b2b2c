package com.niu.mall.controller.api.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.mall.service.admin.goods.param.MallGoodsRankSearchParam;
import com.niu.mall.service.api.goods.IGoodsRankService;
import com.niu.mall.service.api.goods.param.GoodsRankComponentParam;
import com.niu.mall.service.api.goods.param.MallGoodsRankGoodsPageParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/mall/rank")
public class GoodsRankController {
    private final IGoodsRankService goodsRankService;

    @GetMapping("/getRankConfig")
    Result<?> getRankConfig() {
        return Result.success(goodsRankService.getGoodsRankConfig());
    }

    @GetMapping
    Result<?> pages(MallGoodsRankSearchParam searchParam) {
        return Result.success(goodsRankService.page(searchParam));
    }

    @GetMapping("/goods")
    Result<?> goods(PageParam pageParam, MallGoodsRankGoodsPageParam param) {
        return Result.success(goodsRankService.getGoodsPage(pageParam, param));
    }

    @GetMapping("/components")
    Result<?> components(PageParam pageParam, GoodsRankComponentParam param) {
        return Result.success(goodsRankService.getRankComponents(pageParam, param));
    }

    @GetMapping("/search")
    Result<?> search() {
        return Result.success(goodsRankService.getRankListOfSearch());
    }

    @GetMapping("/goods_rank")
    Result<?> getGoodsRank(@RequestParam(value = "goods_id", defaultValue = "0") Integer goodsId) {
        var result = goodsRankService.getGoodsRank(goodsId);
        if (result == null) {
            return Result.success(Collections.emptyList());
        }
        return Result.success(result);
    }

}
