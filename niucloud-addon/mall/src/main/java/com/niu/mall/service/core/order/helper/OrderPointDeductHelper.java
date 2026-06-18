package com.niu.mall.service.core.order.helper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.entity.member.MemberAccountLog;
import com.niu.core.enums.member.AccountTypeEnum;
import com.niu.core.mapper.member.MemberAccountLogMapper;
import com.niu.core.service.core.point_deduct.ICorePointDeductService;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderDiscount;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.enums.order.OrderCloseTypeEnum;
import com.niu.mall.mapper.order.MallOrderDiscountMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 订单积分抵扣：关单退还等辅助逻辑（对齐 PHP AfterMallOrderClose，并兼容 use_point 未落库的历史数据）
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

    /**
     * 非退款关单时退还抵扣积分（与 PHP AfterMallOrderClose 一致；退款关单由订单项退款处理）
     */
    public void refundOnOrderClose(MallOrder order, String closeType, List<MallOrderGoods> orderGoods) {
        if (OrderCloseTypeEnum.REFUND_CLOSE.getType().equals(closeType)) {
            return;
        }
        Integer refundUsePoint = resolveRefundUsePoint(order, orderGoods);
        if (refundUsePoint == null || refundUsePoint <= 0) {
            return;
        }
        if (hasPointRefundLog(order.getMemberId(), order.getOrderNo())) {
            return;
        }
        corePointDeductService.refundPointDeduct(
                order.getSiteId(), order.getMemberId(), refundUsePoint, order.getOrderNo());
    }

    /**
     * 解析应退积分：order.use_point → order_goods 汇总 → mall_order_discount（point_deduct）
     */
    public Integer resolveRefundUsePoint(MallOrder order, List<MallOrderGoods> orderGoods) {
        if (order.getUsePoint() != null && order.getUsePoint() > 0) {
            return order.getUsePoint();
        }
        if (orderGoods != null && !orderGoods.isEmpty()) {
            int fromGoods = orderGoods.stream()
                    .mapToInt(item -> item.getUsePoint() != null ? item.getUsePoint() : 0)
                    .sum();
            if (fromGoods > 0) {
                return fromGoods;
            }
        }
        List<MallOrderDiscount> discounts = mallOrderDiscountMapper.selectList(
                new LambdaQueryWrapper<MallOrderDiscount>()
                        .eq(MallOrderDiscount::getOrderId, order.getOrderId())
                        .eq(MallOrderDiscount::getDiscountType, "point_deduct"));
        if (discounts == null || discounts.isEmpty()) {
            return 0;
        }
        return discounts.stream()
                .mapToInt(d -> d.getNum() != null ? d.getNum() : 0)
                .sum();
    }

    /**
     * 下单落库用：basic.use_point 为空时从商品行汇总（与 PHP 子单 use_point 分摊一致）
     */
    public static int resolveOrderUsePoint(Integer basicUsePoint, Iterable<MallOrderGoods> orderGoods) {
        if (basicUsePoint != null && basicUsePoint > 0) {
            return basicUsePoint;
        }
        if (orderGoods == null) {
            return 0;
        }
        int sum = 0;
        for (MallOrderGoods item : orderGoods) {
            if (item.getUsePoint() != null) {
                sum += item.getUsePoint();
            }
        }
        return sum;
    }

    private boolean hasPointRefundLog(Integer memberId, String orderNo) {
        if (memberId == null || orderNo == null || orderNo.isEmpty()) {
            return false;
        }
        Long count = memberAccountLogMapper.selectCount(
                new LambdaQueryWrapper<MemberAccountLog>()
                        .eq(MemberAccountLog::getMemberId, memberId)
                        .eq(MemberAccountLog::getRelatedId, orderNo)
                        .eq(MemberAccountLog::getAccountType, AccountTypeEnum.POINT.getType())
                        .eq(MemberAccountLog::getFromType, POINT_REFUND_FROM_TYPE)
                        .gt(MemberAccountLog::getAccountData, 0));
        return count != null && count > 0;
    }
}
