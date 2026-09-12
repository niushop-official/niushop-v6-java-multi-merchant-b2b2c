package com.niu.core.job.vip_card;

import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.core.service.core.vip_card.card.ICoreVipCardBuyService;
import lombok.extern.log4j.Log4j2;
import org.quartz.JobExecutionContext;

/**
 * 付费会员卡到期提醒
 */
@Log4j2
@JobProvider(key = "vipCardBuyExpireRemindJob", name = "付费会员卡到期提醒", describe = "付费会员卡到期提醒", source = "core")
public class VipCardBuyExpireRemindJob extends AbstractJobProvider implements IJobProvider {

    private ICoreVipCardBuyService coreVipCardBuyService() {
        return SpringContext.bean(ICoreVipCardBuyService.class);
    }

    @Override
    public void exec(JobExecutionContext context) {
        try {
            coreVipCardBuyService().vipCardBuyExpireRemind();
        } catch (Exception e) {
            log.error("付费会员卡到期提醒失败: {}", e.getMessage(), e);
        }
    }
}
