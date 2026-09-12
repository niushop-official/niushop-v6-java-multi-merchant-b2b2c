package com.niu.mall.service.core.goods.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.core.entity.goods.MallGoods;
import com.niu.core.mapper.goods.MallGoodsMapper;
import com.niu.mall.service.core.goods.ICoreGoodsAccessNumService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class CoreGoodsAccessNumServiceImpl implements ICoreGoodsAccessNumService {

    @Resource
    MallGoodsMapper mallGoodsMapper;

    @Override
    public void inc(Integer goodsId, Integer accessNum) {
        if (goodsId == null || accessNum == null || accessNum == 0) {
            return;
        }
        mallGoodsMapper.update(null, new UpdateWrapper<MallGoods>()
                .setSql("access_num = access_num + " + accessNum)
                .eq("goods_id", goodsId));
    }
}
