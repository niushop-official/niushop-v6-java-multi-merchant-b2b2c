package com.niu.core.listener.separ;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.entity.recharge.RechargeOrder;
import com.niu.core.entity.vip_card.MemberVipCardOrder;
import com.niu.core.event.separ.SeparTradeSceneQueryEventDefiner;
import com.niu.core.mapper.recharge.RechargeOrderMapper;
import com.niu.core.mapper.vip_card.VipCardOrderMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 充值 / 付费会员卡分账 trade_scene 回填
 */
@EventCallback("core")
@Component
public class SeparTradeSceneQueryListener extends SeparTradeSceneQueryEventDefiner {

    @Resource
    private RechargeOrderMapper rechargeOrderMapper;

    @Resource
    private VipCardOrderMapper vipCardOrderMapper;

    @Override
    public SeparTradeSceneQueryResult handleCallback(SeparTradeSceneQueryEvent event) {
        SeparTradeSceneQueryResult result = new SeparTradeSceneQueryResult();
        if (event.getTradeId() == null || !StringUtils.hasText(event.getTradeType())) {
            return result;
        }
        String orderFrom = resolveOrderFrom(event);
        if (StringUtils.hasText(orderFrom)) {
            result.setOrderFrom(orderFrom);
        }
        return result;
    }

    private String resolveOrderFrom(SeparTradeSceneQueryEvent event) {
        if ("recharge_order_trade".equals(event.getTradeType())) {
            RechargeOrder order = rechargeOrderMapper.selectOne(new QueryWrapper<RechargeOrder>()
                    .eq("order_id", event.getTradeId())
                    .last("limit 1"));
            return order != null ? order.getOrderFrom() : null;
        }
        if ("vip_card_order_trade".equals(event.getTradeType())) {
            MemberVipCardOrder order = vipCardOrderMapper.selectOne(new QueryWrapper<MemberVipCardOrder>()
                    .eq("order_id", event.getTradeId())
                    .last("limit 1"));
            return order != null ? order.getOrderFrom() : null;
        }
        return null;
    }
}
