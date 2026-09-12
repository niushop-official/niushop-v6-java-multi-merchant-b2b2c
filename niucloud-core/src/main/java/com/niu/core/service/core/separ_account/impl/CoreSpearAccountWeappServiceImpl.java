package com.niu.core.service.core.separ_account.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.core.common.component.context.queue.AsyncTask;
import com.niu.core.common.component.context.queue.AsyncTaskManager;
import com.niu.core.common.component.context.queue.AsyncTaskQueueManager;
import com.niu.core.common.component.context.queue.TaskArgument;
import com.niu.core.entity.separ_account.SeparAccount;
import com.niu.core.entity.separ_account.SeparAccountDetail;
import com.niu.core.enums.account.SeparStatusEnum;
import com.niu.core.event.separ.SeparAccountModel;
import com.niu.core.event.separ.SeparPayQueryEventDefiner;
import com.niu.core.event.separ.SeparTradeSceneQueryEventDefiner;
import com.niu.core.mapper.separ_account.SeparAccountDetailMapper;
import com.niu.core.mapper.separ_account.SeparAccountMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.separ_account.ICoreSeparAccountService;
import com.niu.core.service.core.separ_account.ICoreSpearAccountWeappService;
import com.niu.core.service.core.separ_account.param.SeparNotifyParam;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class CoreSpearAccountWeappServiceImpl implements ICoreSpearAccountWeappService {

    private static final String HUIFU_WECHATPAY = "huifu_wechatpay";
    private static final String WEAPP_SCENE = "weapp";
    /** DEALING 锁超时时间（秒），超时后恢复为 fail 以便再次重试 */
    private static final long DEALING_LOCK_TIMEOUT_SEC = 30 * 60L;

    @Resource
    private SeparAccountMapper separAccountMapper;

    @Resource
    private SeparAccountDetailMapper separAccountDetailMapper;

    @Resource
    private ICoreSeparAccountService coreSeparAccountService;

    @Override
    public List<SeparAccount> getFailHuifuList() {
        return separAccountMapper.selectList(new QueryWrapper<SeparAccount>()
                .eq("trade_scene", WEAPP_SCENE)
                .eq("status", SeparStatusEnum.FAIL.getStatus())
                .eq("pay_type", HUIFU_WECHATPAY));
    }

    @Override
    public void weappSpearBatch() {
        recoverStaleDealing();
        backfillTradeScene();
        List<SeparAccount> list = getFailHuifuList();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        for (SeparAccount item : list) {
            SeparAccountModel model = buildAccountModel(item);
            AsyncTask asyncTask = AsyncTaskManager.build(ICoreSpearAccountWeappService.class)
                    .setMethodName("doJob")
                    .addMethodParameter("data", model);
            AsyncTaskQueueManager.delayExecute(asyncTask);
        }
    }

    @Override
    public void spearExec(SeparAccountModel data) {
        if (data == null || data.getId() == null) {
            return;
        }
        try {
            SeparPayQueryEventDefiner.SeparPayQueryEvent event = new SeparPayQueryEventDefiner.SeparPayQueryEvent();
            event.setName("SeparPayQuery");
            event.addAppSign("core");
            event.setAccount(data);
            List<SeparPayQueryEventDefiner.SeparPayQueryResult> results =
                    EventAndSubscribeOfPublisher.publishAndCallback(event);
            boolean canRetry = !CollectionUtils.isEmpty(results) && results.stream()
                    .filter(Objects::nonNull)
                    .map(SeparPayQueryEventDefiner.SeparPayQueryResult::getStatus)
                    .anyMatch("success"::equals);
            if (!canRetry) {
                return;
            }
            long lockTime = System.currentTimeMillis() / 1000;
            int locked = separAccountMapper.update(null, new UpdateWrapper<SeparAccount>()
                    .eq("id", data.getId())
                    .eq("status", SeparStatusEnum.FAIL.getStatus())
                    .set("status", SeparStatusEnum.DEALING.getStatus())
                    .set("fail_reason", "")
                    .set("separ_time", lockTime));
            if (locked <= 0) {
                return;
            }
            data.setStatus(SeparStatusEnum.DEALING.getStatus());
            SeparAccount account = new SeparAccount();
            BeanUtils.copyProperties(data, account);
            coreSeparAccountService.toSpear(account);
        } catch (Exception e) {
            log.error("splitSettlement spearExec failed, data={}", data, e);
            SeparNotifyParam param = new SeparNotifyParam();
            param.setId(data.getId());
            param.setStatus(SeparStatusEnum.FAIL);
            param.setFailReason(e.getMessage());
            coreSeparAccountService.notify(param);
        }
    }

    @Override
    public void doJob(TaskArgument taskArgument) {
        Object data = taskArgument.get("data");
        if (data instanceof SeparAccountModel model) {
            spearExec(model);
        }
    }

    /**
     * 恢复超时的 DEALING 记录，避免进程中断后永久卡住
     */
    private void recoverStaleDealing() {
        long expireBefore = System.currentTimeMillis() / 1000 - DEALING_LOCK_TIMEOUT_SEC;
        separAccountMapper.update(null, new UpdateWrapper<SeparAccount>()
                .eq("status", SeparStatusEnum.DEALING.getStatus())
                .eq("pay_type", HUIFU_WECHATPAY)
                .eq("trade_scene", WEAPP_SCENE)
                .lt("separ_time", expireBefore)
                .gt("separ_time", 0)
                .set("status", SeparStatusEnum.FAIL.getStatus())
                .set("fail_reason", "分账处理超时，已恢复为失败待重试"));
    }

    /**
     * 历史 fail 记录 trade_scene 回填
     */
    private void backfillTradeScene() {
        List<SeparAccount> pendingList = separAccountMapper.selectList(new QueryWrapper<SeparAccount>()
                .eq("status", SeparStatusEnum.FAIL.getStatus())
                .eq("pay_type", HUIFU_WECHATPAY)
                .and(wrapper -> wrapper.isNull("trade_scene")
                        .or()
                        .eq("trade_scene", "")
                        .or()
                        .eq("trade_scene", "1")));
        if (CollectionUtils.isEmpty(pendingList)) {
            return;
        }
        for (SeparAccount item : pendingList) {
            String orderFrom = resolveOrderFrom(item);
            if (!StringUtils.hasText(orderFrom)) {
                continue;
            }
            separAccountMapper.update(null, new UpdateWrapper<SeparAccount>()
                    .eq("id", item.getId())
                    .set("trade_scene", orderFrom));
        }
    }

    private String resolveOrderFrom(SeparAccount account) {
        SeparTradeSceneQueryEventDefiner.SeparTradeSceneQueryEvent event =
                new SeparTradeSceneQueryEventDefiner.SeparTradeSceneQueryEvent();
        event.setName("SeparTradeSceneQuery");
        event.addAppSign("core");
        event.setTradeType(account.getTradeType());
        event.setTradeId(account.getTradeId());
        event.setSiteId(account.getSiteId());
        List<SeparTradeSceneQueryEventDefiner.SeparTradeSceneQueryResult> results =
                EventAndSubscribeOfPublisher.publishAndCallback(event);
        if (CollectionUtils.isEmpty(results)) {
            return null;
        }
        return results.stream()
                .filter(Objects::nonNull)
                .map(SeparTradeSceneQueryEventDefiner.SeparTradeSceneQueryResult::getOrderFrom)
                .filter(StringUtils::hasText)
                .findFirst()
                .orElse(null);
    }

    private SeparAccountModel buildAccountModel(SeparAccount item) {
        SeparAccountModel model = new SeparAccountModel();
        BeanUtils.copyProperties(item, model);
        model.setDetail(separAccountDetailMapper.selectList(
                new QueryWrapper<SeparAccountDetail>().eq("separ_id", item.getId())));
        return model;
    }
}
