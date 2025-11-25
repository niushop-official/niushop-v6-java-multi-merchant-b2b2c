package com.niu.core.controller.siteapi.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.site.goods.param.EditGoodsBrandSortParam;
import com.niu.core.service.site.goods.param.GoodsBrandParam;
import com.niu.core.service.site.goods.param.GoodsBrandSearchParam;
import com.niu.core.service.site.goods.vo.GoodsBrandInfoVo;
import com.niu.core.service.site.goods.vo.GoodsBrandListVo;
import com.niu.core.service.site.goods.IGoodsBrandService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.List;

/**
 * 商品品牌控制器
 */

@RestController
@RequestMapping("adminapi/basic/site/goods/brand")
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


}
