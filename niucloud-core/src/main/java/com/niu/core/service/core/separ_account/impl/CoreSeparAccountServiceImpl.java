package com.niu.core.service.core.separ_account.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.separ_account.SeparAccount;
import com.niu.core.entity.separ_account.SeparAccountDetail;
import com.niu.core.enums.account.SeparStatusEnum;
import com.niu.core.event.separ.AfterSeparEvent;
import com.niu.core.event.separ.SeparAccountModel;
import com.niu.core.event.separ.ToSeparEvent;
import com.niu.core.mapper.separ_account.SeparAccountDetailMapper;
import com.niu.core.mapper.separ_account.SeparAccountMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.separ_account.ICoreSeparAccountService;
import com.niu.core.service.core.separ_account.param.SeparNotifyParam;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class CoreSeparAccountServiceImpl implements ICoreSeparAccountService {

    @Resource
    SeparAccountMapper separAccountMapper;

    @Resource
    SeparAccountDetailMapper separAccountDetailMapper;

    /**
     * 添加分账
     * @param account
     * @param detail
     */
    @Override
    public void add(SeparAccount account, List<SeparAccountDetail> detail) {
        separAccountMapper.insert(account);

        for (SeparAccountDetail separAccountDetail : detail) {
            separAccountDetail.setSeparId(account.getId());
            separAccountDetailMapper.insert(separAccountDetail);
        }

        toSpear(account);
    }

    /**
     * 分账通知
     * @param param
     */
    @Override
    public void notify(SeparNotifyParam param) {
        SeparAccount account = separAccountMapper.selectById(param.getId());
        if (account != null) {
            account.setStatus(param.getStatus().getStatus());
            account.setFailReason(param.getFailReason());
            if (param.getStatus().getStatus().equals(SeparStatusEnum.SUCCESS.getStatus())) {
                account.setSeparTime(System.currentTimeMillis() / 1000);
            }
            if (StringUtils.isNotEmpty(param.getSeparOutTradeNo())) {
                account.setOutTradeNo(param.getSeparOutTradeNo());
            }
            separAccountMapper.updateById(account);

            if(param.getStatus().getStatus().equals(SeparStatusEnum.SUCCESS.getStatus())){
                AfterSeparEvent event = new AfterSeparEvent();
                event.setSiteId(RequestUtils.defaultSiteId());
                event.addAppSign("mall");
                event.setAccount(account);
                EventAndSubscribeOfPublisher.publishAll(event);
            }
        }
    }


    /**
     * 去分账
     * @param account
     */
    @Override
    public void toSpear(SeparAccount account) {
        log.info("进入分账");
        try {
            SeparAccountModel model = new SeparAccountModel();
            BeanUtils.copyProperties(account, model);
            model.setDetail(separAccountDetailMapper.selectList(new QueryWrapper<SeparAccountDetail>().eq("separ_id", account.getId())));
            ToSeparEvent event = new ToSeparEvent();
            event.setSiteId(RequestUtils.defaultSiteId());
            event.setAccount(model);
            event.setName("ToSeparEvent");
            EventAndSubscribeOfPublisher.publishAll(event);
        } catch (Exception e) {
            log.error("toSpear failed, accountId={}", account.getId(), e);
            SeparNotifyParam param = new SeparNotifyParam();
            param.setId(account.getId());
            param.setStatus(SeparStatusEnum.FAIL);
            param.setFailReason(e.getMessage());
            notify(param);
        }
    }
}
