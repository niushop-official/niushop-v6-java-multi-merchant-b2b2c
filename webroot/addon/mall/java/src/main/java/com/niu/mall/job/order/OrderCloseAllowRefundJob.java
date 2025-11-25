package com.niu.mall.job.order;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.enums.order.OrderLogActionEnum;
import com.niu.mall.enums.order.OrderLogMainTypeEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.order.ICoreOrderLogService;
import com.niu.mall.service.core.order.param.AddOrderLogParam;
import org.quartz.JobExecutionContext;

import java.util.ArrayList;
import java.util.List;

@JobProvider(key = "OrderCloseAllowRefundJob", name = "订单完成自动关闭售后", describe = "订单完成自动关闭售后", source = "mall")
public class OrderCloseAllowRefundJob extends AbstractJobProvider implements IJobProvider  {

    private MallOrderMapper shopOrderMapper() {
        return SpringContext.bean(MallOrderMapper.class);
    }

    private MallOrderGoodsMapper mallOrderGoodsMapper() {
        return SpringContext.bean(MallOrderGoodsMapper.class);
    }

    private static ICoreOrderLogService coreOrderLogService() {
        return SpringContext.bean(ICoreOrderLogService.class);
    }

    @Override
    public void exec(JobExecutionContext context) {
        List<MallOrder> orderList = shopOrderMapper().selectList(new QueryWrapper<MallOrder>()
            .eq("status", OrderStatusEnum.FINISH.getStatus())
            .eq("is_enable_refund", 1)
            .le("timeout", System.currentTimeMillis() / 1000)
            .select("order_id,site_id")
        );
        if (!orderList.isEmpty()) {
            List<Integer> orderIdList = orderList.stream().map(MallOrder::getOrderId).toList();
            //订单设置
            MallOrder order = new MallOrder();
            order.setIsEnableRefund(0);
            order.setTimeout(0L);
            shopOrderMapper().update(order, new QueryWrapper<MallOrder>().in("order_id", orderIdList));
            //订单项设置
            MallOrderGoods orderGoods = new MallOrderGoods();
            orderGoods.setIsEnableRefund(0);
            mallOrderGoodsMapper().update(orderGoods, new QueryWrapper<MallOrderGoods>().in("order_id", orderIdList));

            //写日志
            List<AddOrderLogParam> data = new ArrayList<>();
            orderList.forEach(info -> {
                AddOrderLogParam param = new AddOrderLogParam();
                param.setOrderId(info.getOrderId());
                param.setMainType(OrderLogMainTypeEnum.SYSTEM.getType());
                param.setMainId(0);
                param.setType(OrderLogActionEnum.ORDER_CLOSE_ALLOW_REFUND_ACTION.getAction());
                param.setStatus(OrderStatusEnum.FINISH.getStatus());
                param.setContent( "");
                data.add(param);
            });
            coreOrderLogService().addAll(data);
        }
    }

}
