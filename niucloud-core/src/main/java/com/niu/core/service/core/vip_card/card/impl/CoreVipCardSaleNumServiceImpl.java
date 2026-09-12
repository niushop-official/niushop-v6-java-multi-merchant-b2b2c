package com.niu.core.service.core.vip_card.card.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.niu.core.entity.vip_card.MemberVipCard;
import com.niu.core.entity.vip_card.MemberVipCardItem;
import com.niu.core.mapper.vip_card.VipCardItemMapper;
import com.niu.core.mapper.vip_card.VipCardMapper;
import com.niu.core.service.core.vip_card.card.ICoreVipCardSaleNumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CoreVipCardSaleNumServiceImpl implements ICoreVipCardSaleNumService {

    @Autowired
    private VipCardMapper vipCardMapper;

    @Autowired
    private VipCardItemMapper vipCardItemMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incSaleNum(Integer cardId, Integer itemId, int num) {
        // 更新会员卡销量
        LambdaUpdateWrapper<MemberVipCard> cardWrapper = new LambdaUpdateWrapper<>();
        cardWrapper.eq(MemberVipCard::getCardId, cardId)
                .setSql("sale_num = sale_num + " + num);
        vipCardMapper.update(null, cardWrapper);

        // 更新规格销量
        LambdaUpdateWrapper<MemberVipCardItem> itemWrapper = new LambdaUpdateWrapper<>();
        itemWrapper.eq(MemberVipCardItem::getCardId, cardId)
                .eq(MemberVipCardItem::getItemId, itemId)
                .setSql("sale_num = sale_num + " + num);
        vipCardItemMapper.update(null, itemWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void decSaleNum(Integer cardId, Integer itemId, int num) {
        // 更新会员卡销量（减少）
        LambdaUpdateWrapper<MemberVipCard> cardWrapper = new LambdaUpdateWrapper<>();
        cardWrapper.eq(MemberVipCard::getCardId, cardId)
                .setSql("sale_num = sale_num - " + num);
        vipCardMapper.update(null, cardWrapper);

        // 更新规格销量（减少）
        LambdaUpdateWrapper<MemberVipCardItem> itemWrapper = new LambdaUpdateWrapper<>();
        itemWrapper.eq(MemberVipCardItem::getCardId, cardId)
                .eq(MemberVipCardItem::getItemId, itemId)
                .setSql("sale_num = sale_num - " + num);
        vipCardItemMapper.update(null, itemWrapper);
    }
}