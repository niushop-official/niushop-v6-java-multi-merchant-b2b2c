package com.niu.mall.service.core.vip_card.benefits;

import com.niu.core.service.core.vip_card.card.benefits.BenefitsContentHandler;

import java.util.Map;

public class MallBuyGoodsDiscountHandler implements BenefitsContentHandler {
    @Override
    public String handle(Integer siteId, Map<String, Object> config) {
        return "购买部分商品享受专属折扣";
    }
}
