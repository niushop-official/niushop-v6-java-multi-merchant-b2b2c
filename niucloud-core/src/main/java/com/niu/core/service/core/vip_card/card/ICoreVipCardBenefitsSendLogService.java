package com.niu.core.service.core.vip_card.card;

public interface ICoreVipCardBenefitsSendLogService {

    /**
     * 添加权益发放记录
     *
     * @param memberId     会员ID
     * @param cardId       会员卡ID
     * @param benefitsId   权益ID
     * @param benefitsType 权益类型
     */
    void add(Integer memberId, Integer cardId, Integer benefitsId, String benefitsType);
}