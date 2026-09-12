package com.niu.core.service.core.vip_card.card;


import com.niu.core.entity.vip_card.MemberVipCardBuy;
import com.niu.core.entity.vip_card.MemberVipCardOrderItem;

public interface ICoreVipCardBuyService {

    MemberVipCardBuy getCardBuy(Integer cardId, Integer memberId);

    MemberVipCardBuy getMemberBuy(Integer memberId);

    MemberVipCardBuy getCardBuyTrial(Integer cardId, Integer memberId);

    Integer getCardBuyNum(Integer cardId);

    void sendBenefits(MemberVipCardOrderItem orderItem);

    void sendBenefitsGifts();

    void sendGifts(Integer siteId, Integer memberId, Integer cardId, String type, Object gifts, String memo);

    void vipCardBuyExpire();

    void vipCardBuyExpireRemind();
}