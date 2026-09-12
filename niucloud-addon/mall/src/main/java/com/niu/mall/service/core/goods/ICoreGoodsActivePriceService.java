package com.niu.mall.service.core.goods;


import cn.hutool.json.JSONObject;
import com.niu.core.service.api.member.vo.MemberLevelInfoVo;
import com.niu.mall.service.core.goods.vo.ShowPriceVo;
import com.niu.mall.service.core.goods.param.GetShowPriceParam;

import java.math.BigDecimal;

public interface ICoreGoodsActivePriceService {

    /**
     * 获取商品展示价格
     * @param param
     * @return
     */
    ShowPriceVo getShowPrice(GetShowPriceParam param);

    /**
     * 计算会员价格
     * @param memberLevel
     * @param calculateType
     * @param config
     * @param price
     * @return
     */
    BigDecimal calculateMemberPrice(MemberLevelInfoVo memberLevel, String calculateType, JSONObject config, BigDecimal price);

    /**
     * 计算付费会员卡价格
     * @param memberId 会员id
     * @param vipCardDiscount 付费会员卡折扣类型
     * @param svipDiscount 付费会员折扣 JSON
     * @param svipPrice 付费会员价 JSON
     * @param price 原价
     * @return 付费会员价，未命中返回 null
     */
    BigDecimal calculateVipCardPrice(Integer memberId, String vipCardDiscount, String svipDiscount, String svipPrice, BigDecimal price);
}
