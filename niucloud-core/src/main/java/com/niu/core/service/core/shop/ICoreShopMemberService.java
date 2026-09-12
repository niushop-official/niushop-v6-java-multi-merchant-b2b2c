package com.niu.core.service.core.shop;

import com.niu.core.entity.shop.SiteShopMember;

import java.math.BigDecimal;
import java.util.List;

public interface ICoreShopMemberService {

    /**
     * 调整会员最后下单时间
     *
     * @return
     */
    Boolean setConsum(Integer siteId, Integer memberId, String addon, Integer comsumNum, BigDecimal comsumMoney);

    /**
     * 获取商品会员实例（不存在则创建）
     *
     * @return
     */
    SiteShopMember findShopMemberCreateIfcNotExist(Integer siteId, Integer memberId, String addon);

    /**
     * 批量创建店铺会员记录（根据站点应用列表）
     */
    void batchFindShopMemberCreateIfcNotExist(Integer siteId, Integer memberId);

    /**
     * 修改单个店铺会员字段
     */
    void modify(Integer siteId, Integer memberId, String addon, String field, Object value);

    /**
     * 批量修改店铺会员字段
     */
    void batchModify(Integer siteId, List<Integer> memberIds, String addon, String field, Object value);

    /**
     * 检查会员是否为老用户（根据配置天数）
     */
    void checkOldShopMember(Integer memberId);
}
