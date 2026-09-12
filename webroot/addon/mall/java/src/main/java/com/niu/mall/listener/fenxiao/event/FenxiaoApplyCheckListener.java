package com.niu.mall.listener.fenxiao.event;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.event.fenxiao.FenxiaoApplyCheckEventDefiner;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.mapper.order.MallOrderMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商城成为分销商条件检测
 */
@Component
@EventCallback("mall")
public class FenxiaoApplyCheckListener extends FenxiaoApplyCheckEventDefiner {

    @Resource
    MallOrderMapper mallOrderMapper;

    /**
     * 统计会员商城订单数/金额，用于成为分销商条件判断
     */
    @Override
    public FenxiaoApplyCheckEventResult handleCallback(FenxiaoApplyCheckEvent event) {
        FenxiaoApplyCheckEventResult result = new FenxiaoApplyCheckEventResult();
        result.setOrderCount(0L);
        result.setOrderSum(BigDecimal.ZERO);
        Integer memberId = event.getMemberId();
        if (memberId == null) {
            return result;
        }
        String fenxiaoCondition = event.getFenxiaoCondition();
        String consumeCondition = event.getConsumeCondition();
        if ("1".equals(fenxiaoCondition)) {
            if ("1".equals(consumeCondition)) {
                result.setOrderCount(mallOrderMapper.selectCount(new QueryWrapper<MallOrder>()
                        .eq("member_id", memberId).ne("pay_time", 0)));
            } else if ("2".equals(consumeCondition)) {
                result.setOrderCount(mallOrderMapper.selectCount(new QueryWrapper<MallOrder>()
                        .eq("member_id", memberId).eq("status", OrderStatusEnum.FINISH.getStatus())));
            }
        } else if ("2".equals(fenxiaoCondition)) {
            QueryWrapper<MallOrder> wrapper = new QueryWrapper<MallOrder>().eq("member_id", memberId);
            if ("1".equals(consumeCondition)) {
                wrapper.ne("pay_time", 0);
            } else if ("2".equals(consumeCondition)) {
                wrapper.eq("status", OrderStatusEnum.FINISH.getStatus());
            }
            wrapper.select("IFNULL(SUM(order_money),0) AS order_sum");
            List<Map<String, Object>> maps = mallOrderMapper.selectMaps(wrapper);
            if (!maps.isEmpty() && maps.get(0).get("order_sum") != null) {
                result.setOrderSum(new BigDecimal(maps.get(0).get("order_sum").toString()));
            }
        }
        return result;
    }
}
