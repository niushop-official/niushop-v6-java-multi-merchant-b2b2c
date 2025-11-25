package com.niu.mall.controller.siteapi.order;

import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.site.order.param.MallOrderDiscountParam;
import com.niu.mall.service.site.order.param.MallOrderDiscountSearchParam;
import com.niu.mall.service.site.order.IMallOrderDiscountService;
import com.niu.mall.service.site.order.vo.MallOrderDiscountInfoVo;
import com.niu.mall.service.site.order.vo.MallOrderDiscountListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;

/**
 * 订单优惠控制器
 */

@RestController
@RequestMapping("adminapi/mall/site/order/discount")
public class OrderDiscountController {

    @Resource
    IMallOrderDiscountService iMallOrderDiscountService;

    /**
     * 订单优惠列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<MallOrderDiscountListVo>>
     */
    @GetMapping("/list")
    public Result<PageResult<MallOrderDiscountListVo>> list(@Validated PageParam pageParam,
                                                            @Validated MallOrderDiscountSearchParam searchParam) {
        PageResult<MallOrderDiscountListVo> list = iMallOrderDiscountService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 订单优惠详情
     * @param id 主键ID
     * @return Result<MallOrderDiscountInfoVo>
     */
    @GetMapping("/info")
    public Result<MallOrderDiscountInfoVo> info(@Validated @RequestParam("id") Integer id) {
        MallOrderDiscountInfoVo info = iMallOrderDiscountService.info(id);
        return Result.success(info);
    }

    /**
     * 订单优惠添加
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("/add")
    public Result<Object> add(@Validated @RequestBody MallOrderDiscountParam addParam) {
        iMallOrderDiscountService.add(addParam);
        return Result.success();
    }

    /**
     * 订单优惠编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PostMapping("/edit")
    public Result<Object> edit(Integer id, @Validated @RequestBody MallOrderDiscountParam editParam) {
        iMallOrderDiscountService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 订单优惠删除
     * @param id 参数
     * @return Result<Object>
     */
    @PostMapping("/del")
    public Result<Object> del(@Validated @RequestBody Integer id) {
        iMallOrderDiscountService.del(id);
        return Result.success();
    }

}
