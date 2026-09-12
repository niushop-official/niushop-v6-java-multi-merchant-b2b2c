package com.niu.core.service.core.vip_card.card;


import com.niu.core.entity.vip_card.MemberVipCard;
import com.niu.core.entity.vip_card.MemberVipCardItem;
import com.niu.core.entity.vip_card.MemberVipCardOrderItem;
import com.niu.core.service.core.vip_card.card.vo.VipCardItemVo;

import java.util.Map;

public interface ICoreVipCardService {

    MemberVipCard getInfo(Integer cardId);

    Map<String, Object> getCardBenefits(MemberVipCard vipCard);

    Long getCardExpireTime(MemberVipCardOrderItem orderItem);

    VipCardItemVo getCardTrialAndRenewInfo(MemberVipCard vipCard, MemberVipCardItem vipCardItem, Integer memberId);
}