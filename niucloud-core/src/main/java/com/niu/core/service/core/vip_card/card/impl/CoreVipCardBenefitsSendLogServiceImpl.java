package com.niu.core.service.core.vip_card.card.impl;

import com.niu.core.entity.vip_card.MemberVipCardBenefitsSendLog;
import com.niu.core.mapper.vip_card.VipCardBenefitsSendLogMapper;
import com.niu.core.service.core.vip_card.card.ICoreVipCardBenefitsSendLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoreVipCardBenefitsSendLogServiceImpl implements ICoreVipCardBenefitsSendLogService {

    @Autowired
    private VipCardBenefitsSendLogMapper sendLogMapper;

    @Override
    public void add(Integer memberId, Integer cardId, Integer benefitsId, String benefitsType) {
        MemberVipCardBenefitsSendLog log = new MemberVipCardBenefitsSendLog();
        log.setMemberId(memberId);
        log.setCardId(cardId);
        log.setBenefitsId(benefitsId);
        log.setBenefitsType(benefitsType);
        log.setCreateTime(System.currentTimeMillis() / 1000);
        sendLogMapper.insert(log);
    }
}