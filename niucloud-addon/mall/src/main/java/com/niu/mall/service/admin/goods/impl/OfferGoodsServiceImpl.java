package com.niu.mall.service.admin.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.site.Site;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.entity.goods.MallGoods;
import com.niu.core.entity.goods.MallGoodsSku;
import com.niu.mall.entity.goods.MallGoodsSpec;
import com.niu.core.enums.goods.GoodsAuditStatusEnum;
import com.niu.core.enums.goods.GoodsStatusEnum;
import com.niu.core.mapper.goods.MallGoodsMapper;
import com.niu.core.mapper.goods.MallGoodsSkuMapper;
import com.niu.mall.mapper.goods.MallGoodsSpecMapper;
import com.niu.mall.service.admin.goods.IOfferGoodsService;
import com.niu.mall.service.admin.goods.param.GoodsOutPutParam;
import com.niu.mall.service.admin.goods.vo.SelectGoodsListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 输出商品服务层
 */
@Service
public class OfferGoodsServiceImpl implements IOfferGoodsService {

    @Resource
    MallGoodsMapper mallGoodsMapper;

    @Resource
    MallGoodsSkuMapper mallGoodsSkuMapper;
    @Resource
    MallGoodsSpecMapper mallGoodsSpecMapper;
    @Resource
    SiteMapper siteMapper;


    public PageResult<SelectGoodsListVo> getOfferGoodsList(PageParam pageParam, GoodsOutPutParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<MallGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("site_id,goods_id,goods_name,goods_type,sub_title,goods_image,unit,goods_video,goods_desc,goods_cover,stock,supply_id");
        queryWrapper.eq("delete_time", 0);

        queryWrapper.eq("status", GoodsStatusEnum.ONLINE.getStatus());
        queryWrapper.eq("audit_status", GoodsAuditStatusEnum.AUDIT_ADOPT.getStatus());
        queryWrapper.eq("site_id", searchParam.getSiteId());
        if (StringUtils.isNotBlank(searchParam.getGoodsName())) {
            queryWrapper.like("goods_name", searchParam.getGoodsName());
        }
        if (StringUtils.isNotBlank(searchParam.getGoodsType())) {
            queryWrapper.eq("goods_type", searchParam.getGoodsType());
        }
        if (searchParam.getBrandId() != null) {
            queryWrapper.eq("brand_id", searchParam.getBrandId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsCategory())) {
            JSONArray valueList = JSONUtil.parseArray(searchParam.getGoodsCategory());
            queryWrapper.and(wrapper -> {
                for (int i = 0; i < valueList.size(); i++) {
                    String likeValue = valueList.getStr(i);
                    if (i == 0) {
                        // 第一个条件直接like
                        wrapper.like("goods_category", likeValue);
                    } else {
                        // 后续条件用OR拼接
                        wrapper.or().like("goods_category", likeValue);
                    }
                }
            });
        }
        if (CollectionUtils.isNotEmpty(searchParam.getLabelIds())) {
            queryWrapper.in("label_ids", searchParam.getLabelIds());
        }

        queryWrapper.orderByDesc("create_time");

        IPage<MallGoods> iPage = mallGoodsMapper.selectPage(new Page<>(page, limit), queryWrapper);
        if (iPage.getTotal() == 0 || iPage.getRecords().isEmpty()) {
            return PageResult.build(iPage, List.of());
        }
        List<SelectGoodsListVo> goodsList = iPage.getRecords().stream().map(item -> {
            SelectGoodsListVo selectGoodsListVo = new SelectGoodsListVo();
            BeanUtils.copyProperties(item, selectGoodsListVo);
            selectGoodsListVo.setSourceId(item.getGoodsId());
            return selectGoodsListVo;
        }).collect(Collectors.toList());
        List<Integer> goodsIds = goodsList.stream()
                .map(SelectGoodsListVo::getGoodsId)
                .collect(Collectors.toList());

        QueryWrapper<MallGoodsSku> skuQueryWrapper = new QueryWrapper<>();
        skuQueryWrapper.in("goods_id", goodsIds)
                .select("goods_id", "sku_id", "is_default", "sku_name", "sku_image",
                        "price", "cost_price", "is_default", "sku_spec_format", "sku_id", "stock", "supply_id")
                .orderByDesc("is_default");
        List<MallGoodsSku> shopGoodsSkus = mallGoodsSkuMapper.selectList(skuQueryWrapper);
        List<SelectGoodsListVo.GoodsSkuVO> allSkus = shopGoodsSkus.stream().map(sku -> {
            SelectGoodsListVo.GoodsSkuVO goodsSkuVO = new SelectGoodsListVo.GoodsSkuVO();
            BeanUtils.copyProperties(sku, goodsSkuVO);
            goodsSkuVO.setSourceSkuId(sku.getSkuId());
            return goodsSkuVO;
        }).toList();

        QueryWrapper<MallGoodsSpec> specQueryWrapper = new QueryWrapper<>();
        specQueryWrapper.in("goods_id", goodsIds)
                .select("goods_id", "spec_name", "spec_values");
        List<MallGoodsSpec> shopGoodsSpecs = mallGoodsSpecMapper.selectList(specQueryWrapper);
        List<SelectGoodsListVo.GoodsSpecVO> allSpecs = CollectUtils.convert(shopGoodsSpecs, SelectGoodsListVo.GoodsSpecVO.class);
        List<Integer> siteIds = goodsList.stream().map(SelectGoodsListVo::getSupplyId).distinct().toList();
        List<SelectGoodsListVo.SupplyInfo> allSupply = new ArrayList<>();
        if (!siteIds.isEmpty()) {
            QueryWrapper<Site> siteQueryWrapper = new QueryWrapper<>();
            siteQueryWrapper.in("site_id", siteIds).gt("site_id", 0)
                    .select("site_id", "site_name");
            List<Site> siteList = siteMapper.selectList(siteQueryWrapper);
            allSupply = CollectUtils.convert(siteList, SelectGoodsListVo.SupplyInfo.class);
        }

        Map<Integer, List<SelectGoodsListVo.GoodsSkuVO>> skuMap = allSkus.stream()
                .collect(Collectors.groupingBy(SelectGoodsListVo.GoodsSkuVO::getGoodsId));

        Map<Integer, List<SelectGoodsListVo.GoodsSpecVO>> specMap = allSpecs.stream()
                .collect(Collectors.groupingBy(SelectGoodsListVo.GoodsSpecVO::getGoodsId));

        for (SelectGoodsListVo goods : goodsList) {
            // 设置SKU列表
            List<SelectGoodsListVo.GoodsSkuVO> skuList = skuMap.getOrDefault(goods.getGoodsId(), new ArrayList<>());
            goods.setSkuList(skuList);

            // 设置规格信息
            List<SelectGoodsListVo.GoodsSpecVO> specList = specMap.getOrDefault(goods.getGoodsId(), new ArrayList<>());
            goods.setGoodsSpec(specList);

            //设置供应商
            if (ObjectUtil.isNotEmpty(goods.getSupplyId())) {
                List<SelectGoodsListVo.SupplyInfo> supplyList = allSupply.stream()
                        .filter(supply -> supply.getSiteId().equals(goods.getSupplyId()))
                        .toList();
                if (!supplyList.isEmpty()) {
                    goods.setSupply(supplyList.get(0));
                }
            }

            // 判断规格类型
            boolean hasMultiSpec = skuList.stream()
                    .anyMatch(sku -> StringUtils.isNotBlank(sku.getSkuSpecFormat()));
            goods.setSpecType(hasMultiSpec ? "multi" : "single");

            // 设置缩略图（根据实际业务实现）
            if (StringUtils.isNotBlank(goods.getGoodsCover())) {
                goods.setGoodsCoverThumbSmall(CommonUtils.thumbImageSmall(goods.getSiteId(), goods.getGoodsCover()));
            }
        }
        return PageResult.build(page, limit, iPage.getTotal(), goodsList);
    }

    /**
     * 商品详情
     *
     * @param
     * @return ShopGoodsInfoVo
     */
    @Override
    public SelectGoodsListVo getOfferGoodsInfo(Integer goodsId) {
        PageParam pageParam = new PageParam();
        pageParam.setPage(1);
        pageParam.setLimit(1);
        GoodsOutPutParam goodsOutPutParam = new GoodsOutPutParam();
        goodsOutPutParam.setGoodsId(goodsId);
        PageResult<SelectGoodsListVo> offerGoodsList = getOfferGoodsList(pageParam, goodsOutPutParam);

        return offerGoodsList.getData().get(0);
    }
}
