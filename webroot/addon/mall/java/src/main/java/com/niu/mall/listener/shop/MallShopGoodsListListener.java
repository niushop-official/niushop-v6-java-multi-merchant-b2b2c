package com.niu.mall.listener.shop;

import cn.hutool.core.collection.CollectionUtil;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.event.shop.MallShopGoodsListEventDefiner;
import com.niu.core.service.api.shop.vo.MallGoodsListVo;
import com.niu.mall.service.api.goods.IGoodsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;


/**
 * 查询成功
 */
@EventCallback("mall")
@Component
public class MallShopGoodsListListener extends MallShopGoodsListEventDefiner {

    @Resource
    IGoodsService goodsService;


    @Override
    public MallShopGoodsListEventResult handleCallback(MallShopGoodsListEvent event) {
        MallShopGoodsListEventResult result=new MallShopGoodsListEventResult();
        List<MallGoodsListVo> mallGoodsListVos = goodsService.selectGoodsAndSkuList(event);
        if (CollectionUtil.isEmpty(mallGoodsListVos)) {
            mallGoodsListVos = Collections.emptyList();
        }
        result.setGoods(mallGoodsListVos);
        result.setGoodsList(mallGoodsListVos);
        return result;
    }
}
