package com.niu.core.service.core.shop;

import com.niu.core.entity.shop.SiteShopMember;

import java.math.BigDecimal;

public interface ICoreShopMemberService {

    /**
     * 调整会员最后下单时间
     * @return
     */
    Boolean setConsum(Integer siteId,Integer memberId,Integer comsumNum, BigDecimal comsumMoney);

    /**
     * 获取商品会员实例（不存在则创建）
     * @return
     */
    SiteShopMember findShopMemberCreateIfcNotExist(Integer siteId,Integer memberId);
}
