package com.niu.core.controller.siteapi.goods;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.site.goods.param.*;
import com.niu.core.service.site.goods.IGoodsAttrService;
import com.niu.core.service.site.goods.vo.GoodsAttrInfoVo;
import com.niu.core.service.site.goods.vo.GoodsAttrListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * 商品参数控制器
 */
@RestController
@RequestMapping("adminapi/basic/site/goods")
public class AttrController {

    @Resource
    IGoodsAttrService goodsAttrService;

    /**
     * 商品参数分页列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopGoodsAttrListVo>>
     */
    @GetMapping("/attr")
    public Result<PageResult<GoodsAttrListVo>> page(@Validated PageParam pageParam, @Validated GoodsAttrSearchParam searchParam) {
        PageResult<GoodsAttrListVo> list = goodsAttrService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 商品参数列表
     *
     * @return Result<List<ShopGoodsAttrListVo>>
     */
    @GetMapping("/attr_list")
    public Result<List<GoodsAttrListVo>> list(@Validated GoodsAttrSearchParam searchParam) {
        List<GoodsAttrListVo> list = goodsAttrService.list(searchParam);
        return Result.success(list);
    }

    /**
     * 商品参数列表
     *
     * @param id 主键ID
     * @return Result<ShopGoodsAttrInfoVo>
     */
    @GetMapping("/attr/{id}")
    public Result info(@Validated @PathVariable("id") Integer id) {
        GoodsAttrInfoVo info = goodsAttrService.info(id);
        return Result.success(ObjectUtil.defaultIfNull(info, new LinkedList<>()));
    }

    /**
     * 商品参数添加
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("/attr")
    public Result<Object> add(@Validated @RequestBody GoodsAttrParam addParam) {
        goodsAttrService.add(addParam);
        return Result.success();
    }

    /**
     * 商品参数编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/attr/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody GoodsAttrParam editParam) {
        goodsAttrService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 商品参数删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/attr/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        goodsAttrService.del(id);
        return Result.success();
    }

    /**
     * 修改商品参数排序号
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/attr/sort")
    public Result<Object> modifySort(@Validated @RequestBody EditGoodsAttrSortParam editParam) {
        goodsAttrService.modifySort(editParam);
        return Result.success();
    }

    /**
     * 修改商品参数名称
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/attr/attr_name")
    public Result<Object> modifyAttrName(@Validated @RequestBody EditGoodsAttrNameParam editParam) {
        goodsAttrService.modifyAttrName(editParam);
        return Result.success();
    }

    /**
     * 修改商品参数值
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/attr/attr_value")
    public Result<Object> modifyAttrValueFormat(@Validated @RequestBody EditGoodsAttrValueParam editParam) {
        goodsAttrService.modifyAttrValueFormat(editParam);
        return Result.success();
    }

}
