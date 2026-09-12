package com.niu.mall.service.core.goods.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.core.entity.goods.MallGoods;
import com.niu.core.mapper.goods.MallGoodsMapper;
import com.niu.mall.service.core.goods.ICoreGoodsCollectNumService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class CoreGoodsCollectNumServiceImpl implements ICoreGoodsCollectNumService {

    @Resource
    MallGoodsMapper mallGoodsMapper;

    @Override
    public void inc(Integer goodsId, Integer collectNum) {
        if (goodsId == null || collectNum == null || collectNum == 0) {
            return;
        }
        mallGoodsMapper.update(null, new UpdateWrapper<MallGoods>()
                .setSql("collect_num = collect_num + " + collectNum)
                .eq("goods_id", goodsId));
    }
}
