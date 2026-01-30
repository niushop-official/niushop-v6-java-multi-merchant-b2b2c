package com.niu.core.controller.adminapi.goods;

import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.goods.param.*;
import com.niu.core.service.admin.goods.IGoodsBrandService;
import com.niu.core.service.admin.goods.vo.GoodsBrandInfoVo;
import com.niu.core.service.admin.goods.vo.GoodsBrandListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.List;

/**
 * 商品品牌控制器
 */

@RestController
@RequestMapping("adminapi/basic/admin/goods/brand")
public class BrandController {

    @Resource
    IGoodsBrandService goodsBrandService;

    /**
     * 商品品牌分页列表
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopGoodsBrandListVo>>
     */
    @GetMapping("")
    public Result<PageResult<GoodsBrandListVo>> page(@Validated PageParam pageParam, @Validated GoodsBrandSearchParam searchParam) {
        PageResult<GoodsBrandListVo> list = goodsBrandService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 商品品牌审核分页列表
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopGoodsBrandListVo>>
     */
    @GetMapping("/audit")
    public Result<PageResult<GoodsBrandListVo>> pageAudit(@Validated PageParam pageParam, @Validated GoodsBrandSearchParam searchParam) {
        PageResult<GoodsBrandListVo> list = goodsBrandService.getAuditPage(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 商品品牌列表
     *
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopGoodsBrandListVo>>
     */
    @GetMapping("/list")
    public Result<List<GoodsBrandListVo>> list(@Validated GoodsBrandSearchParam searchParam) {
        List<GoodsBrandListVo> list = goodsBrandService.list(searchParam);
        return Result.success(list);
    }

    /**
     * 商品品牌详情
     *
     * @param id 主键ID
     * @return Result<ShopGoodsBrandInfoVo>
     */
    @GetMapping("/{id}")
    public Result<GoodsBrandInfoVo> info(@PathVariable("id") Integer id) {
        GoodsBrandInfoVo info = goodsBrandService.info(id);
        return Result.success(info);
    }

    /**
     * 商品品牌添加
     *
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody GoodsBrandParam addParam) {
        goodsBrandService.add(addParam);
        return Result.success();
    }

    /**
     * 商品品牌编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody GoodsBrandParam editParam) {
        goodsBrandService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 商品品牌删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        goodsBrandService.del(id);
        return Result.success();
    }

    /**
     * 修改排序
     *
     * @return
     */
    @PutMapping("/sort")
    public Result<Object> modifySort(@Validated @RequestBody EditGoodsBrandSortParam editParam) {
        goodsBrandService.modifySort(editParam);
        return Result.success();
    }

    /**
     * 商品品牌审核
     *
     * @return
     */
    @PutMapping("/audit")
    public Result<Object> audit(@Validated @RequestBody GoodsBrandAuditParam editParam) {
        goodsBrandService.audit(editParam);
        return Result.success();
    }

    /**
     * 修改商品品牌是否热门
     *
     * @return
     */
    @PutMapping("/hot")
    public Result<Object> hot(@Validated @RequestBody GoodsBrandIsHotParam editParam) {
        goodsBrandService.modifyHot(editParam);
        return Result.success();
    }

    /**
     * 修改品牌状态
     *
     * @return
     */
    @PutMapping("/status")
    public Result<Object> modifyStatus(@Validated @RequestBody GoodsBrandStatusParam editParam) {
        goodsBrandService.modifyStatus(editParam);
        return Result.success();
    }


    /**
     * 转到平台
     *
     * @return
     */
    @PutMapping("/move")
    public Result<Object> move(@Validated @RequestBody GoodsBrandMoveParam moveParam) {
        goodsBrandService.move(moveParam.getBrandId());
        return Result.success();
    }

    /**
     * 获取商品品牌字典
     *
     * @return
     */
    @GetMapping("/init")
    public Result init() { return Result.success(goodsBrandService.init());  }
}
