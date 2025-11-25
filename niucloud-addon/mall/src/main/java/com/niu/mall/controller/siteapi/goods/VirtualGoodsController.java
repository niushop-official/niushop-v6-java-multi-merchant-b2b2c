package com.niu.mall.controller.siteapi.goods;

import com.niu.core.common.domain.Result;
import com.niu.mall.service.site.goods.IMallVirtualGoodsService;
import com.niu.mall.service.site.goods.param.MallVirtualGoodsParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("adminapi/mall/site/goods/virtual")
public class VirtualGoodsController {

    @Resource
    IMallVirtualGoodsService goodsService;

    /**
     * 商品添加
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody MallVirtualGoodsParam addParam) {
        goodsService.add(addParam);
        return Result.success();
    }

    /**
     * 商品编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody MallVirtualGoodsParam editParam) {
        goodsService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 商品添加/编辑数据
     * @return
     */
    @GetMapping("/init")
    public Result<?> init(@RequestParam(value = "goods_id", defaultValue = "0") Integer goodsId) {
        return Result.success(goodsService.getInit(goodsId));
    }
}
