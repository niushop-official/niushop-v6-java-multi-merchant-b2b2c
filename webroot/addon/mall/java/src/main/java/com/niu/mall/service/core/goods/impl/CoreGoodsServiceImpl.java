package com.niu.mall.service.core.goods.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.entity.goods.MallGoodsSku;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import com.niu.mall.mapper.goods.MallGoodsSkuMapper;
import com.niu.mall.service.core.goods.ICoreGoodsService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class CoreGoodsServiceImpl implements ICoreGoodsService {

    @Resource
    MallGoodsMapper mallGoodsMapper;

    @Resource
    MallGoodsSkuMapper mallGoodsSkuMapper;

    /**
     * 增加销量
     *
     * @param siteId
     * @param goodsId
     * @param skuId
     * @param num
     */
    @Override
    public void incSaleNum(Integer siteId, Integer goodsId, Integer skuId, Integer num) {
        UpdateWrapper goods = new UpdateWrapper<MallGoods>();
        goods.eq("goods_id", goodsId);
        goods.eq("site_id", siteId);
        goods.setSql("sale_num = sale_num + " + num);
        mallGoodsMapper.update(null, goods);

        UpdateWrapper sku = new UpdateWrapper<MallGoodsSku>();
        sku.eq("sku_id", skuId);
        sku.eq("site_id", siteId);
        sku.setSql("sale_num = sale_num + " + num);
        mallGoodsSkuMapper.update(null, sku);
    }

    /**
     * 减少销量
     *
     * @param siteId
     * @param goodsId
     * @param skuId
     * @param num
     */
    @Override
    public void decSaleNum(Integer siteId, Integer goodsId, Integer skuId, Integer num) {
        UpdateWrapper goods = new UpdateWrapper<MallGoods>();
        goods.eq("goods_id", goodsId);
        goods.eq("site_id", siteId);
        goods.setSql("sale_num = GREATEST(0, sale_num - " + num + ")");
        mallGoodsMapper.update(null, goods);

        UpdateWrapper sku = new UpdateWrapper<MallGoodsSku>();
        sku.eq("sku_id", skuId);
        sku.eq("site_id", siteId);
        sku.setSql("sale_num = GREATEST(0, sale_num - " + num + ")");
        mallGoodsSkuMapper.update(null, sku);
    }

    /**
     * 增加库存
     *
     * @param siteId
     * @param goodsId
     * @param skuId
     * @param num
     */
    @Override
    public void incStock(Integer siteId, Integer goodsId, Integer skuId, Integer num) {
        UpdateWrapper goods = new UpdateWrapper<MallGoods>();
        goods.eq("goods_id", goodsId);
        goods.eq("site_id", siteId);
        goods.setSql("stock = stock + " + num);
        mallGoodsMapper.update(null, goods);

        UpdateWrapper sku = new UpdateWrapper<MallGoodsSku>();
        sku.eq("sku_id", skuId);
        sku.eq("site_id", siteId);
        sku.setSql("stock = stock + " + num);
        mallGoodsSkuMapper.update(null, sku);
    }

    /**
     * 减少库存
     *
     * @param siteId
     * @param goodsId
     * @param skuId
     * @param num
     */
    @Override
    public void decStock(Integer siteId, Integer goodsId, Integer skuId, Integer num) {
        UpdateWrapper goods = new UpdateWrapper<MallGoods>();
        goods.eq("goods_id", goodsId);
        goods.eq("site_id", siteId);
        goods.setSql("stock = stock - " + num);
        mallGoodsMapper.update(null, goods);

        UpdateWrapper sku = new UpdateWrapper<MallGoodsSku>();
        sku.eq("sku_id", skuId);
        sku.eq("site_id", siteId);
        sku.setSql("stock = stock - " + num);
        mallGoodsSkuMapper.update(null, sku);
    }
}
