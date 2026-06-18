package com.niu.mall.controller.adminapi.order;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.enums.order.OrderTypeEnum;
import com.niu.mall.service.admin.order.param.*;
import com.niu.mall.service.admin.order.IMallOrderService;
import com.niu.mall.service.admin.order.vo.MallOrderInfoVo;
import com.niu.mall.service.admin.order.vo.MallOrderListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;

import java.util.Map;

/**
 * 订单控制器
 */

@RestController
@RequestMapping("adminapi/mall/admin/order")
public class OrderController {

    @Resource
    IMallOrderService mallOrderService;
    

    @GetMapping("/list")
    public Result<PageResult<MallOrderListVo>> list(@Validated PageParam pageParam, @Validated MallOrderSearchParam searchParam,@RequestParam Map<String, String> allParams) {
        if (allParams.containsKey("extend_field_1") && ObjectUtil.isNotEmpty(allParams.get("extend_field_1"))) {
            searchParam.setExtendField1(Integer.parseInt(allParams.get("extend_field_1")));
        }
        if (allParams.containsKey("extend_field_2") && ObjectUtil.isNotEmpty(allParams.get("extend_field_2"))) {
            searchParam.setExtendField2(Integer.parseInt(allParams.get("extend_field_2")));
        }
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

    /**
     * 订单支付方式
     *
     * @return
     */
    @GetMapping("/type")
    public Result getType() {
        return Result.success(OrderTypeEnum.getOrderType(null));
    }
}
