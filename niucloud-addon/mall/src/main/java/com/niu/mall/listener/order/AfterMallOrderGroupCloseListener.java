package com.niu.mall.listener.order;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.mall.entity.order.MallOrderGroup;
import com.niu.mall.entity.order.MallOrderGroupDiscount;
import com.niu.mall.event.order.OrderGroupCloseAfterEvent;
import com.niu.mall.mapper.order.MallOrderGroupDiscountMapper;
import com.niu.core.service.core.marketing.ICoreCouponMemberService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 订单组关闭后退还组级平台优惠券
 * 对应 PHP: addon\mall\app\listener\order_group\AfterMallOrderGroupClose
 */
@Slf4j
@EventListen("mall")
@Component
public class AfterMallOrderGroupCloseListener extends AbstractListener {

    @Resource
    private MallOrderGroupDiscountMapper mallOrderGroupDiscountMapper;

    @Resource
    private ICoreCouponMemberService coreCouponMemberService;

    public void handleEvent(OrderGroupCloseAfterEvent event) {
        MallOrderGroup orderGroup = event.getOrderGroup();
        if (orderGroup == null || orderGroup.getGroupId() == null || orderGroup.getGroupId() <= 0) {
            return;
        }
        Integer groupId = orderGroup.getGroupId();
        List<MallOrderGroupDiscount> groupDiscounts = mallOrderGroupDiscountMapper.selectList(
                new QueryWrapper<MallOrderGroupDiscount>().eq("group_id", groupId));
        if (ObjectUtil.isEmpty(groupDiscounts)) {
            return;
        }
        for (MallOrderGroupDiscount item : groupDiscounts) {
            if (item.getDiscountTypeId() == null || item.getDiscountTypeId() <= 0) {
                continue;
            }
            if ("platform_coupon".equals(item.getDiscountType())) {
                coreCouponMemberService.recover(item.getDiscountTypeId());
                log.info("recover group platform coupon, groupId={}, couponMemberId={}",
                        groupId, item.getDiscountTypeId());
            }
        }
    }
}
