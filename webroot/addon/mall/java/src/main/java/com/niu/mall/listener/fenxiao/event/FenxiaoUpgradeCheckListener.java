package com.niu.mall.listener.fenxiao.event;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.event.fenxiao.FenxiaoUpgradeCheckEventDefiner;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.mapper.order.MallOrderMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商城分销商等级升级订单统计
 */
@Component
@EventCallback("mall")
public class FenxiaoUpgradeCheckListener extends FenxiaoUpgradeCheckEventDefiner {

    @Resource
    MallOrderMapper mallOrderMapper;

    /**
     * 统计分销商已完成商城订单数/金额
     */
    @Override
    public FenxiaoUpgradeCheckEventResult handleCallback(FenxiaoUpgradeCheckEvent event) {
        FenxiaoUpgradeCheckEventResult result = new FenxiaoUpgradeCheckEventResult();
        result.setOrderNum(0L);
        result.setOrderMoney(BigDecimal.ZERO);
        Integer memberId = event.getFenxiaoMemberId();
        if (memberId == null) {
            return result;
        }
        result.setOrderNum(mallOrderMapper.selectCount(new QueryWrapper<MallOrder>()
                .eq("member_id", memberId).eq("status", OrderStatusEnum.FINISH.getStatus())));
        List<Map<String, Object>> maps = mallOrderMapper.selectMaps(new QueryWrapper<MallOrder>()
                .eq("member_id", memberId).eq("status", OrderStatusEnum.FINISH.getStatus())
                .select("IFNULL(SUM(order_money),0) AS order_money"));
        if (!maps.isEmpty() && maps.get(0).get("order_money") != null) {
            result.setOrderMoney(new BigDecimal(maps.get(0).get("order_money").toString()));
        }
        return result;
    }
}
