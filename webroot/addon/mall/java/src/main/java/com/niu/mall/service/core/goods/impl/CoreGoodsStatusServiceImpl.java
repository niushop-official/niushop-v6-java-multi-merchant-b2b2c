package com.niu.mall.service.core.goods.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.enums.goods.GoodsStatusEnum;
import com.niu.mall.event.goods.AfterMallGoodsStatusEditEvent;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import com.niu.mall.service.core.goods.ICoreGoodsStatusService;
import com.niu.mall.service.core.goods.param.GoodsStatusEditParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CoreGoodsStatusServiceImpl implements ICoreGoodsStatusService {

    @Resource
    private MallGoodsMapper mallGoodsMapper;

    @Override
    public void editStatus(List<GoodsStatusEditParam> params) {
        List<Integer> goodsIds = params.stream().map(GoodsStatusEditParam::getGoodsId).toList();
        MallGoods mallGoods = new MallGoods();
        mallGoods.setStatus(GoodsStatusEnum.OFFLINE.getStatus());
        mallGoodsMapper.update(mallGoods,new QueryWrapper<MallGoods>().in("goods_id", goodsIds));

        //跟新商城上架商品数
        Map<Integer, List<GoodsStatusEditParam>> siteGoodsIds = params.stream().collect(Collectors.groupingBy(GoodsStatusEditParam::getSiteId));
        siteGoodsIds.forEach((siteId,goods)->{
            //商品状态编辑后
            AfterMallGoodsStatusEditEvent event = new AfterMallGoodsStatusEditEvent();
            event.setSiteId(siteId);
            event.setGoodsIds(goods.stream().map(GoodsStatusEditParam::getGoodsId).toList());
            EventAndSubscribeOfPublisher.publishAll(event);
        });
    }
}
