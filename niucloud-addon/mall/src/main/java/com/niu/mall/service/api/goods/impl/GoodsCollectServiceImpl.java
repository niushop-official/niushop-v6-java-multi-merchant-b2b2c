package com.niu.mall.service.api.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.goods.MallGoods;
import com.niu.mall.entity.goods.MallGoodsCollect;
import com.niu.mall.entity.goods.MallGoodsStat;
import com.niu.mall.mapper.goods.MallGoodsCollectMapper;
import com.niu.core.mapper.goods.MallGoodsMapper;
import com.niu.mall.mapper.goods.MallGoodsStatMapper;
import com.niu.mall.service.api.goods.IGoodsCollectService;
import com.niu.mall.service.api.goods.vo.MallGoodsCollectVo;
import com.niu.mall.service.core.goods.ICoreGoodsActivePriceService;
import com.niu.mall.service.core.goods.ICoreGoodsCollectNumService;
import com.niu.mall.service.core.goods.ICoreGoodsStatService;
import com.niu.mall.service.core.goods.param.GetShowPriceParam;
import com.niu.mall.service.core.goods.param.GoodsAddStatParam;
import com.niu.mall.service.core.goods.vo.ShowPriceVo;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class GoodsCollectServiceImpl implements IGoodsCollectService {

    @Resource
    ICoreGoodsStatService coreGoodsStatService;

    @Resource
    ICoreGoodsCollectNumService coreGoodsCollectNumService;

    @Resource
    MallGoodsStatMapper mallGoodsStatMapper;

    @Resource
    MallGoodsCollectMapper mallGoodsCollectMapper;

    @Resource
    ICoreGoodsActivePriceService coreGoodsActivePriceService;

    @Resource
    MallGoodsMapper mallGoodsMapper;

    @Override
    public PageResult<MallGoodsCollectVo> getMemberGoodsCollectList(PageParam pageParam) {

        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<MallGoodsCollect> queryWrapper = new MPJQueryWrapper<>();

        queryWrapper.setAlias("gc")
                .leftJoin("?_mall_goods ng ON gc.goods_id = ng.goods_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_mall_goods_sku ngs ON gc.goods_id = ngs.goods_id and ngs.is_default = 1".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.eq("gc.member_id", RequestUtils.memberId());
        queryWrapper.select("gc.*", "ng.goods_name", "ng.goods_cover", "ng.member_discount", "ng.vip_card_discount", "ng.is_discount", "ng.status", "ngs.sku_name", "ngs.price","ngs.sale_price", "ngs.price", "ngs.sku_id", "ngs.member_price", "ngs.svip_discount", "ngs.svip_price");
        queryWrapper.orderByDesc("gc.id");

        IPage<MallGoodsCollectVo> iPage = mallGoodsCollectMapper.selectJoinPage(new Page<>(page, limit), MallGoodsCollectVo.class, queryWrapper);
        for (MallGoodsCollectVo vo : iPage.getRecords()) {
            ShowPriceVo skuShowPrice = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                    .goodsId(vo.getGoodsId())
                    .salePrice(vo.getSalePrice())
                    .skuId(vo.getSkuId())
                    .price(vo.getPrice())
                    .memberDiscount(vo.getMemberDiscount())
                    .memberPrice(vo.getMemberPrice())
                    .vipCardDiscount(vo.getVipCardDiscount())
                    .svipDiscount(vo.getSvipDiscount())
                    .svipPrice(vo.getSvipPrice())
                    .memberId(RequestUtils.memberId())
                    .build());
            vo.setShowPrice(skuShowPrice.getShowPrice());
            vo.setShowType(skuShowPrice.getShowType());
            
            // 组装嵌套的goods对象
            MallGoodsCollectVo.GoodsInfo goodsInfo = new MallGoodsCollectVo.GoodsInfo();
            goodsInfo.setGoodsId(vo.getGoodsId());
            goodsInfo.setGoodsName(vo.getGoodsName());
            goodsInfo.setGoodsCover(vo.getGoodsCover());
            goodsInfo.setMemberDiscount(vo.getMemberDiscount());
            goodsInfo.setVipCardDiscount(vo.getVipCardDiscount());
            goodsInfo.setStatus(vo.getStatus());
            // 从查询结果中获取is_discount，如果为null则设置为0
            goodsInfo.setIsDiscount(vo.getIsDiscount() != null ? vo.getIsDiscount() : 0);
            vo.setGoods(goodsInfo);
            
            // 组装嵌套的goodsSku对象
            MallGoodsCollectVo.GoodsSkuInfo goodsSkuInfo = new MallGoodsCollectVo.GoodsSkuInfo();
            goodsSkuInfo.setSkuId(vo.getSkuId());
            goodsSkuInfo.setSkuName(vo.getSkuName());
            goodsSkuInfo.setPrice(vo.getPrice());
            goodsSkuInfo.setSalePrice(vo.getSalePrice());
            // 安全地转换memberPrice，如果为null或空字符串则设置为null
            if (ObjectUtil.isNotEmpty(vo.getMemberPrice())) {
                try {
                    goodsSkuInfo.setMemberPrice(new java.math.BigDecimal(vo.getMemberPrice()));
                } catch (Exception e) {
                    goodsSkuInfo.setMemberPrice(null);
                }
            } else {
                goodsSkuInfo.setMemberPrice(null);
            }
            goodsSkuInfo.setMemberDiscount(vo.getMemberDiscount());
            goodsSkuInfo.setVipCardDiscount(vo.getVipCardDiscount());
            vo.setGoodsSku(goodsSkuInfo);
        }

        return PageResult.build(iPage);
    }

    @Override
    public void addGoodsCollect(Integer goodsId) {
        MallGoodsCollect model = mallGoodsCollectMapper.selectOne(new QueryWrapper<MallGoodsCollect>().eq("goods_id", goodsId).eq("member_id", RequestUtils.memberId()));
        if (ObjectUtil.isNotEmpty(model)) throw new CommonException("商品已收藏");
        MallGoods goods = mallGoodsMapper.selectById(goodsId);
        model = new MallGoodsCollect();
        model.setGoodsId(goodsId);
        model.setMemberId(RequestUtils.memberId());
        model.setSiteId(goods.getSiteId());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        mallGoodsCollectMapper.insert(model);

        GoodsAddStatParam param = new GoodsAddStatParam();
        param.setGoodsId(goodsId);
        param.setSiteId(model.getSiteId());
        param.setCollectNum(1);
        coreGoodsStatService.addStat(param);
        coreGoodsCollectNumService.inc(goodsId, 1);
    }

    @Override
    public void cancelGoodsCollect(Integer goodsId) {

        MallGoods goods = mallGoodsMapper.selectById(goodsId);
        if (ObjectUtil.isEmpty(goods)) throw new CommonException("商品不存在");
        mallGoodsCollectMapper.delete(new QueryWrapper<MallGoodsCollect>().eq("goods_id", goodsId).eq("member_id", RequestUtils.memberId()));
        MallGoodsStat stat = mallGoodsStatMapper.selectOne(new QueryWrapper<MallGoodsStat>()
                .eq("site_id", goods.getSiteId())
                .eq("goods_id", goodsId)
                .eq("date", DateUtils.currInitDate()));
        if (stat != null && stat.getCollectNum() != null && stat.getCollectNum() > 0) {
            GoodsAddStatParam param = new GoodsAddStatParam();
            param.setGoodsId(goodsId);
            param.setSiteId(goods.getSiteId());
            param.setCollectNum(-1);
            coreGoodsStatService.addStat(param);
        }
        coreGoodsCollectNumService.inc(goodsId, -1);
    }

    @Override
    public void cancelGoodsCollect(Integer[] goodsId) {
        for (Integer id : goodsId) {
            cancelGoodsCollect(id);
        }
    }
}
