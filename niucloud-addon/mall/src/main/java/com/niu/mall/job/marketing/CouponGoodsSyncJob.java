package com.niu.mall.job.marketing;

import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.core.service.core.marketing.ICoreActiveGoodsService;
import lombok.extern.log4j.Log4j2;
import org.quartz.JobExecutionContext;

@Log4j2
@JobProvider(key = "CouponGoodsSyncJob", name = "优惠券适用商品信息同步", describe = "优惠券适用商品信息同步", source = "mall")
public class CouponGoodsSyncJob extends AbstractJobProvider implements IJobProvider {

    private ICoreActiveGoodsService coreActiveGoodsService() {
        return SpringContext.bean(ICoreActiveGoodsService.class);
    }

    @Override
    public void exec(JobExecutionContext context) {
        log.info("优惠券适用商品信息同步");
        try {
            coreActiveGoodsService().couponGoodsCache();
        }catch (Exception e){
            log.error("优惠券适用商品信息同步失败",e);
        }
    }
}

