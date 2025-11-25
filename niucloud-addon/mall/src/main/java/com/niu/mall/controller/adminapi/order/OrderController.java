package com.niu.mall.controller.adminapi.order;

import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.service.admin.order.param.*;
import com.niu.mall.service.admin.order.IMallOrderService;
import com.niu.mall.service.admin.order.vo.MallOrderInfoVo;
import com.niu.mall.service.admin.order.vo.MallOrderListVo;
import com.niu.core.common.domain.PageParam;
import com.niu.mall.service.admin.order.vo.SettlementInfoListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;

/**
 * 订单控制器
 */

@RestController
@RequestMapping("adminapi/mall/admin/order")
public class OrderController {

    @Resource
    IMallOrderService mallOrderService;
    

    @GetMapping("/list")
    public Result<PageResult<MallOrderListVo>> list(@Validated PageParam pageParam, @Validated MallOrderSearchParam searchParam) {
        PageResult<MallOrderListVo> list = mallOrderService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 订单详情
     * @param id 主键ID
     * @return Result<ShopOrderInfoVo>
        */
    @GetMapping("/detail/{id}")
    public Result<MallOrderInfoVo> info(@PathVariable("id") Integer id) {
        MallOrderInfoVo info = mallOrderService.detail(id);
        return Result.success(info);
    }

    /**
     * 商家备注
     *
     * @param param
     * @return
     */
    @PutMapping("/shop_remark")
    public Result setShopRemark(@Validated @RequestBody SetShopRemarkParam param) {
        mallOrderService.setShopRemark(param);
        return Result.success();
    }

    /**
     * 订单状态
     *
     * @return
     */
    @GetMapping("/status")
    public Result status() {
        return Result.success(OrderStatusEnum.getMap());
    }

    /**
     * 订单来源
     *
     * @return
     */
    @GetMapping("/from")
    public Result getOrderFrom() {
        return Result.success(ChannelEnum.getMap());
    }

    /**
     * 订单支付方式
     *
     * @return
     */
    @GetMapping("/pay/type")
    public Result getPayType() {
        return Result.success(PayTypeEnum.getType());
    }
}
