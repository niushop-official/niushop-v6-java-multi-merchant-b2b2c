package com.niu.core.service.core.vip_card.card;


import com.niu.core.entity.vip_card.MemberVipCardItem;

public interface ICoreVipCardItemService {

    /**
     * 根据规格ID获取会员卡规格信息
     *
     * @param itemId 规格ID
     * @return 规格实体，不存在返回null
     */
    MemberVipCardItem getInfo(Integer itemId);
}