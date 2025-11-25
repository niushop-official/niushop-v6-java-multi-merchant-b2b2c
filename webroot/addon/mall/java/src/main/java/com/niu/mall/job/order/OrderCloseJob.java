package com.niu.mall.job.order;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.enums.order.OrderCloseTypeEnum;
import com.niu.mall.enums.order.OrderLogMainTypeEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.order.ICoreOrderCloseService;
import com.niu.mall.service.core.order.ICoreOrderGroupCloseService;
import com.niu.mall.service.core.order.ICoreOrderGroupService;
import com.niu.mall.service.core.order.ICoreOrderService;
import com.niu.mall.service.core.order.param.OrderCloseParam;
import org.quartz.JobExecutionContext;

import java.util.List;

@JobProvider(key = "OrderCloseJob", name = "订单自动关闭", describe = "订单自动关闭", source = "mall")
public class OrderCloseJob extends AbstractJobProvider implements IJobProvider  {

    private MallOrderMapper shopOrderMapper() {
        return SpringContext.bean(MallOrderMapper.class);
    }

    private static ICoreOrderCloseService coreOrderCloseService() {
        return SpringContext.bean(ICoreOrderCloseService.class);
    }

    private static ICoreOrderGroupCloseService coreOrderGroupCloseService() {
        return SpringContext.bean(ICoreOrderGroupCloseService.class);
    }

    @Override
    public void exec(JobExecutionContext context) {
        List<MallOrder> orderList = shopOrderMapper().selectList(new QueryWrapper<MallOrder>()
            .eq("status", OrderStatusEnum.WAIT_PAY.getStatus())
            .gt("timeout", 0)
            .le("timeout", System.currentTimeMillis() / 1000)
            .select("order_id,site_id,group_id")
        );
        if (!orderList.isEmpty()) {
            for (MallOrder order: orderList) {
                //先判断订单是否存在订单组,且订单组状态为待支付
                //先尝试关闭订单组
                OrderCloseParam closeParamGroup = new OrderCloseParam();
                closeParamGroup.setMainType(OrderLogMainTypeEnum.SYSTEM.getType());
                closeParamGroup.setMainId(0);
                closeParamGroup.setSiteId(order.getSiteId());
                closeParamGroup.setCloseType(OrderCloseTypeEnum.AUTO_CLOSE.getType());
                closeParamGroup.setOrderId(order.getOrderId());
                coreOrderGroupCloseService().close(order.getGroupId(), closeParamGroup);

                OrderCloseParam closeParam = new OrderCloseParam();
                closeParam.setOrderId(order.getOrderId());
                closeParam.setSiteId(order.getSiteId());
                closeParam.setMainType(OrderLogMainTypeEnum.SYSTEM.getType());
                closeParam.setMainId(0);
                closeParam.setCloseType(OrderCloseTypeEnum.AUTO_CLOSE.getType());
                coreOrderCloseService().close(closeParam);
            }
        }
    }

}
