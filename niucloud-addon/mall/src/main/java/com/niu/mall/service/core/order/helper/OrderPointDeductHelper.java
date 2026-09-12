package com.niu.mall.service.core.order.helper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.entity.member.MemberAccountLog;
import com.niu.core.enums.member.AccountTypeEnum;
import com.niu.core.mapper.member.MemberAccountLogMapper;
import com.niu.core.service.core.point_deduct.ICorePointDeductService;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderDiscount;
import com.niu.mall.entity.order.MallOrderDiscountGoods;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.enums.order.OrderCloseTypeEnum;
import com.niu.mall.enums.order.OrderDiscountTypeEnum;
import com.niu.mall.enums.order.OrderGoodsRefundEnum;
import com.niu.mall.mapper.order.MallOrderDiscountGoodsMapper;
import com.niu.mall.mapper.order.MallOrderDiscountMapper;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 订单积分抵扣辅助：关单退还、落库积分解析（兼容 use_point 未落库的历史数据）
 */
@Component
public class OrderPointDeductHelper {

    private static final String POINT_REFUND_FROM_TYPE = "order_refund";

    @Resource
    private ICorePointDeductService corePointDeductService;

    @Resource
    private MemberAccountLogMapper memberAccountLogMapper;

    @Resource
    private MallOrderDiscountMapper mallOrderDiscountMapper;

    @Resource
    private MallOrderDiscountGoodsMapper mallOrderDiscountGoodsMapper;

    @Resource
    private MallOrderGoodsMapper mallOrderGoodsMapper;

    /**
     * 非退款关单时退还抵扣积分；退款关单由订单项退款流程处理。
     */
    public void refundOnOrderClose(MallOrder order, String closeType, List<MallOrderGoods> orderGoods) {
        // 【分支】closeType=退款关单：积分由退款流程退还，此处直接返回避免重复
        if (OrderCloseTypeEnum.REFUND_CLOSE.getType().equals(closeType)) {
            return;
        }
        // refundUsePoint：本单实际抵扣积分（order → 行项 → discount 三级解析）
        Integer refundUsePoint = resolveRefundUsePoint(order, orderGoods);
        // 【分支】无有效抵扣积分则无需退还
        if (refundUsePoint == null || refundUsePoint <= 0) {
            return;
        }
        // 【分支】member_account_log 已有 order_refund 流水则跳过，防重复退积分
        if (hasPointRefundLog(order.getMemberId(), order.getOrderNo())) {
            return;
        }
        // 调用积分服务退还 refundUsePoint 到会员账户
        corePointDeductService.refundPointDeduct(
                order.getSiteId(), order.getMemberId(), BigDecimal.valueOf(refundUsePoint), order.getOrderNo());
    }

    /**
     * 解析应退积分：order.use_point → order_goods 汇总 → mall_order_discount（point_deduct）
     */
    public Integer resolveRefundUsePoint(MallOrder order, List<MallOrderGoods> orderGoods) {
        // 【分支】优先级1：mall_order.use_point 已落库且 >0，直接取整返回
        if (order.getUsePoint() != null && order.getUsePoint().compareTo(BigDecimal.ZERO) > 0) {
            return order.getUsePoint().intValue();
        }
        // 【分支】优先级2：order.use_point 为空/0 时，汇总 orderGoods 各行 use_point
        if (orderGoods != null && !orderGoods.isEmpty()) {
            // 【循环】orderGoods 每行 item.use_point 累加为 fromGoods
            int fromGoods = orderGoods.stream()
                    .mapToInt(item -> item.getUsePoint() != null ? item.getUsePoint() : 0)
                    .sum();
            if (fromGoods > 0) {
                return fromGoods;
            }
        }
        // 【分支】优先级3：查 mall_order_discount（discount_type=point_deduct）汇总 num
        List<MallOrderDiscount> discounts = mallOrderDiscountMapper.selectList(
                new LambdaQueryWrapper<MallOrderDiscount>()
                        .eq(MallOrderDiscount::getOrderId, order.getOrderId())
                        .eq(MallOrderDiscount::getDiscountType, "point_deduct"));
        if (discounts == null || discounts.isEmpty()) {
            return 0;
        }
        // 【循环】discounts 每条 d.num 累加为应退积分
        return discounts.stream()
                .mapToInt(d -> d.getNum() != null ? d.getNum() : 0)
                .sum();
    }

    /**
     * 下单落库：basic.use_point 为空或 0 时，从 orderGoods 行项汇总 use_point。
     */
    public static int resolveOrderUsePoint(BigDecimal basicUsePoint, Iterable<MallOrderGoods> orderGoods) {
        // 【分支】basic.use_point 已写入且 >0，作为 mall_order.use_point 落库值
        if (basicUsePoint != null && basicUsePoint.compareTo(BigDecimal.ZERO) > 0) {
            return basicUsePoint.intValue();
        }
        if (orderGoods == null) {
            return 0;
        }
        int sum = 0;
        // 【循环】orderGoods 每行 item.use_point 累加，兼容 basic 未落 use_point 的历史数据
        for (MallOrderGoods item : orderGoods) {
            if (item.getUsePoint() != null) {
                sum += item.getUsePoint();
            }
        }
        return sum;
    }

    /**
     * 解析订单项应退积分（对齐 PHP：优先 order_goods.use_point；兼容多站点汇总后行项未落库场景）。
     */
    public RefundPointDeduct resolveItemRefundPoint(MallOrder order, MallOrderGoods orderGoods) {
        RefundPointDeduct result = new RefundPointDeduct();
        if (orderGoods == null) {
            return result;
        }
        int usePoint = orderGoods.getUsePoint() != null ? orderGoods.getUsePoint() : 0;
        BigDecimal pointMoney = orderGoods.getPointDeductMoney() != null
                ? orderGoods.getPointDeductMoney() : BigDecimal.ZERO;

        if (usePoint <= 0) {
            MallOrderDiscountGoods discountGoods = mallOrderDiscountGoodsMapper.selectOne(
                    new LambdaQueryWrapper<MallOrderDiscountGoods>()
                            .eq(MallOrderDiscountGoods::getOrderId, orderGoods.getOrderId())
                            .eq(MallOrderDiscountGoods::getOrderGoodsId, String.valueOf(orderGoods.getOrderGoodsId()))
                            .eq(MallOrderDiscountGoods::getDiscountType, OrderDiscountTypeEnum.POINT_DEDUCT.getType())
                            .last("limit 1"));
            if (discountGoods != null && discountGoods.getNum() != null && discountGoods.getNum() > 0) {
                usePoint = discountGoods.getNum();
                pointMoney = discountGoods.getMoney() != null ? discountGoods.getMoney() : BigDecimal.ZERO;
            }
        }

        if (usePoint <= 0 && order != null) {
            int orderUsePoint = order.getUsePoint() != null ? order.getUsePoint().intValue() : 0;
            int refundedPoint = order.getRefundDeductPoint() != null ? order.getRefundDeductPoint() : 0;
            int remainingPoint = orderUsePoint - refundedPoint;
            if (remainingPoint > 0 && isLastGoodsRefundForOrder(order.getOrderId(), orderGoods.getOrderGoodsId())) {
                usePoint = remainingPoint;
                BigDecimal orderPointMoney = order.getPointDeductMoney() != null
                        ? order.getPointDeductMoney() : BigDecimal.ZERO;
                BigDecimal refundedPointMoney = order.getRefundPointDeductMoney() != null
                        ? order.getRefundPointDeductMoney() : BigDecimal.ZERO;
                pointMoney = orderPointMoney.subtract(refundedPointMoney).max(BigDecimal.ZERO)
                        .setScale(2, RoundingMode.HALF_UP);
            }
        }

        result.setUsePoint(Math.max(usePoint, 0));
        result.setPointDeductMoney(pointMoney.max(BigDecimal.ZERO));
        return result;
    }

    private boolean isLastGoodsRefundForOrder(Integer orderId, Integer currentOrderGoodsId) {
        if (orderId == null || currentOrderGoodsId == null) {
            return false;
        }
        Long unfinished = mallOrderGoodsMapper.selectCount(
                new LambdaQueryWrapper<MallOrderGoods>()
                        .eq(MallOrderGoods::getOrderId, orderId)
                        .ne(MallOrderGoods::getOrderGoodsId, currentOrderGoodsId)
                        .ne(MallOrderGoods::getStatus, OrderGoodsRefundEnum.REFUND_FINISH.getStatus()));
        return unfinished != null && unfinished == 0;
    }

    private boolean hasPointRefundLog(Integer memberId, String orderNo) {
        // 【分支】memberId/orderNo 缺失无法定位流水，视为未退还
        if (memberId == null || orderNo == null || orderNo.isEmpty()) {
            return false;
        }
        // 查 member_account_log：account_type=point、from_type=order_refund、related_id=orderNo 的正向流水
        Long count = memberAccountLogMapper.selectCount(
                new LambdaQueryWrapper<MemberAccountLog>()
                        .eq(MemberAccountLog::getMemberId, memberId)
                        .eq(MemberAccountLog::getRelatedId, orderNo)
                        .eq(MemberAccountLog::getAccountType, AccountTypeEnum.POINT.getType())
                        .eq(MemberAccountLog::getFromType, POINT_REFUND_FROM_TYPE)
                        .gt(MemberAccountLog::getAccountData, 0));
        return count != null && count > 0;
    }

    @Data
    public static class RefundPointDeduct {
        private int usePoint;
        private BigDecimal pointDeductMoney = BigDecimal.ZERO;
    }
}
