package com.niu.mall.service.core.goods.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.event.goods.CheckGoodsStatusEvent;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.entity.goods.MallGoods;
import com.niu.core.entity.goods.MallGoodsSku;
import com.niu.core.mapper.goods.MallGoodsMapper;
import com.niu.core.mapper.goods.MallGoodsSkuMapper;
import com.niu.mall.service.core.goods.ICoreGoodsOnlineCheckService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CoreGoodsOnlineCheckServiceImpl implements ICoreGoodsOnlineCheckService {

    @Resource
    private MallGoodsMapper mallGoodsMapper;

    @Resource
    private MallGoodsSkuMapper mallGoodsSkuMapper;

    @Override
    public void checkGoodsStatus(List<Integer> goodsIds) {
        if (goodsIds == null || goodsIds.isEmpty()) {
            return;
        }
        QueryWrapper<MallGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("goods_id", goodsIds)
                .gt("supply_goods_id", 0)
                .select("supply_goods_id");
        List<MallGoods> goodsList = mallGoodsMapper.selectList(queryWrapper);

        List<Integer> supplyGoodsIds = goodsList.stream()
                .map(MallGoods::getSupplyGoodsId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (!supplyGoodsIds.isEmpty()) {
            CheckGoodsStatusEvent event = new CheckGoodsStatusEvent();
            event.setGoodsIds(supplyGoodsIds);
            EventAndSubscribeOfPublisher.publishAndCallbackBySite(event);
        }
    }

    @Override
    public void checkGoodsSkuPriceBatch(List<Integer> goodsIds) {
        QueryWrapper<MallGoods> goodsQuery = new QueryWrapper<>();
        goodsQuery.in("goods_id", goodsIds)
                .ne("supply_goods_id", 0)
                .select("goods_id");
        List<MallGoods> goodsList = mallGoodsMapper.selectList(goodsQuery);
        List<Integer> filteredGoodsIds = goodsList.stream()
                .map(MallGoods::getGoodsId)
                .collect(Collectors.toList());

        if (filteredGoodsIds.isEmpty()) {
            return;
        }
        QueryWrapper<MallGoodsSku> skuQuery = new QueryWrapper<>();
        skuQuery.in("goods_id", filteredGoodsIds)
                .select("sku_id", "price", "cost_price", "min_price", "max_price");
        List<MallGoodsSku> goodsSkuList = mallGoodsSkuMapper.selectList(skuQuery);

        if (goodsSkuList.isEmpty()) {
            throw new CommonException("MALL_GOODS_NOT_EXIST");
        }

        for (MallGoodsSku sku : goodsSkuList) {
            if (sku.getPrice().compareTo(sku.getCostPrice()) < 0) {
                throw new CommonException("销售价不能小于供货价格");
            }

            if (sku.getMinPrice() != null && sku.getMinPrice().compareTo(BigDecimal.ZERO) > 0
                    && sku.getPrice().compareTo(sku.getMinPrice()) < 0) {
                throw new CommonException("销售价不能小于最低价格");
            }

            if (sku.getMaxPrice() != null && sku.getMaxPrice().compareTo(BigDecimal.ZERO) > 0
                    && sku.getPrice().compareTo(sku.getMaxPrice()) > 0) {
                throw new CommonException("销售价不能大于最高价格");
            }
        }
    }

}
