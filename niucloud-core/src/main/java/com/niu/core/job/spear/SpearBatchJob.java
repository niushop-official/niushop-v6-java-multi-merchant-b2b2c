package com.niu.core.job.spear;

import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.core.service.core.separ_account.ICoreSpearAccountWeappService;
import lombok.extern.log4j.Log4j2;
import org.quartz.JobExecutionContext;

/**
 * 小程序资金冻结分账批量重试
 * 对应 PHP: app\job\spear\SpearBatch
 */
@Log4j2
@JobProvider(key = "SpearBatchJob", name = "小程序资金冻结分账", describe = "小程序资金冻结分账", source = "core")
public class SpearBatchJob extends AbstractJobProvider implements IJobProvider {

    private ICoreSpearAccountWeappService coreSpearAccountWeappService() {
        return SpringContext.bean(ICoreSpearAccountWeappService.class);
    }

    @Override
    public void exec(JobExecutionContext context) {
        try {
            coreSpearAccountWeappService().weappSpearBatch();
        } catch (Exception e) {
            log.error("SpearBatchJob failed: {}", e.getMessage(), e);
        }
    }
}
