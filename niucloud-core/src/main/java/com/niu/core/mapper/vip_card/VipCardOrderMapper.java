package com.niu.core.mapper.vip_card;

import com.github.yulichang.base.MPJBaseMapper;
import com.niu.core.entity.vip_card.MemberVipCardOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 付费会员卡订单表 Mapper 接口
 */
@Mapper
public interface VipCardOrderMapper extends MPJBaseMapper<MemberVipCardOrder> {
}