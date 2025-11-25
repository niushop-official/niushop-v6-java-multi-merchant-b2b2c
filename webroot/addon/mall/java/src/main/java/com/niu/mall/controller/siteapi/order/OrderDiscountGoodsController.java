package com.niu.mall.controller.siteapi.order;

import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.site.order.param.MallOrderDiscountGoodsParam;
import com.niu.mall.service.site.order.param.MallOrderDiscountGoodsSearchParam;
import com.niu.mall.service.site.order.IMallOrderDiscountGoodsService;
import com.niu.mall.service.site.order.vo.MallOrderDiscountGoodsInfoVo;
import com.niu.mall.service.site.order.vo.MallOrderDiscountGoodsListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;

/**
 * 订单项优惠控制器
 */

@RestController
@RequestMapping("adminapi/mall/site/goods/order/discount")
public class OrderDiscountGoodsController {

    @Resource
    IMallOrderDiscountGoodsService iMallOrderDiscountGoodsService;

    /**
     * 订单项优惠列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<MallOrderDiscountGoodsListVo>>
     */
    @GetMapping("/list")
    public Result<PageResult<MallOrderDiscountGoodsListVo>> list(@Validated PageParam pageParam,
                                                                 @Validated MallOrderDiscountGoodsSearchParam searchParam) {
        PageResult<MallOrderDiscountGoodsListVo> list = iMallOrderDiscountGoodsService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 订单项优惠详情
     * @param id 主键ID
     * @return Result<MallOrderDiscountGoodsInfoVo>
     */
    @GetMapping("/info")
    public Result<MallOrderDiscountGoodsInfoVo> info(@Validated @RequestParam("id") Integer id) {
        MallOrderDiscountGoodsInfoVo info = iMallOrderDiscountGoodsService.info(id);
        return Result.success(info);
    }

    /**
     * 订单项优惠添加
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("/add")
    public Result<Object> add(@Validated @RequestBody MallOrderDiscountGoodsParam addParam) {
        iMallOrderDiscountGoodsService.add(addParam);
        return Result.success();
    }

    /**
     * 订单项优惠编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PostMapping("/edit")
    public Result<Object> edit(Integer id, @Validated @RequestBody MallOrderDiscountGoodsParam editParam) {
        iMallOrderDiscountGoodsService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 订单项优惠删除
     * @param id 参数
     * @return Result<Object>
     */
    @PostMapping("/del")
    public Result<Object> del(@Validated @RequestBody Integer id) {
        iMallOrderDiscountGoodsService.del(id);
        return Result.success();
    }

}
