package com.niu.mall.job.order;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.enums.trade.SettlementEnum;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.order.ICoreOrderFinishService;
import com.niu.mall.service.core.order.ICoreOrderSettlementService;
import com.niu.mall.service.core.order.param.OrderFinishParam;
import org.quartz.JobExecutionContext;

import java.util.List;
import java.util.Map;

@JobProvider(key = "OrderSettlementJob", name = "订单自动结算", describe = "订单自动结算", source = "mall")
public class OrderSettlementJob extends AbstractJobProvider implements IJobProvider  {

    private MallOrderMapper mallOrderMapper() {
        return SpringContext.bean(MallOrderMapper.class);
    }

    private static ICoreOrderSettlementService coreOrderSettlementService() {
        return SpringContext.bean(ICoreOrderSettlementService.class);
    }


    @Override
    public void exec(JobExecutionContext context) {
        QueryWrapper<MallOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", OrderStatusEnum.FINISH.getStatus());
        queryWrapper.le("settlement_timer", System.currentTimeMillis() / 1000);
        queryWrapper.ne("settlement_timer", 0);
        queryWrapper.eq("settlement_status", SettlementEnum.WAIT.getStatus());
        List<MallOrder> orderList = mallOrderMapper().selectList(queryWrapper);
        if (!orderList.isEmpty()) {
            for (MallOrder order: orderList) {
                coreOrderSettlementService().settlement(order);
            }
        }
    }

}
