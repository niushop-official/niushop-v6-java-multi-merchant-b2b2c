package com.niu.core.job.vip_card;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.core.entity.vip_card.MemberVipCardOrder;
import com.niu.core.enums.vip_card.VipCardOrderStatusEnum;
import com.niu.core.mapper.vip_card.VipCardOrderMapper;
import com.niu.core.service.core.vip_card.order.ICoreVipCardOrderCloseService;
import lombok.extern.log4j.Log4j2;
import org.quartz.JobExecutionContext;

import java.util.List;

@Log4j2
@JobProvider(key = "vipCardOrderCloseJob", name = "付费会员卡订单自动关闭", describe = "付费会员卡订单自动关闭", source = "core")
public class VipCardOrderCloseJob extends AbstractJobProvider implements IJobProvider {

    private VipCardOrderMapper vipCardOrderMapper() {
        return SpringContext.bean(VipCardOrderMapper.class);
    }

    private ICoreVipCardOrderCloseService coreVipCardOrderCloseService() {
        return SpringContext.bean(ICoreVipCardOrderCloseService.class);
    }

    @Override
    public void exec(JobExecutionContext context) {
        List<MemberVipCardOrder> list = vipCardOrderMapper().selectList(new LambdaQueryWrapper<MemberVipCardOrder>()
                .eq(MemberVipCardOrder::getStatus, VipCardOrderStatusEnum.WAIT_PAY.getCode())
                .le(MemberVipCardOrder::getTimeout, System.currentTimeMillis() / 1000)
                .gt(MemberVipCardOrder::getTimeout, 0)
        );
        if (!list.isEmpty()) {
            ICoreVipCardOrderCloseService closeService = coreVipCardOrderCloseService();
            for (MemberVipCardOrder order : list) {
                try {
                    closeService.close(Math.toIntExact(order.getOrderId()));
                } catch (Exception e) {
                    log.error("{},付费会员卡订单自动关闭失败:,{}", DateUtil.formatDateTime(DateUtil.date()), e.getMessage());
                }
            }
        }
    }
}
