package com.niu.mall.controller.siteapi.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.mall.service.site.goods.IMallGoodsStatService;
import com.niu.mall.service.site.goods.param.GoodsStatGetRankParam;
import com.niu.mall.service.site.goods.param.MallGoodsStatDateParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("adminapi/mall/site/goods/statistics")
public class GoodsStatController {
    private final IMallGoodsStatService shopGoodsStatService;

    @GetMapping("/basic")
    Result<?> basic(MallGoodsStatDateParam param) {
        return Result.success(shopGoodsStatService.getBasic(param));
    }

    @GetMapping("/trend")
    Result<?> trend(MallGoodsStatDateParam param) {
        return Result.success(shopGoodsStatService.getTrend(param));
    }

    @GetMapping("/rank")
    Result<?> rank(PageParam pageParam, GoodsStatGetRankParam param,
                   @RequestParam(value = "category_ids[]", required = false) List<Integer> categoryIds) {
        param.setCategoryIds(categoryIds);
        return Result.success(shopGoodsStatService.getRank(pageParam, param));
    }

    @GetMapping("/type")
    Result<?> type() {
        return Result.success(shopGoodsStatService.getType());
    }


}
