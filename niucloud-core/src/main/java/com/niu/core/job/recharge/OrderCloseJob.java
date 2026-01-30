package com.niu.core.job.recharge;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.core.entity.recharge.RechargeOrder;
import com.niu.core.enums.recharge.RechargeOrderStatusEnum;
import com.niu.core.mapper.recharge.RechargeOrderMapper;
import com.niu.core.service.core.recharge.order.ICoreOrderCloseService;
import com.niu.core.service.core.site.ICoreSiteService;
import lombok.extern.log4j.Log4j2;
import org.quartz.JobExecutionContext;

import java.util.List;

@Log4j2
@JobProvider(key = "rechargeOrderCloseJob", name = "充值订单自动关闭", describe = "充值订单自动关闭", source = "core")
public class OrderCloseJob extends AbstractJobProvider implements IJobProvider {

    private RechargeOrderMapper rechargeOrderMapper() {
        return SpringContext.bean(RechargeOrderMapper.class);
    }
    private ICoreOrderCloseService coreOrderCloseService() {
        return SpringContext.bean(ICoreOrderCloseService.class);
    }

    @Override
    public void exec(JobExecutionContext context) {
        List<RechargeOrder> list = rechargeOrderMapper().selectList(new QueryWrapper<RechargeOrder>()
                .eq("order_status", RechargeOrderStatusEnum.WAIT_PAY.getStatus())
                .le("timeout", System.currentTimeMillis()/1000)
                .gt("timeout", 0)
        );
        if(!list.isEmpty()){
            for (RechargeOrder rechargeOrder : list){
                try {
                    coreOrderCloseService().close(rechargeOrder.getOrderId());
                }catch (Exception e){
                    log.error("{},充值订单自动关闭失败:,{}", DateUtil.formatDateTime(DateUtil.date()), e.getMessage());
                }

            }
        }
    }
}
