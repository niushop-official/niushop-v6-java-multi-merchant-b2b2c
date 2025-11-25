package com.niu.mall.service.core.goods.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.service.core.shop.ICoreShopStatService;
import com.niu.core.service.core.shop.param.ShopStatParam;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.mapper.goods.MallGoodsStatMapper;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.service.core.goods.ICoreGoodsStatNumService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoreGoodsStatNumServiceImpl implements ICoreGoodsStatNumService {
    @Resource
    private MallOrderGoodsMapper mallOrderGoodsMapper;
    @Resource
    private ICoreShopStatService coreShopStatService;

    @Override
    @Transactional
    public void updateMallSaleNum(Integer orderId) {
        List<MallOrderGoods> mallOrderGoodsList = mallOrderGoodsMapper.selectList(new QueryWrapper<MallOrderGoods>().eq("order_id", orderId));
        if (!mallOrderGoodsList.isEmpty()){
            for (MallOrderGoods mallOrderGoods : mallOrderGoodsList){
                ShopStatParam shopStatParam = new ShopStatParam();
                shopStatParam.setSiteId(mallOrderGoods.getSiteId());
                shopStatParam.setMallSaleNum(mallOrderGoods.getNum());
                coreShopStatService.addStat(shopStatParam);
            }
        }
    }
}
