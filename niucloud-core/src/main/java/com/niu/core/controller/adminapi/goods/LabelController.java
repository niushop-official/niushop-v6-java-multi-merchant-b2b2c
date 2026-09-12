package com.niu.core.controller.adminapi.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.goods.IGoodsLabelService;
import com.niu.core.service.admin.goods.param.EditGoodsLabelSortParam;
import com.niu.core.service.admin.goods.param.EditGoodsLabelStatusParam;
import com.niu.core.service.admin.goods.param.GoodsLabelParam;
import com.niu.core.service.admin.goods.param.GoodsLabelSearchParam;
import com.niu.core.service.admin.goods.vo.GoodsLabelInfoVo;
import com.niu.core.service.admin.goods.vo.GoodsLabelListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品标签控制器
 * 对应 PHP: app\adminapi\controller\goods\admin\Label
 */
@RestController
@RequestMapping("adminapi/basic/admin/goods/label")
public class LabelController {

    @Resource
    private IGoodsLabelService goodsLabelService;

    /**
     * 商品标签分页列表
     */
    @GetMapping("")
    public Result<PageResult<GoodsLabelListVo>> page(@Validated PageParam pageParam,
                                                     @Validated GoodsLabelSearchParam searchParam) {
        return Result.success(goodsLabelService.page(pageParam, searchParam));
    }

    /**
     * 商品标签列表
     *
     * @param searchParam 搜索条件
     * @return 标签列表
     */
    @GetMapping("/list")
    public Result<List<GoodsLabelListVo>> list(@Validated GoodsLabelSearchParam searchParam) {
        return Result.success(goodsLabelService.list(searchParam));
    }

    /**
     * 商品标签详情
     */
    @GetMapping("/{id}")
    public Result<GoodsLabelInfoVo> info(@PathVariable("id") Integer id) {
        return Result.success(goodsLabelService.info(id));
    }

    /**
     * 添加商品标签
     */
    @PostMapping("")
    public Result<?> add(@Validated @RequestBody GoodsLabelParam addParam) {
        Integer id = goodsLabelService.add(addParam);
        return Result.success(Map.of("id", id));
    }

    /**
     * 编辑商品标签
     */
    @PutMapping("/{id}")
    public Result<?> edit(@PathVariable("id") Integer id, @Validated @RequestBody GoodsLabelParam editParam) {
        goodsLabelService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 删除商品标签
     */
    @DeleteMapping("/{id}")
    public Result<?> del(@PathVariable("id") Integer id) {
        goodsLabelService.del(id);
        return Result.success();
    }

    /**
     * 修改排序
     */
    @PutMapping("/sort")
    public Result<?> modifySort(@Validated @RequestBody EditGoodsLabelSortParam editParam) {
        goodsLabelService.modifySort(editParam);
        return Result.success();
    }

    /**
     * 修改状态
     */
    @PutMapping("/status")
    public Result<?> modifyStatus(@Validated @RequestBody EditGoodsLabelStatusParam editParam) {
        goodsLabelService.modifyStatus(editParam);
        return Result.success();
    }

    /**
     * 复制商品标签
     */
    @PostMapping("/copy/{label_id}")
    public Result<?> copy(@PathVariable("label_id") Integer id) {
        goodsLabelService.copy(id);
        return Result.success();
    }
}
