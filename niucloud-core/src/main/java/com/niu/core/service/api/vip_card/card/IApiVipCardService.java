package com.niu.core.service.api.vip_card.card;


import com.niu.core.service.api.vip_card.card.param.ApiVipCardGoodsParam;
import com.niu.core.service.api.vip_card.card.vo.ApiVipCardBenefitsGroupVo;
import com.niu.core.service.api.vip_card.card.vo.ApiVipCardComponentsVo;
import com.niu.core.service.api.vip_card.card.vo.ApiVipCardDetailVo;
import com.niu.core.service.api.vip_card.card.vo.ApiVipCardGoodsVo;

import java.util.List;

public interface IApiVipCardService {

    /**
     * 会员卡详情
     */
    ApiVipCardDetailVo getInfo(Integer cardId);

    /**
     * 获取会员卡权益分组
     */
    ApiVipCardBenefitsGroupVo getVipCardBenefitsGroup();

    /**
     * 获取付费会员卡专属商品
     */
    List<ApiVipCardGoodsVo> getVipCardGoods(ApiVipCardGoodsParam param);

    /**
     * 获取付费会员卡组件信息
     */
    ApiVipCardComponentsVo getVipCardComponents();
}