package com.niu.mall.job.goods;

import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.mall.service.admin.goods.IMallGoodsStatService;
import org.quartz.JobExecutionContext;

@SuppressWarnings("unused")
@JobProvider(key = "GoodsStatisticJob", name = "商品统计更新", describe = "商品统计更新", source = "mall")
public class GoodsStatisticJob extends AbstractJobProvider implements IJobProvider {
    private volatile boolean init = false;
    private IMallGoodsStatService mallGoodsStatService;

    @Override
    public void exec(JobExecutionContext context) {
        if (!init) {
            init();
        }

        mallGoodsStatService.syncStatGoods();
    }

    private void init() {
        this.mallGoodsStatService = SpringContext.bean(IMallGoodsStatService.class);
        init = true;
    }

}
