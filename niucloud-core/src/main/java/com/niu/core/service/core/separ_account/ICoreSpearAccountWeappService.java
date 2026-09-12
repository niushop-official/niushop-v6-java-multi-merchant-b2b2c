package com.niu.core.service.core.separ_account;

import com.niu.core.common.component.context.queue.TaskArgument;
import com.niu.core.entity.separ_account.SeparAccount;
import com.niu.core.event.separ.SeparAccountModel;

import java.util.List;

public interface ICoreSpearAccountWeappService {

    /**
     * 查询待自动重试的汇付 fail 分账记录
     */
    List<SeparAccount> getFailHuifuList();

    /**
     * 分账失败批量扫描并派发子任务
     */
    void weappSpearBatch();

    /**
     * 单条分账重试：支付查询校验 -> 抢占 fail 记录 -> 执行分账
     */
    void spearExec(SeparAccountModel data);

    /**
     * 队列消费
     */
    void doJob(TaskArgument taskArgument);
}
