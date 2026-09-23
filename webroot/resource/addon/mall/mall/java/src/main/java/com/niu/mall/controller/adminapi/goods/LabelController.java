package com.niu.mall.controller.adminapi.goods;

import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.admin.goods.param.*;
import com.niu.mall.service.admin.goods.IMallGoodsLabelService;
import com.niu.mall.service.admin.goods.vo.MallGoodsLabelInfoVo;
import com.niu.mall.service.admin.goods.vo.MallGoodsLabelListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.List;

/**
 * 商品标签控制器
 */

@RestController
@RequestMapping("adminapi/mall/admin/goods/label")
public class LabelController {

    @Resource
    IMallGoodsLabelService goodsLabelService;

    /**
     * 商品标签分页列表
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<MallGoodsLabelListVo>>
     */
    @GetMapping("")
    public Result<PageResult<MallGoodsLabelListVo>> list(@Validated PageParam pageParam, @Validated MallGoodsLabelSearchParam searchParam) {
        PageResult<MallGoodsLabelListVo> list = goodsLabelService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 商品标签列表
     *
     * @param searchParam 搜索条件
     * @return Result<PageResult<MallGoodsLabelListVo>>
     */
    @GetMapping("/list")
    public Result<List<MallGoodsLabelListVo>> list(@Validated MallGoodsLabelSearchParam searchParam) {
        List<MallGoodsLabelListVo> list = goodsLabelService.list(searchParam);
        return Result.success(list);
    }

    /**
     * 商品标签详情
     *
     * @param id 主键ID
     * @return Result<MallGoodsLabelInfoVo>
     */
    @GetMapping("/{id}")
    public Result<MallGoodsLabelInfoVo> info(@PathVariable("id") Integer id) {
        MallGoodsLabelInfoVo info = goodsLabelService.info(id);
        return Result.success(info);
    }

    /**
     * 商品标签添加
     *
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody MallGoodsLabelParam addParam) {
        goodsLabelService.add(addParam);
        return Result.success();
    }

    /**
     * 商品标签编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody MallGoodsLabelParam editParam) {
        goodsLabelService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 商品标签删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        goodsLabelService.del(id);
        return Result.success();
    }

    /**
     * 修改排序
     *
     * @return
     */
    @PutMapping("/sort")
    public Result<Object> modifySort(@Validated @RequestBody EditGoodsLabelSortParam editParam) {
        goodsLabelService.modifySort(editParam);
        return Result.success();
    }

    /**
     * 修改状态
     *
     * @return
     */
    @PutMapping("/status")
    public Result<Object> modifyStatus(@Validated @RequestBody EditGoodsLabelStatusParam editParam) {
        goodsLabelService.modifyStatus(editParam);
        return Result.success();
    }

    /**
     * 复制商品标签
     *
     * @param id 主键ID
     * @return Result<Object>
     */
    @PostMapping("/copy/{label_id}")
    public Result<Object> copy(@PathVariable("label_id") Integer id) {
         goodsLabelService.copy(id);
        return Result.success();
    }
}
