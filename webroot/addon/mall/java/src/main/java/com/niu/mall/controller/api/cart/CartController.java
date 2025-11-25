package com.niu.mall.controller.api.cart;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.mall.service.api.cart.IMallCartService;
import com.niu.mall.service.api.cart.param.MallCartCalculateParam;
import com.niu.mall.service.api.cart.param.MallCartDeleteParam;
import com.niu.mall.service.api.cart.param.MallCartEditParam;
import com.niu.mall.service.api.cart.param.MallCartParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("api/mall/cart")
@SaCheckLogin
public class CartController {

    @Resource
    IMallCartService cartService;

    /**
     * 添加购物车
     *
     * @return
     */
    @PostMapping("")
    public Result add(@Validated @RequestBody MallCartParam addParam) {
        cartService.add(addParam);
        return Result.success();
    }

    /**
     * 添加购物车
     *
     * @return
     */
    @PutMapping("")
    public Result edit(@Validated @RequestBody MallCartEditParam editParam) {
        cartService.edit(editParam);
        return Result.success();
    }

    /**
     * 添加购物车
     *
     * @return
     */
    @PutMapping("/delete")
    public Result edit(@Validated @RequestBody MallCartDeleteParam param) {
        cartService.del(param);
        return Result.success();
    }

    /**
     * 清空购物车
     *
     * @return
     */
    @DeleteMapping("/clear")
    public Result add() {
        cartService.clear();
        return Result.success();
    }

    /**
     * 获取购物车列表
     *
     * @return
     */
    @GetMapping("")
    public Result list() {
        return Result.success(cartService.list());
    }

    /**
     * 获取购物车商品列表
     *
     * @return
     */
    @GetMapping("/goods")
    public Result goodsList() {
        return Result.success(cartService.goodsList());
    }


    /**
     * 购物车数量
     *
     * @return
     */
    @GetMapping("/sum")
    public Result getSum(@RequestParam(value = "goods_id", defaultValue = "0") Integer goodsId) {
        return Result.success(cartService.getSum(goodsId));
    }


}
