package com.niu.mall.service.api.cart.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.site.Site;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.api.member.vo.MemberLevelInfoVo;
import com.niu.mall.entity.cart.MallCart;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.entity.goods.MallGoodsSku;
import com.niu.mall.entity.goods.MallGoodsSpec;
import com.niu.mall.mapper.cart.MallCartMapper;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import com.niu.mall.mapper.goods.MallGoodsSkuMapper;
import com.niu.mall.mapper.goods.MallGoodsSpecMapper;
import com.niu.mall.service.api.cart.IMallCartService;
import com.niu.mall.service.api.cart.param.MallCartCalculateParam;
import com.niu.mall.service.api.cart.param.MallCartDeleteParam;
import com.niu.mall.service.api.cart.param.MallCartEditParam;
import com.niu.mall.service.api.cart.param.MallCartParam;
import com.niu.mall.service.api.cart.vo.*;
import com.niu.mall.service.api.goods.IGoodsService;
import com.niu.mall.service.core.goods.ICoreGoodsActivePriceService;
import com.niu.mall.service.core.goods.ICoreGoodsCartNumService;
import com.niu.mall.service.core.goods.ICoreGoodsLimitBuyService;
import com.niu.mall.service.core.goods.ICoreGoodsStatService;
import com.niu.mall.service.core.goods.param.GetShowPriceParam;
import com.niu.mall.service.core.goods.param.GoodsAddStatParam;
import com.niu.mall.service.core.goods.vo.ShowPriceVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 购物车实现
 */
@Service
public class MallCartServiceImpl implements IMallCartService {

    @Resource
    MallCartMapper mallCartMapper;

    @Resource
    MallGoodsMapper mallGoodsMapper;

    @Resource
    MallGoodsSpecMapper mallGoodsSpecMapper;

    @Resource
    MallGoodsSkuMapper mallGoodsSkuMapper;

    @Resource
    SiteMapper siteMapper;

    @Resource
    IMemberLevelService memberLevelService;

    @Resource
    IGoodsService goodsService;

    @Resource
    ICoreGoodsLimitBuyService coreGoodsLimitBuyService;

    @Resource
    ICoreGoodsStatService coreGoodsStatService;

    @Resource
    ICoreGoodsCartNumService coreGoodsCartNumService;

    @Resource
    ICoreGoodsActivePriceService coreGoodsActivePriceService;


    /**
     * 购物车列表
     *
     * @return List<ShopCartListVo>
     */
    @Override
    public Map<Integer, MallCartListApiVo> list() {
        QueryWrapper<MallCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id", RequestUtils.memberId());
        queryWrapper.orderByDesc("id");

        List<MallCart> record = mallCartMapper.selectList(queryWrapper);

        List<MallCartListVo> list = new LinkedList<>();
        for (MallCart item : record) {
            MallCartListVo vo = new MallCartListVo();
            BeanUtils.copyProperties(item, vo);

            MallCartListVo.Goods goodsVo = new MallCartListVo.Goods();
            MallGoods goods = mallGoodsMapper.selectOne(new QueryWrapper<MallGoods>().eq("goods_id", item.getGoodsId()));
            BeanUtils.copyProperties(goods, goodsVo);

            //商品限购
            goodsVo.setHasBuy(coreGoodsLimitBuyService.getGoodsHasBuyNumber(goods.getSiteId(), RequestUtils.memberId(), item.getGoodsId()));

            vo.setGoods(goodsVo);

            MallCartListVo.GoodsSku skuVo = new MallCartListVo.GoodsSku();
            MallGoodsSku sku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>().eq("sku_id", item.getSkuId()));
            BeanUtils.copyProperties(sku, skuVo);
            ShowPriceVo skuShowPrice = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                    .goodsId(sku.getGoodsId())
                    .salePrice(sku.getSalePrice())
                    .skuId(sku.getSkuId())
                    .price(sku.getPrice())
                    .memberDiscount(goods.getMemberDiscount())
                    .memberPrice(sku.getMemberPrice())
                    .memberId(RequestUtils.memberId())
                    .build());
            skuVo.setShowPrice(skuShowPrice.getShowPrice());
            skuVo.setShowType(skuShowPrice.getShowType());
            List<MallCartListVo.GoodsSpec> specList = CollectUtils.convert(mallGoodsSpecMapper.selectList(new QueryWrapper<MallGoodsSpec>().eq("goods_id", item.getGoodsId())), MallCartListVo.GoodsSpec.class);
            vo.setGoodsSpec(specList);
            vo.setGoodsSku(skuVo);
            Site site = siteMapper.selectById(item.getSiteId());
            MallCartListVo.SiteInfo siteInfo = new MallCartListVo.SiteInfo();
            if(ObjectUtil.isNotEmpty(site))
            {
                BeanUtils.copyProperties(site, siteInfo);
            }

            vo.setSite(siteInfo);
            list.add(vo);
        }

        Map<Integer, MallCartListApiVo> map = new HashMap<>();
        if(ObjectUtil.isNotEmpty(list))
        {
            for (MallCartListVo item : list) {


                MallCartListApiVo apiVo = new MallCartListApiVo();
                if (map.containsKey(item.getSiteId())) {
                    apiVo = map.get(item.getSiteId());
                }
                apiVo.setSiteId(item.getSiteId());
                apiVo.setSiteName(item.getSite().getSiteName());
                apiVo.setLogo(item.getSite().getLogo());

                if (ObjectUtil.isEmpty(apiVo.getGoodsList())) {
                    apiVo.setGoodsList(new LinkedList<>());

                }
                apiVo.getGoodsList().add(item);

                map.put(item.getSiteId(), apiVo);
            }
        }
        return map;
    }

    @Override
    public Map<Integer, MallCartGoodsListApiVo> goodsList() {
        QueryWrapper<MallCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id", RequestUtils.memberId());
        queryWrapper.orderByDesc("id");

        List<MallCart> record = mallCartMapper.selectList(queryWrapper);

        MemberLevelInfoVo memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());

        List<MallCartGoodsListVo> list = new LinkedList<>();
        for (MallCart item : record) {
            MallCartGoodsListVo vo = new MallCartGoodsListVo();
            BeanUtils.copyProperties(item, vo);

            MallCartGoodsListVo.Goods goodsVo = new MallCartGoodsListVo.Goods();
            MallGoods goods = mallGoodsMapper.selectOne(new QueryWrapper<MallGoods>().eq("goods_id", item.getGoodsId()));
            BeanUtils.copyProperties(goods, goodsVo);

            //商品限购
            goodsVo.setHasBuy(coreGoodsLimitBuyService.getGoodsHasBuyNumber(goods.getSiteId(), RequestUtils.memberId(), item.getGoodsId()));

            vo.setGoods(goodsVo);

            MallCartGoodsListVo.GoodsSku skuVo = new MallCartGoodsListVo.GoodsSku();
            MallGoodsSku sku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>().eq("sku_id", item.getSkuId()));
            BeanUtils.copyProperties(sku, skuVo);


            ShowPriceVo skuShowPrice = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                    .goodsId(sku.getGoodsId())
                    .salePrice(sku.getSalePrice())
                    .skuId(sku.getSkuId())
                    .price(sku.getPrice())
                    .memberDiscount(goods.getMemberDiscount())
                    .memberPrice(sku.getMemberPrice())
                    .memberId(RequestUtils.memberId())
                    .build());
            skuVo.setShowPrice(skuShowPrice.getShowPrice());
            skuVo.setShowType(skuShowPrice.getShowType());

            List<MallCartGoodsListVo.GoodsSpec> specList = CollectUtils.convert(mallGoodsSpecMapper.selectList(new QueryWrapper<MallGoodsSpec>().eq("goods_id", item.getGoodsId())), MallCartGoodsListVo.GoodsSpec.class);
            vo.setGoodsSpec(specList);

            vo.setGoodsSku(skuVo);
            Site site = siteMapper.selectById(item.getSiteId());
            if(ObjectUtil.isEmpty(site))
            {
                //失效数据
                continue;
            }
            MallCartGoodsListVo.SiteInfo siteInfo = new MallCartGoodsListVo.SiteInfo();
            BeanUtils.copyProperties(site, siteInfo);
            vo.setSite(siteInfo);
            list.add(vo);
        }
        Map<Integer, MallCartGoodsListApiVo> map = new HashMap<>();
        if(ObjectUtil.isNotEmpty(list))
        {
            for (MallCartGoodsListVo item : list) {


                MallCartGoodsListApiVo apiVo = new MallCartGoodsListApiVo();
                if (map.containsKey(item.getSiteId())) {
                    apiVo = map.get(item.getSiteId());
                }
                apiVo.setSiteId(item.getSiteId());
                apiVo.setSiteName(item.getSite().getSiteName());
                apiVo.setLogo(item.getSite().getLogo());

                if (ObjectUtil.isEmpty(apiVo.getGoodsList())) {
                    apiVo.setGoodsList(new LinkedList<>());

                }
                apiVo.getGoodsList().add(item);

                map.put(item.getSiteId(), apiVo);
            }
        }
        return map;
    }

    /**
     * 购物车添加
     *
     * @param addParam 参数
     */
    @Override
    public void add(MallCartParam addParam) {
        MallCart cart = mallCartMapper.selectOne(new QueryWrapper<MallCart>()
                .eq("member_id", RequestUtils.memberId())
                .eq("sku_id", addParam.getSkuId())
        );

        MallGoods goods = mallGoodsMapper.selectById(addParam.getGoodsId());
        GoodsAddStatParam param = new GoodsAddStatParam();
        param.setSiteId(goods.getSiteId());
        param.setGoodsId(addParam.getGoodsId());
        param.setCartNum(addParam.getNum());
        coreGoodsStatService.addStat(param);
        coreGoodsCartNumService.inc(addParam.getGoodsId(), addParam.getNum());
        if (cart != null) {
            cart.setNum(cart.getNum() + addParam.getNum());
            mallCartMapper.updateById(cart);
        } else {
            MallCart model = new MallCart();
            BeanUtils.copyProperties(addParam, model);
            model.setSiteId(goods.getSiteId());
            model.setMemberId(RequestUtils.memberId());
            model.setCreateTime(System.currentTimeMillis() / 1000);
            mallCartMapper.insert(model);
        }
    }

    /**
     * 购物车编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(MallCartEditParam editParam) {
        MallCart model = mallCartMapper.selectOne(
                new QueryWrapper<MallCart>()
                        .eq("id", editParam.getId())
                        .eq("member_id", RequestUtils.memberId()));

        Assert.notNull(model, "数据不存在!");
        model.setNum(editParam.getNum());

        if (!model.getSkuId().equals(editParam.getSkuId())) {
            model.setSkuId(editParam.getSkuId());

            mallCartMapper.delete(new QueryWrapper<MallCart>()
                    .eq("sku_id", editParam.getSkuId())
                    .eq("member_id", RequestUtils.memberId()));
        }

        mallCartMapper.updateById(model);

        GoodsAddStatParam param = new GoodsAddStatParam();
        param.setSiteId(model.getSiteId());
        param.setGoodsId(model.getGoodsId());
        param.setCartNum(editParam.getNum() - model.getNum());
        coreGoodsStatService.addStat(param);
        coreGoodsCartNumService.dec(editParam.getGoodsId(), editParam.getNum());
    }

    /**
     * 购物车删除
     *
     * @param param 主键ID
     */
    @Override
    public void del(MallCartDeleteParam param) {
        QueryWrapper<MallCart> queryWrapper = new QueryWrapper<MallCart>()
                .eq("member_id", RequestUtils.memberId());

        if (param.getIds() instanceof Integer) {
            queryWrapper.eq("id", param.getIds());
        } else if (param.getIds() instanceof ArrayList) {
            Integer[] ids = ((ArrayList<?>) param.getIds()).toArray(new Integer[0]);
            queryWrapper.in("id", ids);
        }

        mallCartMapper.delete(queryWrapper);
    }

    /**
     * 清空购物车
     */
    @Override
    public void clear() {
        mallCartMapper.delete(new QueryWrapper<MallCart>().eq("member_id", RequestUtils.memberId()));
    }

    /**
     * 获取购物车数量
     *
     * @param goodsId
     * @return
     */
    @Override
    public Integer getSum(Integer goodsId) {
        QueryWrapper<MallCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id", RequestUtils.memberId());
        if (goodsId > 0) queryWrapper.eq("goods_id", goodsId);
        queryWrapper.select("SUM(num) as num");
        MallCart card = mallCartMapper.selectOne(queryWrapper);
        if (card == null) return 0;
        return card.getNum();
    }
}