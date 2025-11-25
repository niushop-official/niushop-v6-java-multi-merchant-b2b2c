package com.niu.mall.service.core.goods.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import com.niu.mall.service.core.goods.ICoreGoodsCartNumService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class CoreGoodsCartNumServiceImpl implements ICoreGoodsCartNumService {

    @Resource
    MallGoodsMapper mallGoodsMapper;

    @Override
    public void inc(Integer goodsId, Integer cartNum) {
        MallGoods mallGoods = mallGoodsMapper.selectById(goodsId);
        int cart_num = mallGoods.getCartNum() + cartNum;
        mallGoodsMapper.update(null, new UpdateWrapper<MallGoods>().set("cart_num" , cartNum).eq("goods_id", goodsId));
    }

    @Override
    public void dec(Integer goodsId, Integer cartNum) {
        MallGoods mallGoods = mallGoodsMapper.selectById(goodsId);
        int cart_num = mallGoods.getCartNum() - cartNum;
        mallGoodsMapper.update(null, new UpdateWrapper<MallGoods>().eq("goods_id", goodsId).set("cart_num" , cartNum));
    }
}
