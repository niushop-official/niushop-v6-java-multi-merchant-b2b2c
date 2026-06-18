package com.niu.mall.service.admin.goods_select.seckill.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.site.Site;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.entity.goods.MallGoodsSku;
import com.niu.mall.entity.goods.MallGoodsSpec;
import com.niu.mall.enums.goods.GoodsAuditStatusEnum;
import com.niu.mall.enums.goods.GoodsStatusEnum;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import com.niu.mall.mapper.goods.MallGoodsSkuMapper;
import com.niu.mall.mapper.goods.MallGoodsSpecMapper;
import com.niu.mall.service.admin.goods.vo.SelectGoodsListVo;
import com.niu.mall.service.admin.goods_select.seckill.ISeckillGoodsSelectService;
import com.niu.mall.service.admin.goods_select.seckill.param.SeckillGoodsSelectParam;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SeckillGoodsSelectServiceImpl implements ISeckillGoodsSelectService {

    @Resource
    private MallGoodsMapper mallGoodsMapper;

    @Resource
    private MallGoodsSkuMapper mallGoodsSkuMapper;

    @Resource
    private MallGoodsSpecMapper mallGoodsSpecMapper;

    @Resource
    private SiteMapper siteMapper;

    @Override
    public PageResult<SelectGoodsListVo> getGoodsList(PageParam pageParam, SeckillGoodsSelectParam param) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<MallGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("site_id,goods_id,goods_name,goods_type,sub_title,goods_image,unit,goods_video,goods_desc,goods_cover,stock,supply_id");
        queryWrapper.eq("delete_time", 0);
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.eq("status", GoodsStatusEnum.ONLINE.getStatus());
        queryWrapper.eq("audit_status", GoodsAuditStatusEnum.AUDIT_ADOPT.getStatus());

        if (param != null) {
            if (StringUtils.isNotBlank(param.getGoodsName())) {
                queryWrapper.like("goods_name", param.getGoodsName());
            }
            if (StringUtils.isNotBlank(param.getGoodsType())) {
                queryWrapper.eq("goods_type", param.getGoodsType());
            }
            if (param.getBrandId() != null) {
                queryWrapper.eq("brand_id", param.getBrandId());
            }
            if (ObjectUtil.isNotEmpty(param.getGoodsCategory())) {
                queryWrapper.like("goods_category", "\"" + param.getGoodsCategory() + "\"");
            }
            if (CollectionUtils.isNotEmpty(param.getLabelIds())) {
                queryWrapper.in("label_ids", param.getLabelIds());
            }
            if (ObjectUtil.isNotEmpty(param.getGoodsMallCategory())) {
                queryWrapper.in("goods_mall_category", param.getGoodsMallCategory());
            }
        }

        queryWrapper.orderByDesc("create_time");

        IPage<MallGoods> iPage = mallGoodsMapper.selectPage(new Page<>(page, limit), queryWrapper);
        if (iPage.getTotal() == 0 || iPage.getRecords().isEmpty()) {
            return PageResult.build(iPage, List.of());
        }

        List<SelectGoodsListVo> goodsList = iPage.getRecords().stream().map(item -> {
            SelectGoodsListVo vo = new SelectGoodsListVo();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());

        List<Integer> goodsIds = goodsList.stream()
                .map(SelectGoodsListVo::getGoodsId)
                .collect(Collectors.toList());

        QueryWrapper<MallGoodsSku> skuQueryWrapper = new QueryWrapper<>();
        skuQueryWrapper.in("goods_id", goodsIds)
                .select("goods_id", "sku_id", "is_default", "sku_name", "sku_image",
                        "price", "cost_price", "sku_spec_format", "stock", "supply_id")
                .orderByDesc("is_default");
        List<MallGoodsSku> mallGoodsSkus = mallGoodsSkuMapper.selectList(skuQueryWrapper);
        List<SelectGoodsListVo.GoodsSkuVO> allSkus = mallGoodsSkus.stream().map(sku -> {
            SelectGoodsListVo.GoodsSkuVO goodsSkuVO = new SelectGoodsListVo.GoodsSkuVO();
            BeanUtils.copyProperties(sku, goodsSkuVO);
            return goodsSkuVO;
        }).toList();

        QueryWrapper<MallGoodsSpec> specQueryWrapper = new QueryWrapper<>();
        specQueryWrapper.in("goods_id", goodsIds)
                .select("goods_id", "spec_name", "spec_values");
        List<MallGoodsSpec> mallGoodsSpecs = mallGoodsSpecMapper.selectList(specQueryWrapper);
        List<SelectGoodsListVo.GoodsSpecVO> allSpecs = CollectUtils.convert(mallGoodsSpecs, SelectGoodsListVo.GoodsSpecVO.class);

        List<Integer> siteIds = goodsList.stream()
                .map(SelectGoodsListVo::getSupplyId)
                .filter(ObjectUtil::isNotEmpty)
                .distinct()
                .toList();
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
            List<SelectGoodsListVo.GoodsSkuVO> skuList = skuMap.getOrDefault(goods.getGoodsId(), new ArrayList<>());
            goods.setSkuList(skuList);

            List<SelectGoodsListVo.GoodsSpecVO> specList = specMap.getOrDefault(goods.getGoodsId(), new ArrayList<>());
            goods.setGoodsSpec(specList);

            if (ObjectUtil.isNotEmpty(goods.getSupplyId())) {
                allSupply.stream()
                        .filter(supply -> supply.getSiteId().equals(goods.getSupplyId()))
                        .findFirst()
                        .ifPresent(goods::setSupply);
            }

            boolean hasMultiSpec = skuList.stream()
                    .anyMatch(sku -> StringUtils.isNotBlank(sku.getSkuSpecFormat()));
            goods.setSpecType(hasMultiSpec ? "multi" : "single");

            if (StringUtils.isNotBlank(goods.getGoodsCover())) {
                goods.setGoodsCoverThumbSmall(CommonUtils.thumbImageSmall(goods.getSiteId(), goods.getGoodsCover()));
            }

            skuList.stream()
                    .filter(sku -> sku.getIsDefault() != null && sku.getIsDefault() == 1)
                    .findFirst()
                    .ifPresent(defaultSku -> {
                        SelectGoodsListVo.DefaultGoodsSkuVO goodsSku = new SelectGoodsListVo.DefaultGoodsSkuVO();
                        goodsSku.setGoodsId(defaultSku.getGoodsId());
                        goodsSku.setSkuId(defaultSku.getSkuId());
                        goodsSku.setPrice(defaultSku.getPrice());
                        goodsSku.setCostPrice(defaultSku.getCostPrice());
                        goodsSku.setStock(defaultSku.getStock());
                        goods.setGoodsSku(goodsSku);
                        goods.setSkuId(defaultSku.getSkuId());
                        goods.setPrice(defaultSku.getPrice());
                        goods.setCostPrice(defaultSku.getCostPrice());
                    });
        }

        return PageResult.build(page, limit, iPage.getTotal(), goodsList);
    }
}
