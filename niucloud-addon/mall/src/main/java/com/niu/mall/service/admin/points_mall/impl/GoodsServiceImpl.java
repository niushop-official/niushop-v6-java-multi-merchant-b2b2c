package com.niu.mall.service.admin.points_mall.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.goods.GoodsCategory;
import com.niu.core.mapper.goods.GoodsCategoryMapper;
import com.niu.core.entity.goods.MallGoods;
import com.niu.core.entity.goods.MallGoodsSku;
import com.niu.mall.entity.goods.MallGoodsSpec;
import com.niu.core.enums.goods.GoodsTypeEnum;
import com.niu.core.mapper.goods.MallGoodsMapper;
import com.niu.core.mapper.goods.MallGoodsSkuMapper;
import com.niu.mall.mapper.goods.MallGoodsSpecMapper;
import com.niu.mall.service.admin.points_mall.IGoodsService;
import com.niu.mall.service.admin.points_mall.param.PointsMallGoodsSelectDetailParam;
import com.niu.mall.service.admin.points_mall.param.PointsMallGoodsSelectParam;
import com.niu.mall.service.admin.points_mall.vo.PointsMallGoodsSelectVo;
import com.niu.mall.service.admin.points_mall.vo.PointsMallGoodsSelectDetailVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 订单实现
 */
@Service
public class GoodsServiceImpl implements IGoodsService {

    @Resource
    private MallGoodsMapper mallGoodsMapper;
    
    @Resource
    private MallGoodsSkuMapper mallGoodsSkuMapper;
    
    @Resource
    private MallGoodsSpecMapper mallGoodsSpecMapper;
    
    @Resource
    private GoodsCategoryMapper goodsCategoryMapper;

    @Override
    public PageResult<PointsMallGoodsSelectVo> getMallGoodsSelectPage(PointsMallGoodsSelectParam param, PageParam pageParam) {
        MPJQueryWrapper<MallGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("g")
                .leftJoin("?_mall_goods_sku goodsSku ON g.goods_id = goodsSku.goods_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_site site ON g.site_id = site.site_id".replace("?_", GlobalConfig.tablePrefix));

        queryWrapper.select("g.site_id", "g.goods_id", "g.goods_name", "g.goods_type", "g.goods_cover", "g.stock");
        queryWrapper.select("goodsSku.sku_id", "goodsSku.sku_name", "goodsSku.goods_id as sku_goods_id", "goodsSku.price", "goodsSku.stock as sku_stock");
        queryWrapper.select("site.site_id as site_site_id", "site.site_name");

        queryWrapper.eq("goodsSku.is_default", 1);
        
        // 商品类型：如果参数有传则使用参数，否则默认为real
        if (StrUtil.isNotBlank(param.getGoodsType())) {
            queryWrapper.eq("g.goods_type", param.getGoodsType());
        } else {
            queryWrapper.eq("g.goods_type", "real");
        }

        if (param.getSiteId() != null) {
            queryWrapper.eq("g.site_id", param.getSiteId());
        }

        if (StrUtil.isNotBlank(param.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper
                    .like("g.goods_name", param.getKeyword())
                    .or()
                    .like("g.sub_title", param.getKeyword())
            );
        }

        if (StrUtil.isNotBlank(param.getGoodsMallCategory())) {
            queryWrapper.like("g.goods_mall_category", "\"" + param.getGoodsMallCategory() + "\"");
        }

        queryWrapper.orderByAsc("g.sort").orderByDesc("g.create_time");

        Page<PointsMallGoodsSelectVo> page = new Page<>(pageParam.getPage(), pageParam.getLimit());
        Page<PointsMallGoodsSelectVo> result = mallGoodsMapper.selectJoinPage(page, PointsMallGoodsSelectVo.class, queryWrapper);

        for (PointsMallGoodsSelectVo vo : result.getRecords()) {
            vo.setGoodsTypeName(GoodsTypeEnum.getNameByType(vo.getGoodsType()));

            // 设置缩略图，如果生成失败则使用原图
            try {
                String thumbSmall = CommonUtils.thumbImageMiddle(RequestUtils.siteId(), vo.getGoodsCover());
                vo.setGoodsCoverThumbSmall(ObjectUtil.isEmpty(thumbSmall) ? vo.getGoodsCover() : thumbSmall);

                String thumbMid = CommonUtils.thumbImageMiddle(RequestUtils.siteId(), vo.getGoodsCover());
                vo.setGoodsCoverThumbMid(ObjectUtil.isEmpty(thumbMid) ? vo.getGoodsCover() : thumbMid);
            } catch (Exception e) {
                // 缩略图生成失败，使用原图
                vo.setGoodsCoverThumbSmall(vo.getGoodsCover());
                vo.setGoodsCoverThumbMid(vo.getGoodsCover());
            }
        }

        return PageResult.build(pageParam.getPage(), pageParam.getLimit(), result.getTotal()).setData(result.getRecords());
    }

    @Override
    public PointsMallGoodsSelectDetailVo getMallGoodsSelectDetail(PointsMallGoodsSelectDetailParam param) {
        PointsMallGoodsSelectDetailVo result = new PointsMallGoodsSelectDetailVo();
        
        if (ObjectUtil.isNotEmpty(param.getGoodsId())) {
            QueryWrapper<MallGoods> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("goods_id", "goods_name", "sub_title", "goods_type", "goods_cover", "goods_image", "goods_video", "goods_desc", "goods_mall_category", "unit");
            queryWrapper.eq("goods_id", param.getGoodsId());
            MallGoods goodsEntity = mallGoodsMapper.selectOne(queryWrapper);
            
            if (ObjectUtil.isNotEmpty(goodsEntity)) {
                PointsMallGoodsSelectDetailVo.GoodsInfo goodsInfo = new PointsMallGoodsSelectDetailVo.GoodsInfo();
                
                goodsInfo.setGoodsName(goodsEntity.getGoodsName());
                goodsInfo.setSubTitle(goodsEntity.getSubTitle());
                goodsInfo.setGoodsType(goodsEntity.getGoodsType());
                goodsInfo.setGoodsCover(goodsEntity.getGoodsCover());
                goodsInfo.setGoodsImage(goodsEntity.getGoodsImage());
                goodsInfo.setGoodsVideo(goodsEntity.getGoodsVideo());
                goodsInfo.setGoodsDesc(goodsEntity.getGoodsDesc());
                goodsInfo.setGoodsMallCategory(goodsEntity.getGoodsMallCategory());
                goodsInfo.setUnit(goodsEntity.getUnit());
                
                if (ObjectUtil.isNotEmpty(goodsEntity.getGoodsMallCategory())) {
                    QueryWrapper<GoodsCategory> categoryWrapper = new QueryWrapper<>();
                    categoryWrapper.eq("category_id", goodsEntity.getGoodsMallCategory());
                    categoryWrapper.eq("site_id", RequestUtils.siteId());
                    GoodsCategory category = goodsCategoryMapper.selectOne(categoryWrapper);
                    if (ObjectUtil.isEmpty(category)) {
                        goodsInfo.setGoodsMallCategory("");
                    }
                }
                
                QueryWrapper<MallGoodsSku> skuWrapper = new QueryWrapper<>();
                skuWrapper.select("sku_id", "sku_name", "sku_image", "goods_id", "sku_spec_format", "price", "market_price", "is_default");
                skuWrapper.eq("goods_id", param.getGoodsId());
                skuWrapper.orderByAsc("sku_id");
                List<MallGoodsSku> skuEntityList = mallGoodsSkuMapper.selectList(skuWrapper);
                
                List<PointsMallGoodsSelectDetailVo.SkuInfo> skuList = new ArrayList<>();
                for (MallGoodsSku sku : skuEntityList) {
                    PointsMallGoodsSelectDetailVo.SkuInfo skuInfo = new PointsMallGoodsSelectDetailVo.SkuInfo();
                    skuInfo.setSkuId(sku.getSkuId());
                    skuInfo.setSkuName(sku.getSkuName());
                    skuInfo.setSkuImage(sku.getSkuImage());
                    skuInfo.setGoodsId(sku.getGoodsId());
                    skuInfo.setSkuSpecFormat(sku.getSkuSpecFormat());
                    skuInfo.setPrice(sku.getPrice());
                    skuInfo.setMarketPrice(sku.getMarketPrice());
                    skuInfo.setIsDefault(sku.getIsDefault());
                    skuInfo.setIsJoin(1);
                    skuList.add(skuInfo);
                }
                goodsInfo.setSkuList(skuList);
                
                List<String> tempSkuData = skuEntityList.stream()
                        .map(MallGoodsSku::getSkuSpecFormat)
                        .filter(StrUtil::isNotBlank)
                        .collect(Collectors.toList());
                
                goodsInfo.setSpecType("single");
                if (!tempSkuData.isEmpty()) {
                    goodsInfo.setSpecType("multi");
                    
                    QueryWrapper<MallGoodsSpec> specWrapper = new QueryWrapper<>();
                    specWrapper.select("spec_id", "goods_id", "spec_name", "spec_values");
                    specWrapper.eq("goods_id", param.getGoodsId());
                    specWrapper.orderByAsc("spec_id");
                    List<MallGoodsSpec> specEntityList = mallGoodsSpecMapper.selectList(specWrapper);
                    
                    List<PointsMallGoodsSelectDetailVo.SpecInfo> specList = new ArrayList<>();
                    for (MallGoodsSpec spec : specEntityList) {
                        PointsMallGoodsSelectDetailVo.SpecInfo specInfo = new PointsMallGoodsSelectDetailVo.SpecInfo();
                        specInfo.setSpecId(spec.getSpecId());
                        specInfo.setGoodsId(spec.getGoodsId());
                        specInfo.setSpecName(spec.getSpecName());
                        specInfo.setSpecValues(spec.getSpecValues());
                        specList.add(specInfo);
                    }
                    goodsInfo.setSpecList(specList);
                }
                
                goodsInfo.setSource("mall");
                goodsInfo.setSourceId(goodsEntity.getGoodsId());
                goodsInfo.setGoodsCategory(goodsEntity.getGoodsMallCategory());
                goodsInfo.setStatus(1);
                
                result.setGoodsInfo(goodsInfo);
            }
        }
        
        return result;
    }
}
