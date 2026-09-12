package com.niu.core.service.core.vip_card.card;

public interface ICoreVipCardSaleNumService {

    /**
     * 增加销量（会员卡和规格同时增加）
     *
     * @param cardId 会员卡ID
     * @param itemId 规格ID
     * @param num    增加数量
     */
    void incSaleNum(Integer cardId, Integer itemId, int num);

    /**
     * 减少销量（会员卡和规格同时减少）
     *
     * @param cardId 会员卡ID
     * @param itemId 规格ID
     * @param num    减少数量
     */
    void decSaleNum(Integer cardId, Integer itemId, int num);
}