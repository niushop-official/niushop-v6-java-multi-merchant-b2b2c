package com.niu.core.controller.adminapi.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.goods.IGoodsBadgeService;
import com.niu.core.service.admin.goods.param.EditGoodsBadgeSortParam;
import com.niu.core.service.admin.goods.param.EditGoodsBadgeStatusParam;
import com.niu.core.service.admin.goods.param.GoodsBadgeParam;
import com.niu.core.service.admin.goods.param.GoodsBadgeSearchParam;
import com.niu.core.service.admin.goods.vo.GoodsBadgeInfoVo;
import com.niu.core.service.admin.goods.vo.GoodsBadgeListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品角标控制器
 * 对应 PHP: app\adminapi\controller\goods\admin\Badge
 */
@RestController
@RequestMapping("adminapi/basic/admin/goods/badge")
public class BadgeController {

    @Resource
    private IGoodsBadgeService goodsBadgeService;

    @GetMapping("")
    public Result<PageResult<GoodsBadgeListVo>> page(@Validated PageParam pageParam,
                                                     @Validated GoodsBadgeSearchParam searchParam) {
        return Result.success(goodsBadgeService.page(pageParam, searchParam));
    }

    @GetMapping("/list")
    public Result<List<GoodsBadgeListVo>> list(@Validated GoodsBadgeSearchParam searchParam) {
        return Result.success(goodsBadgeService.list(searchParam));
    }

    @GetMapping("/{id}")
    public Result<GoodsBadgeInfoVo> info(@PathVariable("id") Integer id) {
        return Result.success(goodsBadgeService.info(id));
    }

    @PostMapping("")
    public Result<?> add(@Validated @RequestBody GoodsBadgeParam addParam) {
        Integer id = goodsBadgeService.add(addParam);
        return Result.success(Map.of("id", id));
    }

    @PutMapping("/{id}")
    public Result<?> edit(@PathVariable("id") Integer id, @Validated @RequestBody GoodsBadgeParam editParam) {
        goodsBadgeService.edit(id, editParam);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> del(@PathVariable("id") Integer id) {
        goodsBadgeService.del(id);
        return Result.success();
    }

    @PutMapping("/sort")
    public Result<?> modifySort(@Validated @RequestBody EditGoodsBadgeSortParam editParam) {
        goodsBadgeService.modifySort(editParam);
        return Result.success();
    }

    @PutMapping("/status")
    public Result<?> modifyStatus(@Validated @RequestBody EditGoodsBadgeStatusParam editParam) {
        goodsBadgeService.modifyStatus(editParam);
        return Result.success();
    }

    @PostMapping("/copy/{badge_id}")
    public Result<?> copy(@PathVariable("badge_id") Integer id) {
        goodsBadgeService.copy(id);
        return Result.success();
    }
}
