package com.niu.mall.listener.fenxiao.event;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.event.fenxiao.FenxiaoMemberOrderListEventDefiner;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.mapper.order.MallOrderMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商城会员订单列表统计
 */
@Component
@EventCallback("mall")
public class FenxiaoMemberOrderListListener extends FenxiaoMemberOrderListEventDefiner {

    @Resource
    MallOrderMapper mallOrderMapper;

    /**
     * 批量统计会员商城订单数/金额
     */
    @Override
    public FenxiaoMemberOrderListEventResult handleCallback(FenxiaoMemberOrderListEvent event) {
        FenxiaoMemberOrderListEventResult result = new FenxiaoMemberOrderListEventResult();
        List<Integer> memberIds = event.getMemberIds();
        if (memberIds == null || memberIds.isEmpty()) {
            result.setOrderList(new ArrayList<>());
            return result;
        }
        List<Map<String, Object>> maps = mallOrderMapper.selectMaps(new QueryWrapper<MallOrder>()
                .in("member_id", memberIds)
                .ne("pay_time", 0)
                .select("member_id, COUNT(*) AS order_num, IFNULL(SUM(order_money),0) AS order_money")
                .groupBy("member_id"));
        List<Map<String, Object>> orderList = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            orderList.add(new HashMap<>(map));
        }
        result.setOrderList(orderList);
        return result;
    }
}
