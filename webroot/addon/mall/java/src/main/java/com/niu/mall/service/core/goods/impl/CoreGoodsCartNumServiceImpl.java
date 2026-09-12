package com.niu.mall.service.core.goods.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.core.entity.goods.MallGoods;
import com.niu.core.mapper.goods.MallGoodsMapper;
import com.niu.mall.service.core.goods.ICoreGoodsCartNumService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class CoreGoodsCartNumServiceImpl implements ICoreGoodsCartNumService {

    @Resource
    MallGoodsMapper mallGoodsMapper;

    @Override
    public void inc(Integer goodsId, Integer cartNum) {
        if (goodsId == null || cartNum == null || cartNum == 0) {
            return;
        }
        mallGoodsMapper.update(null, new UpdateWrapper<MallGoods>()
                .setSql("cart_num = cart_num + " + cartNum)
                .eq("goods_id", goodsId));
    }

    @Override
    public void dec(Integer goodsId, Integer cartNum) {
        if (goodsId == null || cartNum == null || cartNum == 0) {
            return;
        }
        mallGoodsMapper.update(null, new UpdateWrapper<MallGoods>()
                .setSql("cart_num = GREATEST(cart_num - " + cartNum + ", 0)")
                .eq("goods_id", goodsId));
    }
}
