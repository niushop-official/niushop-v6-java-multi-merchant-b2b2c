package com.niu.mall.listener.separ;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.event.separ.SeparTradeSceneQueryEventDefiner;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.mapper.order.MallOrderMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 商城订单分账 trade_scene 回填
 */
@EventCallback("mall")
@Component
public class MallSeparTradeSceneQueryListener extends SeparTradeSceneQueryEventDefiner {

    @Resource
    private MallOrderMapper mallOrderMapper;

    @Override
    public SeparTradeSceneQueryResult handleCallback(SeparTradeSceneQueryEvent event) {
        SeparTradeSceneQueryResult result = new SeparTradeSceneQueryResult();
        if (event.getTradeId() == null || !"order_trade".equals(event.getTradeType())) {
            return result;
        }
        QueryWrapper<MallOrder> queryWrapper = new QueryWrapper<MallOrder>()
                .eq("order_id", event.getTradeId());
        if (event.getSiteId() != null) {
            queryWrapper.eq("site_id", event.getSiteId());
        }
        MallOrder order = mallOrderMapper.selectOne(queryWrapper.last("limit 1"));
        if (order != null && StringUtils.hasText(order.getOrderFrom())) {
            result.setOrderFrom(order.getOrderFrom());
        }
        return result;
    }
}
