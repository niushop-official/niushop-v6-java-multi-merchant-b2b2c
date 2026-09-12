package com.niu.core.service.core.vip_card.card.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.entity.vip_card.MemberVipCardItem;
import com.niu.core.mapper.vip_card.VipCardItemMapper;
import com.niu.core.service.core.vip_card.card.ICoreVipCardItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoreVipCardItemServiceImpl implements ICoreVipCardItemService {

    @Autowired
    private VipCardItemMapper vipCardItemMapper;

    @Override
    public MemberVipCardItem getInfo(Integer itemId) {
        return vipCardItemMapper.selectOne(
                new LambdaQueryWrapper<MemberVipCardItem>()
                        .eq(MemberVipCardItem::getItemId, itemId)
        );
    }
}