package com.niu.core.mapper.vip_card;

import com.github.yulichang.base.MPJBaseMapper;
import com.niu.core.entity.vip_card.MemberVipCardOrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 付费会员卡订单项表 Mapper 接口
 */
@Mapper
public interface VipCardOrderItemMapper extends MPJBaseMapper<MemberVipCardOrderItem> {
}