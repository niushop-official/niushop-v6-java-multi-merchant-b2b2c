package com.niu.core.job.cash;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.core.entity.cash.CashOrder;
import com.niu.core.enums.cash.CashOrderStatusEnum;
import com.niu.core.mapper.cash.CashOrderMapper;
import com.niu.core.service.core.cash.order.ICoreCashOrderCloseService;
import lombok.extern.log4j.Log4j2;
import org.quartz.JobExecutionContext;

import java.util.List;

/**
 * 买单订单自动关闭
 */
@Log4j2
@JobProvider(key = "cashOrderCloseJob", name = "买单订单未支付关闭", describe = "买单订单未支付关闭", source = "core")
public class CashOrderCloseJob extends AbstractJobProvider implements IJobProvider {

    private CashOrderMapper cashOrderMapper() {
        return SpringContext.bean(CashOrderMapper.class);
    }

    private ICoreCashOrderCloseService coreCashOrderCloseService() {
        return SpringContext.bean(ICoreCashOrderCloseService.class);
    }

    @Override
    public void exec(JobExecutionContext context) {
        long expireTime = System.currentTimeMillis() / 1000 - 10 * 60;
        List<CashOrder> list = cashOrderMapper().selectList(
                new LambdaQueryWrapper<CashOrder>()
                        .eq(CashOrder::getStatus, CashOrderStatusEnum.WAIT_PAY.getStatus())
                        .le(CashOrder::getCreateTime, expireTime)
        );
        if (!list.isEmpty()) {
            ICoreCashOrderCloseService closeService = coreCashOrderCloseService();
            for (CashOrder order : list) {
                try {
                    closeService.close(order.getOrderId());
                } catch (Exception e) {
                    log.error("{},买单订单自动关闭失败:{}", DateUtil.formatDateTime(DateUtil.date()), e.getMessage());
                }
            }
        }
    }
}
