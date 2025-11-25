package com.niu.core.controller.adminapi.goods;

import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.goods.param.GoodsServiceParam;
import com.niu.core.service.admin.goods.param.GoodsServiceSearchParam;
import com.niu.core.service.admin.goods.IGoodsServiceService;
import com.niu.core.service.admin.goods.vo.GoodsServiceInfoVo;
import com.niu.core.service.admin.goods.vo.GoodsServiceListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.List;

/**
 * 商品服务控制器
 */

@RestController
@RequestMapping("adminapi/basic/admin/goods/service")
public class ServiceController {

    @Resource
    IGoodsServiceService goodsServiceService;

    /**
     * 商品服务分页列表
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopGoodsServiceListVo>>
     */
    @GetMapping("")
    public Result<PageResult<GoodsServiceListVo>> page(@Validated PageParam pageParam, @Validated GoodsServiceSearchParam searchParam) {
        PageResult<GoodsServiceListVo> list = goodsServiceService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 商品服务列表
     *
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopGoodsServiceListVo>>
     */
    @GetMapping("/list")
    public Result<List<GoodsServiceListVo>> list(@Validated PageParam pageParam, @Validated GoodsServiceSearchParam searchParam) {
        List<GoodsServiceListVo> list = goodsServiceService.list(searchParam);
        return Result.success(list);
    }

    /**
     * 商品服务详情
     *
     * @param id 主键ID
     * @return Result<ShopGoodsServiceInfoVo>
     */
    @GetMapping("/{id}")
    public Result<GoodsServiceInfoVo> info(@PathVariable("id") Integer id) {
        GoodsServiceInfoVo info = goodsServiceService.info(id);
        return Result.success(info);
    }

    /**
     * 商品服务添加
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody GoodsServiceParam addParam) {
        goodsServiceService.add(addParam);
        return Result.success();
    }

    /**
     * 商品服务编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody GoodsServiceParam editParam) {
        goodsServiceService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 商品服务删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        goodsServiceService.del(id);
        return Result.success();
    }

}
