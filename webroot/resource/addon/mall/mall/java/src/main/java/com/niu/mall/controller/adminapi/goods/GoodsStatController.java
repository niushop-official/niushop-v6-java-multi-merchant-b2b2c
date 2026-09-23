package com.niu.mall.controller.adminapi.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.mall.service.admin.goods.IMallGoodsStatService;
import com.niu.mall.service.admin.goods.param.GoodsStatGetRankParam;
import com.niu.mall.service.admin.goods.param.MallGoodsStatDateParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("adminapi/mall/admin/goods/statistics")
public class GoodsStatController {
    private final IMallGoodsStatService mallGoodsStatService;

    @GetMapping("/basic")
    Result<?> basic(MallGoodsStatDateParam param) {
        return Result.success(mallGoodsStatService.getBasic(param));
    }

    @GetMapping("/trend")
    Result<?> trend(MallGoodsStatDateParam param) {
        return Result.success(mallGoodsStatService.getTrend(param));
    }

    @GetMapping("/rank")
    Result<?> rank(PageParam pageParam, GoodsStatGetRankParam param) {
        return Result.success(mallGoodsStatService.getRank(pageParam, param));
    }

    @GetMapping("/type")
    Result<?> type() {
        return Result.success(mallGoodsStatService.getType());
    }


}
