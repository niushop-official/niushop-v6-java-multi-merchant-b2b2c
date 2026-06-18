package com.niu.mall.controller.siteapi.order;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.enums.order.OrderTypeEnum;
import com.niu.mall.service.site.order.IMallOrderFinishService;
import com.niu.mall.service.site.order.IMallOrderService;
import com.niu.mall.service.site.order.param.*;
import com.niu.mall.service.site.order.vo.EditDeliveryVo;
import com.niu.mall.service.site.order.vo.MallOrderListVo;
import com.niu.mall.service.site.order.vo.MallOrderMemberConsumeListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 订单控制器
 */

@RestController
@RequestMapping("adminapi/mall/site/order")
public class OrderController {

    @Resource
    IMallOrderService mallOrderService;

    @Resource
    IMallOrderFinishService mallOrderFinishService;

    /**
     * 订单列表
     *
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopOrderListVo>>
     */
    @GetMapping("/list")
    public Result<PageResult<MallOrderListVo>> list(@Validated PageParam pageParam, @Validated MallOrderSearchParam searchParam, @RequestParam Map<String, String> allParams) {
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
     *
     * @param id 主键ID
     * @return Result<ShopOrderInfoVo>
     */
    @GetMapping("/detail/{id}")
    public Result<Object> info(@PathVariable("id") Integer id) {
        return Result.success(mallOrderService.detail(id));
    }

    /**
     * 关闭订单
     *
     * @param id
     * @return
     */
    @PutMapping("/close/{id}")
    public Result orderClose(@PathVariable("id") Integer id) {
        mallOrderService.close(id);
        return Result.success();
    }

    /**
     * 订单完成
     *
     * @param id
     * @return
     */
    @PutMapping("/finish/{id}")
    public Result orderFinish(@PathVariable("id") Integer id) {
        mallOrderFinishService.finish(id);
        return Result.success();
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
     * 订单类型
     *
     * @return
     */
    @GetMapping("/type")
    public Result type() {
        return Result.success(OrderTypeEnum.getOrderType(null));
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
     * 订单调价
     *
     * @return
     */
    @PutMapping("/edit_price")
    public Result editPrice(@Validated @RequestBody EditPriceParam param) {
        mallOrderService.editPrice(param);
        return Result.success();
    }

    /**
     * 获取订单配送修改信息
     *
     * @return
     */
    @GetMapping("/edit_delivery")
    public Result<EditDeliveryVo> getEditDeliveryData(EditDeliveryParam param) {
        return Result.success(mallOrderService.getEditDeliveryData(param));
    }

    /**
     * 获取已选订单项总重量
     *
     * @return
     */
    @GetMapping("/select/weight")
    public Result weight(@Validated MallOrderGoodsWeightParam param) {
        return Result.success(mallOrderService.getSelectOrderGoodsWeight(param));
    }

    /**
     * 修改订单配送
     *
     * @return
     */
    @PutMapping("/edit_delivery")
    public Result editDelivery(@Validated @RequestBody EditDeliveryParam param) {
        mallOrderService.editDelivery(param);
        return Result.success();
    }

    /**
     * 修改订单配送
     *
     * @return
     */
    @PostMapping("/delete")
    public Result delete(@Validated @RequestBody MallOrderDeleteParam param) {
        mallOrderService.delete(param);
        return Result.success(true);
    }

    /**
     * 订单列表
     *
     * @param pageParam 分页
     * @param memberId  搜索条件
     * @return Result<PageResult<MallOrderMemberConsumeListVo>>
     */
    @GetMapping("/consume/list")
    public Result<PageResult<MallOrderMemberConsumeListVo>> list(@Validated PageParam pageParam, @RequestParam("member_id") Integer memberId) {
        PageResult<MallOrderMemberConsumeListVo> list = mallOrderService.consumelists(pageParam, memberId);
        return Result.success(list);
    }
}
