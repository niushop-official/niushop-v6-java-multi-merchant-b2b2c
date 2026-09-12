package com.niu.mall.service.api.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.api.member.vo.MemberLevelInfoVo;
import com.niu.core.entity.goods.MallGoods;
import com.niu.mall.entity.goods.MallGoodsBrowse;
import com.niu.core.entity.goods.MallGoodsSku;
import com.niu.mall.mapper.goods.MallGoodsBrowseMapper;
import com.niu.core.mapper.goods.MallGoodsMapper;
import com.niu.core.mapper.goods.MallGoodsSkuMapper;
import com.niu.mall.service.api.goods.IGoodsBrowseService;
import com.niu.mall.service.api.goods.param.GoodsBrowseParam;
import com.niu.mall.service.api.goods.param.GoodsBrowseSearchParam;
import com.niu.mall.service.api.goods.vo.GoodsBrowseListVo;
import com.niu.mall.service.core.goods.ICoreGoodsActivePriceService;
import com.niu.mall.service.core.goods.ICoreGoodsStatService;
import com.niu.mall.service.core.goods.param.GetShowPriceParam;
import com.niu.mall.service.core.goods.param.GoodsAddStatParam;
import com.niu.mall.service.core.goods.vo.ShowPriceVo;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.niu.core.common.exception.AdminException;

@Service
public class GoodsBrowseServiceImpl implements IGoodsBrowseService {

    @Resource
    MallGoodsBrowseMapper mallGoodsBrowseMapper;

    @Resource
    MallGoodsMapper mallGoodsMapper;

    @Resource
    MallGoodsSkuMapper mallGoodsSkuMapper;

    @Resource
    IMemberLevelService memberLevelService;

    @Resource
    ICoreGoodsStatService coreGoodsStatService;

    @Resource
    ICoreGoodsActivePriceService coreGoodsActivePriceService;

    @Override
    public PageResult<GoodsBrowseListVo> page(PageParam pageParam, GoodsBrowseSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MemberLevelInfoVo memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());

        MPJQueryWrapper<MallGoodsBrowse> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("b")
                .leftJoin("?_mall_goods g ON b.goods_id = g.goods_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_mall_goods_sku gs ON b.sku_id = gs.sku_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("b.*,gs.price,gs.sale_price,gs.market_price,gs.member_price,gs.svip_discount,gs.svip_price,g.member_discount,g.vip_card_discount,g.status,g.delete_time");
        queryWrapper.eq("b.member_id", RequestUtils.memberId());
        queryWrapper.eq("g.delete_time", 0);
        if (ObjectUtil.isNotEmpty(searchParam.getDate()))
            QueryMapperUtils.buildByTime(queryWrapper, "b.browse_time", searchParam.getDate());
        queryWrapper.orderByDesc("b.browse_time");
        IPage<GoodsBrowseListVo> iPage = mallGoodsBrowseMapper.selectJoinPage(new Page<>(page, limit), GoodsBrowseListVo.class, queryWrapper);

        for (GoodsBrowseListVo vo : iPage.getRecords()) {
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
            GoodsBrowseListVo.GoodsInfo goodsInfo = new GoodsBrowseListVo.GoodsInfo();
            goodsInfo.setStatus(vo.getStatus());
            goodsInfo.setDeleteTime(vo.getDeleteTime());
            vo.setGoods(goodsInfo);
        }

        return PageResult.build(page, limit, iPage.getTotal()).setData(iPage.getRecords());
    }

    @Override
    public void add(GoodsBrowseParam addParam) {
        MallGoods goods = mallGoodsMapper.selectOne(new QueryWrapper<MallGoods>().select("site_id", "goods_name", "goods_cover").eq("goods_id", addParam.getGoodsId()));
        Assert.notNull(goods, "商品不存在");

        MallGoodsSku sku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>().eq("goods_id", addParam.getGoodsId()).eq("is_default", 1));

        MallGoodsBrowse browse = mallGoodsBrowseMapper.selectOne(new QueryWrapper<MallGoodsBrowse>()
                .eq("member_id", RequestUtils.memberId())
                .eq("goods_id", addParam.getGoodsId())
        );

        MallGoodsBrowse model = new MallGoodsBrowse();
        model.setGoodsId(addParam.getGoodsId());
        model.setBrowseTime(System.currentTimeMillis() / 1000);
        model.setSiteId(goods.getSiteId());
        model.setMemberId(RequestUtils.memberId());
        model.setGoodsName(goods.getGoodsName());
        model.setGoodsCover(goods.getGoodsCover());
        model.setSkuId(sku.getSkuId());

        boolean isFirstBrowse = browse == null;

        if (browse == null) {
            mallGoodsBrowseMapper.insert(model);
        } else {
            model.setId(browse.getId());
            mallGoodsBrowseMapper.updateById(model);
        }

        if (isFirstBrowse) {
            GoodsAddStatParam param = new GoodsAddStatParam();
            param.setSiteId(goods.getSiteId());
            param.setGoodsId(addParam.getGoodsId());
            param.setGoodsVisitMemberCount(1);
            coreGoodsStatService.addStat(param);
        }
    }

    @Override
    public void del(HttpServletRequest request) {
        Map<String, String[]> paramMap = request.getParameterMap();
        List<String> goodsIdsList = new ArrayList<>();
        
        // 只处理goods_ids参数，不包括其他参数
        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            String paramName = entry.getKey();
            // 精确匹配goods_ids或goods_ids[数字]格式
            if (paramName.equals("goods_ids") || paramName.matches("goods_ids\\[\\d+\\]")) {
                for (String value : entry.getValue()) {
                    if (value != null && !value.trim().isEmpty()) {
                        goodsIdsList.add(value.trim());
                    }
                }
            }
        }
        
        if (goodsIdsList.isEmpty()) {
            throw new AdminException("请选择套餐");
        }
        
        // 直接构建查询条件，不需要中间参数对象
        QueryWrapper<MallGoodsBrowse> queryWrapper = new QueryWrapper<MallGoodsBrowse>()
                .eq("member_id", RequestUtils.memberId());
        
        if (goodsIdsList.size() == 1) {
            queryWrapper.eq("goods_id", Integer.parseInt(goodsIdsList.get(0)));
        } else {
            Integer[] goodsIds = goodsIdsList.stream()
                    .map(Integer::parseInt)
                    .toArray(Integer[]::new);
            queryWrapper.in("goods_id", goodsIds);
        }
        
        mallGoodsBrowseMapper.delete(queryWrapper);
    }
}
