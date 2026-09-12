package com.niu.mall.listener.vip_card;

import com.niu.core.common.annotation.EventCallback;
import com.niu.core.event.vip_card.VipCardGoodsListEventDefiner;
import com.niu.core.service.api.vip_card.card.vo.ApiVipCardGoodsVo;
import com.niu.mall.service.api.goods.IGoodsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EventCallback("mall")
public class VipCardGoodsListListener extends VipCardGoodsListEventDefiner {

    @Resource
    private IGoodsService goodsService;

    @Override
    public VipCardGoodsListEventResult handleCallback(VipCardGoodsListEvent vipCardGoodsListEvent) {
        Integer num = vipCardGoodsListEvent.getNum() != null ? vipCardGoodsListEvent.getNum() : 3;
        String group = vipCardGoodsListEvent.getGroup() != null ? vipCardGoodsListEvent.getGroup() : "";
        VipCardGoodsListEventResult result = new VipCardGoodsListEventResult();
        if ("mall".equals(group)) {
            List<ApiVipCardGoodsVo> goodsList = goodsService.getVipCardGoodsList(num);
            result.setResultData(goodsList);
        }
        return result;
    }
}
