package com.niu.core.controller.adminapi.goods;

import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.enums.trade.TradeTypeDict;
import com.niu.core.service.admin.goods.param.*;
import com.niu.core.service.admin.goods.IGoodsBrandService;
import com.niu.core.service.admin.goods.vo.GoodsBrandInfoVo;
import com.niu.core.service.admin.goods.vo.GoodsBrandListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 商品品牌控制器
 * 对齐 PHP goods.admin.Brand
 */

@RestController
@RequestMapping("adminapi/basic/admin/goods/brand")
public class BrandController {

    @Resource
    IGoodsBrandService goodsBrandService;

    /**
     * 商品品牌分页列表
     */
    @GetMapping("")
    public Result<PageResult<GoodsBrandListVo>> page(@Validated PageParam pageParam, @Validated GoodsBrandSearchParam searchParam) {
        PageResult<GoodsBrandListVo> list = goodsBrandService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 商品品牌审核分页列表
     */
    @GetMapping("/audit")
    public Result<PageResult<GoodsBrandListVo>> pageAudit(@Validated PageParam pageParam, @Validated GoodsBrandSearchParam searchParam) {
        PageResult<GoodsBrandListVo> list = goodsBrandService.getAuditPage(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 商品品牌列表
     */
    @GetMapping("/list")
    public Result<List<GoodsBrandListVo>> list(@Validated GoodsBrandSearchParam searchParam) {
        List<GoodsBrandListVo> list = goodsBrandService.list(searchParam);
        return Result.success(list);
    }

    /**
     * 获取商品品牌业务类型（须放在 /{id} 之前，避免 trade_type 被当成 id）
     * 对齐 PHP: GET admin/goods/brand/trade_type
     */
    @GetMapping("/trade_type")
    public Result<Map<String, String>> getTradeType() {
        return Result.success(TradeTypeDict.getTradeType("goods_brand"));
    }

    /**
     * 获取商品品牌字典
     */
    @GetMapping("/init")
    public Result init() {
        return Result.success(goodsBrandService.init());
    }

    /**
     * 商品品牌详情
     */
    @GetMapping("/{id}")
    public Result<GoodsBrandInfoVo> info(@PathVariable("id") Integer id) {
        GoodsBrandInfoVo info = goodsBrandService.info(id);
        return Result.success(info);
    }

    /**
     * 商品品牌添加
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody GoodsBrandParam addParam) {
        goodsBrandService.add(addParam);
        return Result.success();
    }

    /**
     * 商品品牌编辑
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody GoodsBrandParam editParam) {
        goodsBrandService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 商品品牌删除
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        goodsBrandService.del(id);
        return Result.success();
    }

    /**
     * 修改排序
     */
    @PutMapping("/sort")
    public Result<Object> modifySort(@Validated @RequestBody EditGoodsBrandSortParam editParam) {
        goodsBrandService.modifySort(editParam);
        return Result.success();
    }

    /**
     * 商品品牌审核
     */
    @PutMapping("/audit")
    public Result<Object> audit(@Validated @RequestBody GoodsBrandAuditParam editParam) {
        goodsBrandService.audit(editParam);
        return Result.success();
    }

    /**
     * 修改商品品牌是否热门
     */
    @PutMapping("/hot")
    public Result<Object> hot(@Validated @RequestBody GoodsBrandIsHotParam editParam) {
        goodsBrandService.modifyHot(editParam);
        return Result.success();
    }

    /**
     * 修改品牌状态
     */
    @PutMapping("/status")
    public Result<Object> modifyStatus(@Validated @RequestBody GoodsBrandStatusParam editParam) {
        goodsBrandService.modifyStatus(editParam);
        return Result.success();
    }

    /**
     * 转到平台
     */
    @PutMapping("/move")
    public Result<Object> move(@Validated @RequestBody GoodsBrandMoveParam moveParam) {
        goodsBrandService.move(moveParam.getBrandId());
        return Result.success();
    }
}
