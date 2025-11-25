package com.niu.core.controller.siteapi.shopdelivery;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.site.shop_delivery.IDeliveryDeliverService;
import com.niu.core.service.site.shop_delivery.param.DeliveryDeliverParam;
import com.niu.core.service.site.shop_delivery.param.DeliveryDeliverSearchParam;
import com.niu.core.service.site.shop_delivery.vo.DeliveryDeliverInfoVo;
import com.niu.core.service.site.shop_delivery.vo.DeliveryDeliverListVo;
import jakarta.annotation.Resource;
import jdk.jfr.Description;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 配送员控制器
 */

@RestController
@RequestMapping("adminapi/delivery/site/shop_delivery/staff")
@Description("配送员管理")
public class DeliverController {

    @Resource
    IDeliveryDeliverService deliveryDeliverService;

    /**
     * 配送员列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopDeliveryDeliverListVo>>
     */
    @GetMapping("")
    @Description("配送员分页列表")
    public Result<PageResult<DeliveryDeliverListVo>> list(@Validated PageParam pageParam, @Validated DeliveryDeliverSearchParam searchParam) {
        PageResult<DeliveryDeliverListVo> list = deliveryDeliverService.list(pageParam, searchParam);
        return Result.success(list);
    }


    /**
     * 配送员列表
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopDeliveryDeliverListVo>>
     */
    @GetMapping("/list")
    @Description("配送员列表")
    public Result<List<DeliveryDeliverListVo>> getList(@Validated DeliveryDeliverSearchParam searchParam) {
        List<DeliveryDeliverListVo> list = deliveryDeliverService.getList(searchParam);
        return Result.success(list);
    }
    /**
     * 配送员详情
     * @param id 主键ID
     * @return Result<ShopDeliveryDeliverInfoVo>
     */
    @GetMapping("/{id}")
    @Description("配送员详情")
    public Result<DeliveryDeliverInfoVo> info(@PathVariable("id") Integer id) {
        DeliveryDeliverInfoVo info = deliveryDeliverService.info(id);
        return Result.success(info);
    }

    /**
     * 配送员添加
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("")
    @Description("添加配送员")
    public Result<Object> add(@Validated @RequestBody DeliveryDeliverParam addParam) {
        deliveryDeliverService.add(addParam);
        return Result.success();
    }

    /**
     * 配送员编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    @Description("编辑配送员")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody DeliveryDeliverParam editParam) {
        deliveryDeliverService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 配送员删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    @Description("删除配送员")
    public Result<Object> del(@PathVariable("id") Integer id) {
        deliveryDeliverService.del(id);
        return Result.success();
    }

}
