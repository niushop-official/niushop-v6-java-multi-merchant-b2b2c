package com.niu.mall.service.admin.goods.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.site.Site;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.api.member.vo.MemberLevelInfoVo;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.site.ICoreSiteService;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.mall.entity.active.MallActiveGoods;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.entity.goods.MallGoodsLabel;
import com.niu.mall.entity.goods.MallGoodsSku;
import com.niu.mall.entity.goods.MallGoodsSpec;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.enums.active.ActiveGoodsTypeEnum;
import com.niu.mall.enums.goods.GoodsSourceEnum;
import com.niu.mall.enums.goods.GoodsStatusEnum;
import com.niu.mall.enums.goods.GoodsTypeEnum;
import com.niu.mall.enums.label.LabelStatusEnum;
import com.niu.mall.event.goods.AfterGoodsEditEvent;
import com.niu.mall.event.goods.AfterMallGoodsStatusEditEvent;
import com.niu.mall.event.goods.CheckGoodsSkuPriceEvent;
import com.niu.mall.mapper.active.MallActiveGoodsMapper;
import com.niu.mall.mapper.goods.MallGoodsLabelMapper;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import com.niu.mall.mapper.goods.MallGoodsSkuMapper;
import com.niu.mall.mapper.goods.MallGoodsSpecMapper;
import com.niu.core.mapper.goods.GoodsBrandMapper;
import com.niu.core.mapper.goods.GoodsCategoryMapper;
import com.niu.core.mapper.goods.GoodsServiceMapper;
import com.niu.core.entity.goods.GoodsBrand;
import com.niu.core.entity.goods.GoodsCategory;
import com.niu.core.entity.goods.GoodsService;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.service.admin.goods.IMallGoodsService;
import com.niu.mall.service.admin.goods.IMallGoodsSkuService;
import com.niu.mall.service.admin.goods.IMallGoodsSpecService;
import com.niu.mall.service.admin.goods.param.*;
import com.niu.mall.service.admin.goods.vo.*;
import com.niu.mall.service.api.cart.vo.MallCartListVo;
import com.niu.mall.service.api.goods.IGoodsService;
import com.niu.mall.service.core.goods.ICoreGoodsLimitBuyService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 商品实现
 */
@Service
public class MallGoodsServiceImpl implements IMallGoodsService {

    @Resource
    MallGoodsMapper mallGoodsMapper;

    @Resource
    MallGoodsLabelMapper mallGoodsLabelMapper;

    @Resource
    MallGoodsSkuMapper mallGoodsSkuMapper;

    @Resource
    MallGoodsSpecMapper mallGoodsSpecMapper;

    @Resource
    IMallGoodsSkuService shopGoodsSkuService;

    @Resource
    IMallGoodsSpecService shopGoodsSpecService;

    @Resource
    MallActiveGoodsMapper mallActiveGoodsMapper;

    @Resource
    MallOrderGoodsMapper mallOrderGoodsMapper;

    @Resource
    ICoreUploadService coreUploadService;

    @Resource
    IGoodsService goodsService;


    @Resource
    IMemberLevelService memberLevelService;

    @Resource
    ICoreGoodsLimitBuyService coreGoodsLimitBuyService;
    @Resource
    SiteMapper siteMapper;
    @Resource
    ICoreAddonService coreAddonService;
    @Resource
    ICoreSiteService coreSiteService;
    @Resource
    GoodsBrandMapper goodsBrandMapper;

    @Resource
    GoodsCategoryMapper goodsCategoryMapper;

    @Resource
    GoodsServiceMapper goodsServiceMapper;

    /**
     * 商品列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsListVo>
     */
    @Override
    public PageResult<MallGoodsListVo> page(PageParam pageParam, MallGoodsSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<MallGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("g")
                .innerJoin("?_mall_goods_sku gs ON g.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("g.goods_id,g.site_id,g.goods_name,g.goods_mall_category,g.goods_type,g.goods_cover,g.stock,g.label_ids,g.sale_num,g.status,g.sort,g.create_time,g.apply_time,g.supply_id");
        if (ObjectUtil.isEmpty(searchParam.getStatus()) ||
                Arrays.asList(GoodsStatusEnum.WAIT_AUDIT.getStatus(), GoodsStatusEnum.AUDIT_REFUSE.getStatus()).contains(searchParam.getStatus())) {
            queryWrapper.orderByDesc("g.apply_time", "g.create_time");
        } else {
            queryWrapper.orderByDesc("g.create_time");
        }
        if (ObjectUtil.isNotEmpty(searchParam.getStartPrice()) && ObjectUtil.isNotEmpty(searchParam.getEndPrice())) {
            Double[] prices = {searchParam.getStartPrice(), searchParam.getEndPrice()};
            Arrays.sort(prices); // 确保价格区间正确
            QueryMapperUtils.buildByBetween(queryWrapper, "gs.price", prices);
        } else if (ObjectUtil.isNotEmpty(searchParam.getStartPrice())) {
            queryWrapper.ge("gs.price", searchParam.getStartPrice());
        } else if (ObjectUtil.isNotEmpty(searchParam.getEndPrice())) {
            queryWrapper.le("gs.price", searchParam.getEndPrice());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getOrder())) {
            queryWrapper.orderBy(true, searchParam.getSort().equals("asc"), searchParam.getOrder());
        } else {
            if (ObjectUtil.isEmpty(searchParam.getStatus()) ||
                    Arrays.asList(GoodsStatusEnum.WAIT_AUDIT.getStatus(), GoodsStatusEnum.AUDIT_REFUSE.getStatus()).contains(searchParam.getStatus())) {
                queryWrapper.orderByDesc("g.apply_time", "g.create_time");
            } else {
                queryWrapper.orderByDesc("g.create_time");
            }
        }

        if (ObjectUtil.isNotEmpty(searchParam.getGoodsName()))
            queryWrapper.like("g.goods_name", searchParam.getGoodsName());
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsType()))
            queryWrapper.eq("g.goods_type", searchParam.getGoodsType());
        if (ObjectUtil.isNotEmpty(searchParam.getBrandId()))
            queryWrapper.eq("g.brand_id", searchParam.getBrandId());
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsCategory()))
            queryWrapper.like("g.goods_category", "\"" + searchParam.getGoodsCategory() + "\"");
        if (ObjectUtil.isNotEmpty(searchParam.getLabelIds()))
            queryWrapper.like("g.label_ids", "\"" + searchParam.getLabelIds() + "\"");
        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) queryWrapper.eq("g.status", searchParam.getStatus());
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsMallCategory()))
            queryWrapper.eq("g.goods_mall_category", searchParam.getGoodsMallCategory());
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsSource())){

        }
        QueryMapperUtils.buildByBetween(queryWrapper, "g.sale_num", new Integer[]{searchParam.getStartSaleNum(), searchParam.getEndSaleNum()});
        IPage<MallGoodsListVo> iPage = mallGoodsMapper.selectJoinPage(new Page<>(page, limit), MallGoodsListVo.class, queryWrapper);
        List<MallGoodsListVo> list = iPage.getRecords();

        for (MallGoodsListVo vo : list) {
            MallGoodsSku goodsSku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>()
                    .select("sku_id", "goods_id", "price", "member_price", "cost_price", "market_price",
                            "min_price", "max_price", "sale_price", "sku_name", "sku_image", "sku_no",
                            "sku_spec_format", "stock", "weight", "volume", "is_default", "is_sell",
                            "site_id", "supply_id", "supply_sku_id", "sale_num")
                    .eq("goods_id", vo.getGoodsId()).eq("is_default", 1));
            if (goodsSku != null) {
                MallGoodsListVo.GoodsSku goodsSkuVo = new MallGoodsListVo.GoodsSku();
                BeanUtils.copyProperties(goodsSku, goodsSkuVo);
                vo.setGoodsSku(goodsSkuVo);
            }

            // 处理站点信息
            Site site = siteMapper.selectById(vo.getSiteId());
            if (site != null) {
                MallGoodsListVo.Site siteVo = new MallGoodsListVo.Site();
                siteVo.setSiteId(site.getSiteId());
                siteVo.setSiteName(site.getSiteName());
                vo.setSite(siteVo);
            }

            // 处理商品封面缩略图
            if (ObjectUtil.isNotEmpty(vo.getGoodsCover())) {
                try {
                    String thumbUrl = coreUploadService.thumb(RequestUtils.siteId(), vo.getGoodsCover(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType());
                    vo.setGoodsCoverThumbSmall(thumbUrl);
                } catch (Exception e) {
                    // 如果缩略图生成失败，使用原图
                    vo.setGoodsCoverThumbSmall(vo.getGoodsCover());
                }
            }

            // 处理标签信息
            if (ObjectUtil.isNotEmpty(vo.getLabelIds())) {
                try {
                    // 将JSON字符串转换为List<String>
                    String labelIdsStr = vo.getLabelIds();
                    if (labelIdsStr.startsWith("[") && labelIdsStr.endsWith("]")) {
                        // 移除方括号并分割
                        labelIdsStr = labelIdsStr.substring(1, labelIdsStr.length() - 1);
                        String[] labelIdArray = labelIdsStr.split(",");
                        List<String> labelIdList = new ArrayList<>();
                        for (String labelId : labelIdArray) {
                            // 移除引号
                            String cleanLabelId = labelId.trim().replaceAll("\"", "");
                            labelIdList.add(cleanLabelId);
                        }

                        // 查询标签详细信息
                        if (!labelIdList.isEmpty()) {
                            List<Integer> labelIdInts = labelIdList.stream()
                                    .map(Integer::parseInt)
                                    .collect(Collectors.toList());
                            List<MallGoodsLabel> labels = mallGoodsLabelMapper.selectList(
                                    new QueryWrapper<MallGoodsLabel>().in("label_id", labelIdInts));
                            List<MallGoodsListVo.GoodsLabelArray> labelArray = new ArrayList<>();
                            for (MallGoodsLabel label : labels) {
                                MallGoodsListVo.GoodsLabelArray labelItem = new MallGoodsListVo.GoodsLabelArray();
                                labelItem.setLabelId(label.getLabelId());
                                labelItem.setLabelName(label.getLabelName());
                                labelArray.add(labelItem);
                            }
                            vo.setGoodsLabelArray(labelArray);
                        }
                    }
                } catch (Exception e) {
                    // 处理失败时保持原值
                }
            }
        }
        List<Integer> supplyIds = new ArrayList<>();
        for (MallGoodsListVo item : list) {
            if (item.getSupplyId() != null && item.getSupplyId() > 0) {
                supplyIds.add(item.getSupplyId());
            }
        }

        Map<Integer, String> siteNameMap = new HashMap<>();
        if (!supplyIds.isEmpty()) {
            List<Site> supplySites = siteMapper.selectList(new QueryWrapper<Site>().in("site_id", supplyIds));
            for (Site site : supplySites) {
                siteNameMap.put(site.getSiteId(), site.getSiteName());
            }
        }

        for (MallGoodsListVo item : list) {
            if (item.getSupplyId() != null && item.getSupplyId() > 0) {
                item.setSupplyName(siteNameMap.getOrDefault(item.getSupplyId(), ""));
            }
        }




        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 商品详情
     *
     * @param id 主键
     * @return MallGoodsInfoVo
     */
    /**
     * 商品详情
     *
     * @param id 主键
     * @return MallGoodsInfoVo
     */
    @Override
    public MallGoodsInfoVo info(Integer id) {
        QueryWrapper<MallGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("goods_id", "site_id", "goods_name", "sub_title", "goods_type",
                        "goods_cover", "goods_image", "goods_desc", "brand_id", "goods_category", "goods_mall_category",
                        "label_ids", "service_ids", "unit", "stock", "sale_num",  "status",
                        "audit_status", "sort", "delivery_type", "is_free_shipping", "fee_type", "delivery_money",
                        "delivery_template_id", "supplier_id", "create_time", "update_time", "is_supply_update")
                .eq("goods_id", id);

        MallGoods model = mallGoodsMapper.selectOne(queryWrapper);
        if (model == null) return null;

        MallGoodsInfoVo vo = new MallGoodsInfoVo();
        BeanUtils.copyProperties(model, vo);

        // 处理labelIds - 对应PHP的label_ids处理
        if (ObjectUtil.isNotEmpty(model.getLabelIds())) {
            try {
                List<String> labelIdList = JSONUtil.toList(model.getLabelIds(), String.class);
                List<Integer> labelIdInts = labelIdList.stream()
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());

                QueryWrapper<MallGoodsLabel> enableLabelQueryWrapper = new QueryWrapper<>();
                enableLabelQueryWrapper.select("label_id")
                        .in("label_id", labelIdInts)
                        .eq("status", LabelStatusEnum.ENABLE.getValue());

                List<MallGoodsLabel> enableLabels = mallGoodsLabelMapper.selectList(enableLabelQueryWrapper);
                List<Integer> enableLabelIds = enableLabels.stream()
                        .map(MallGoodsLabel::getLabelId)
                        .collect(Collectors.toList());

                List<String> filteredLabelIds = labelIdList.stream()
                        .map(Integer::parseInt)
                        .filter(enableLabelIds::contains)
                        .map(String::valueOf)
                        .collect(Collectors.toList());

                vo.setLabelIds(filteredLabelIds);
            } catch (Exception e) {
                vo.setLabelIds(new ArrayList<>());
            }
        } else {
            vo.setLabelIds(new ArrayList<>());
        }

        if (model.getBrandId() != null && model.getBrandId() > 0) {
            try {
                QueryWrapper<GoodsBrand> brandQueryWrapper = new QueryWrapper<>();
                brandQueryWrapper.select("brand_id", "brand_name", "logo")
                        .eq("brand_id", model.getBrandId());
                GoodsBrand brand = goodsBrandMapper.selectOne(brandQueryWrapper);
                
                if (brand != null) {
                    MallGoodsInfoVo.BrandInfo brandInfo = new MallGoodsInfoVo.BrandInfo();
                    brandInfo.setBrandId(brand.getBrandId());
                    brandInfo.setBrandName(brand.getBrandName());
                    vo.setGoodsBrand(brandInfo);
                } else {
                    // 提供默认品牌对象，避免前端报错
                    MallGoodsInfoVo.BrandInfo brandInfo = new MallGoodsInfoVo.BrandInfo();
                    brandInfo.setBrandId(model.getBrandId());
                    brandInfo.setBrandName("");
                    vo.setGoodsBrand(brandInfo);
                }
            } catch (Exception e) {
                // 异常情况下也提供默认品牌对象
                MallGoodsInfoVo.BrandInfo brandInfo = new MallGoodsInfoVo.BrandInfo();
                brandInfo.setBrandId(model.getBrandId());
                brandInfo.setBrandName("");
                vo.setGoodsBrand(brandInfo);
            }
        } else {
            // 没有品牌ID时，也提供默认品牌对象，避免前端报错
            MallGoodsInfoVo.BrandInfo brandInfo = new MallGoodsInfoVo.BrandInfo();
            brandInfo.setBrandId(0);
            brandInfo.setBrandName("");
            vo.setGoodsBrand(brandInfo);
        }

        try {
            if (ObjectUtil.isNotEmpty(model.getGoodsCategory())) {
                List<Integer> categoryIds = JSONUtil.toList(model.getGoodsCategory(), Integer.class);
                
                QueryWrapper<GoodsCategory> categoryQueryWrapper = new QueryWrapper<>();
                categoryQueryWrapper.select("category_id", "category_name")
                        .in("category_id", categoryIds)
                        .orderByDesc("category_id");
                List<GoodsCategory> categoryList = goodsCategoryMapper.selectList(categoryQueryWrapper);
                
                // 转换为VO格式
                List<MallGoodsInfoVo.CategoryName> categoryVoList = new ArrayList<>();
                for (GoodsCategory category : categoryList) {
                    MallGoodsInfoVo.CategoryName categoryVo = new MallGoodsInfoVo.CategoryName();
                    categoryVo.setCategoryId(category.getCategoryId());
                    categoryVo.setCategoryName(category.getCategoryName());
                    categoryVoList.add(categoryVo);
                }
                vo.setGoodsCategoryName(categoryVoList);
            } else {
                vo.setGoodsCategoryName(new ArrayList<>());
            }
        } catch (Exception e) {
            vo.setGoodsCategoryName(new ArrayList<>());
        }

        try {
            if (ObjectUtil.isNotEmpty(model.getServiceIds())) {
                List<String> serviceIdList = JSONUtil.toList(model.getServiceIds(), String.class);
                List<Integer> serviceIdInts = serviceIdList.stream()
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
                
                QueryWrapper<GoodsService> serviceQueryWrapper = new QueryWrapper<>();
                serviceQueryWrapper.select("service_id", "service_name")
                        .in("service_id", serviceIdInts)
                        .orderByDesc("service_id");
                List<GoodsService> serviceList = goodsServiceMapper.selectList(serviceQueryWrapper);
                
                // 转换为VO格式
                List<MallGoodsInfoVo.ServiceName> serviceVoList = new ArrayList<>();
                for (GoodsService service : serviceList) {
                    MallGoodsInfoVo.ServiceName serviceVo = new MallGoodsInfoVo.ServiceName();
                    serviceVo.setServiceId(service.getServiceId());
                    serviceVo.setServiceName(service.getServiceName());
                    serviceVoList.add(serviceVo);
                }
                vo.setGoodsServiceName(serviceVoList);
            } else {
                // 对应PHP: 没有else分支，返回null
                vo.setGoodsServiceName(new ArrayList<>());
            }
        } catch (Exception e) {
            vo.setGoodsServiceName(new ArrayList<>());
        }

        try {
            if (ObjectUtil.isNotEmpty(model.getLabelIds())) {
                List<String> labelIdList = JSONUtil.toList(model.getLabelIds(), String.class);
                List<Integer> labelIdInts = labelIdList.stream()
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());

                QueryWrapper<MallGoodsLabel> labelQueryWrapper = new QueryWrapper<>();
                labelQueryWrapper.select("label_id", "label_name", "style_type", "color_json", "icon")
                        .in("label_id", labelIdInts)
                        .eq("status", LabelStatusEnum.ENABLE.getValue())
                        .orderByDesc("sort", "label_id");
                List<MallGoodsLabel> labelList = mallGoodsLabelMapper.selectList(labelQueryWrapper);

                // 转换为VO格式
                List<MallGoodsInfoVo.LabelName> labelVoList = new ArrayList<>();
                for (MallGoodsLabel label : labelList) {
                    MallGoodsInfoVo.LabelName labelVo = new MallGoodsInfoVo.LabelName();
                    labelVo.setLabelId(label.getLabelId());
                    labelVo.setLabelName(label.getLabelName());
                    labelVo.setStyleType(label.getStyleType());
                    labelVo.setIcon(label.getIcon());
                    
                    // 处理color_json
                    if (ObjectUtil.isNotEmpty(label.getColorJson())) {
                        try {
                            MallGoodsInfoVo.ColorJson colorJson = JSONUtil.toBean(label.getColorJson(), MallGoodsInfoVo.ColorJson.class);
                            labelVo.setColorJson(colorJson);
                        } catch (Exception e) {
                            MallGoodsInfoVo.ColorJson colorJson = new MallGoodsInfoVo.ColorJson();
                            labelVo.setColorJson(colorJson);
                        }
                    } else {
                        labelVo.setColorJson(new MallGoodsInfoVo.ColorJson());
                    }
                    
                    labelVoList.add(labelVo);
                }
                vo.setGoodsLabelName(labelVoList);
            } else {
                vo.setGoodsLabelName(new ArrayList<>());
            }
        } catch (Exception e) {
            vo.setGoodsLabelName(new ArrayList<>());
        }

        // 获取商品SKU数据
        try {
            QueryWrapper<MallGoodsSku> skuQueryWrapper = new QueryWrapper<>();
            skuQueryWrapper.select("sku_id", "sku_name", "sku_image", "sku_no", "goods_id", 
                    "sku_spec_format", "price", "market_price", "cost_price", "min_price", 
                    "max_price", "stock", "weight", "volume", "is_default", "is_sell")
                    .eq("goods_id", id)
                    .orderByAsc("sku_id");
            List<MallGoodsSku> skuList = mallGoodsSkuMapper.selectList(skuQueryWrapper);
            
            // 转换为VO格式 - 处理数据类型转换
            List<MallGoodsInfoVo.SkuList> skuVoList = new ArrayList<>();
            for (MallGoodsSku sku : skuList) {
                MallGoodsInfoVo.SkuList skuVo = new MallGoodsInfoVo.SkuList();
                
                // 手动设置字段，处理数据类型转换
                skuVo.setSkuId(sku.getSkuId() != null ? sku.getSkuId().toString() : "");
                skuVo.setSkuName(sku.getSkuName());
                skuVo.setSkuImage(sku.getSkuImage());
                skuVo.setSkuNo(sku.getSkuNo());
                skuVo.setGoodsId(sku.getGoodsId());
                skuVo.setSkuSpecFormat(sku.getSkuSpecFormat());
                skuVo.setPrice(sku.getPrice() != null ? sku.getPrice().toString() : "0");
                skuVo.setMarketPrice(sku.getMarketPrice() != null ? sku.getMarketPrice().toString() : "0");
                skuVo.setCostPrice(sku.getCostPrice() != null ? sku.getCostPrice().toString() : "0");
                skuVo.setMinPrice(sku.getMinPrice() != null ? sku.getMinPrice().toString() : "0");
                skuVo.setMaxPrice(sku.getMaxPrice() != null ? sku.getMaxPrice().toString() : "0");
                skuVo.setStock(sku.getStock());
                skuVo.setWeight(sku.getWeight() != null ? sku.getWeight().toString() : "0");
                skuVo.setVolume(sku.getVolume() != null ? sku.getVolume().toString() : "0");
                skuVo.setIsDefault(sku.getIsDefault());
                skuVo.setIsSell(sku.getIsSell());
                
                skuVoList.add(skuVo);
            }
            vo.setSkuList(skuVoList);

            // 处理规格类型判断
            List<String> tempSkuData = skuList.stream()
                    .map(MallGoodsSku::getSkuSpecFormat)
                    .filter(ObjectUtil::isNotEmpty)
                    .collect(Collectors.toList());
            
            vo.setSpecType("single");
            if (!tempSkuData.isEmpty()) {
                // 多规格
                vo.setSpecType("multi");
                
                // 获取商品规格数据
                QueryWrapper<MallGoodsSpec> specQueryWrapper = new QueryWrapper<>();
                specQueryWrapper.select("spec_id", "goods_id", "spec_name", "spec_values")
                        .eq("goods_id", id)
                        .orderByAsc("spec_id");
                List<MallGoodsSpec> specList = mallGoodsSpecMapper.selectList(specQueryWrapper);
                
                // 转换为VO格式
                List<MallGoodsInfoVo.SpecList> specVoList = new ArrayList<>();
                for (MallGoodsSpec spec : specList) {
                    MallGoodsInfoVo.SpecList specVo = new MallGoodsInfoVo.SpecList();
                    BeanUtils.copyProperties(spec, specVo);
                    specVoList.add(specVo);
                }
                vo.setSpecList(specVoList);
            } else {
                vo.setSpecList(new ArrayList<>());
            }
        } catch (Exception e) {
            // 如果SKU数据获取失败，设置空列表
            vo.setSkuList(new ArrayList<>());
            vo.setSpecList(new ArrayList<>());
            vo.setSpecType("single");
        }
        try {
            if (model.getGoodsMallCategory() != null && model.getGoodsMallCategory() > 0) {
                QueryWrapper<GoodsCategory> mallCategoryQueryWrapper = new QueryWrapper<>();
                mallCategoryQueryWrapper.select("category_name")
                        .eq("category_id", model.getGoodsMallCategory());
                GoodsCategory mallCategory = goodsCategoryMapper.selectOne(mallCategoryQueryWrapper);
                
                if (mallCategory != null) {
                    vo.setGoodsMallCategoryName(mallCategory.getCategoryName());
                } else {
                    vo.setGoodsMallCategoryName("");
                }
            } else {
                vo.setGoodsMallCategoryName("");
            }
        } catch (Exception e) {
            vo.setGoodsMallCategoryName("");
        }

        return vo;
    }



    /**
     * 商品添加
     *
     * @param addParam 参数
     */
    @Override
    @Transactional
    public void add(MallGoodsParam addParam) {
        Integer siteId = RequestUtils.siteId();

        // 添加goods表
        MallGoods model = new MallGoods();
        BeanUtils.copyProperties(addParam, model);
        model.setSiteId(siteId);
        model.setGoodsCover(addParam.getGoodsImage().split(",")[0]);
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setGoodsCategory(JSONUtil.parseArray(intArrToStrArr(addParam.getGoodsCategory())).toString());
        model.setLabelIds(JSONUtil.parseArray(intArrToStrArr(addParam.getLabelIds())).toString());
        model.setServiceIds(JSONUtil.parseArray(intArrToStrArr(addParam.getServiceIds())).toString());
        model.setDeliveryType(JSONUtil.parseArray(addParam.getDeliveryType()).toString());

        mallGoodsMapper.insert(model);

        // 规格数据
        List<MallGoodsSku> skuData = new LinkedList<>();
        if (addParam.getSpecType().equals("single")) {
            MallGoodsSku sku = new MallGoodsSku();
            BeanUtils.copyProperties(addParam, sku);
            sku.setSiteId(siteId);
            sku.setGoodsId(model.getGoodsId());
            sku.setSkuImage(model.getGoodsCover());
            sku.setSalePrice(addParam.getPrice());
            sku.setSkuSpecFormat("");
            sku.setIsDefault(1);
            skuData.add(sku);
        } else {
            if (addParam.getGoodsSkuData().keySet().size() == 0) throw new CommonException("请添加商品规格");

            Integer defaultCount = 0;
            for (String key : addParam.getGoodsSkuData().keySet()) {
                MallGoodsParam.SkuData skuParam = addParam.getGoodsSkuData().get(key);

                List<String> skuSpecFormat = new LinkedList<>();
                for (int i = 0; i < skuParam.getSkuSpec().size(); i++) {
                    JSONObject spec = skuParam.getSkuSpec().getJSONObject(i);
                    skuSpecFormat.add(spec.getStr("spec_value_name", ""));
                }

                MallGoodsSku sku = new MallGoodsSku();
                BeanUtils.copyProperties(skuParam, sku);
                sku.setSkuName(skuParam.getSpecName());
                sku.setSiteId(siteId);
                sku.setSkuImage(skuParam.getSkuImage().isEmpty() ? model.getGoodsCover() : skuParam.getSkuImage());
                sku.setSalePrice(skuParam.getPrice());
                sku.setGoodsId(model.getGoodsId());
                sku.setSkuSpecFormat(String.join(",", skuSpecFormat));
                if (skuParam.getIsDefault().equals(1)) {
                    defaultCount++;
                    if (defaultCount > 1) throw new CommonException("商品不能有多个默认规格");
                }
                skuData.add(sku);
            }
            if (defaultCount.equals(0)) throw new CommonException("商品缺少默认规格");

            // 添加商品规格值
            List<MallGoodsSpec> specData = new LinkedList<>();
            for (int i = 0; i < addParam.getGoodsSpecFormat().size(); i++) {
                JSONObject goodsSpec = addParam.getGoodsSpecFormat().getJSONObject(i);

                MallGoodsSpec spec = new MallGoodsSpec();
                spec.setGoodsId(model.getGoodsId());
                spec.setSiteId(siteId);
                spec.setSpecName(goodsSpec.getStr("spec_name", ""));

                List<String> specValues = new LinkedList<>();
                for (int v = 0; v < goodsSpec.getJSONArray("values").size(); v++) {
                    JSONObject value = goodsSpec.getJSONArray("values").getJSONObject(v);
                    specValues.add(value.getStr("spec_value_name", ""));
                }

                spec.setSpecValues(String.join(",", specValues));
                specData.add(spec);
            }
            shopGoodsSpecService.insertAll(specData);
        }

        shopGoodsSkuService.insertAll(skuData);

        // 商品添加之后事件
        AfterGoodsEditEvent event = new AfterGoodsEditEvent();
        event.setSiteId(siteId);
        event.addAppSign("mall");
        event.setName("AfterGoodsEditEvent");
        event.setGoodsId(model.getGoodsId());
        event.setGoodsData(model);
        event.setSkuData(skuData);
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    /**
     * int 数组转 str 数组
     *
     * @param arr
     * @return
     */
    private String[] intArrToStrArr(Integer[] arr) {
        String[] strArr = new String[arr.length];

        for (int i = 0; i < arr.length; i++) {
            strArr[i] = String.valueOf(arr[i]);
        }
        return strArr;
    }

    /**
     * int 数组转 str 数组
     *
     * @param arr
     * @return
     */
    private Integer[] strArrToIntArr(String[] arr) {
        Integer[] intArr = new Integer[arr.length];

        for (int i = 0; i < arr.length; i++) {
            intArr[i] = Integer.valueOf(arr[i]);
        }
        return intArr;
    }

    /**
     * 商品编辑
     *
     * @param editParam 参数
     */
    @Override
    @Transactional
    public void edit(Integer id, MallGoodsParam editParam) {
        Integer siteId = RequestUtils.siteId();
        // 校验商品规格价格
        editCheckGoodsSkuPrice(id,editParam);

        MallGoods model = mallGoodsMapper.selectOne(new QueryWrapper<MallGoods>().eq("goods_id", id).eq("site_id", siteId).eq("delete_time", 0));
        Assert.notNull(model, "商品不存在");

        Long activeGoodsCount = getActiveGoodsCount(id);
        // 如果有正在进行的活动不允许下架
        if (editParam.getStatus().equals(0) && activeGoodsCount > 0)
            throw new CommonException("商品正在参与营销活动，禁止修改");

        BeanUtils.copyProperties(editParam, model);
        model.setGoodsCover(editParam.getGoodsImage().split(",")[0]);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        model.setGoodsCategory(JSONUtil.parseArray(intArrToStrArr(editParam.getGoodsCategory())).toString());
        model.setLabelIds(JSONUtil.parseArray(intArrToStrArr(editParam.getLabelIds())).toString());
        model.setServiceIds(JSONUtil.parseArray(intArrToStrArr(editParam.getServiceIds())).toString());
        model.setDeliveryType(JSONUtil.parseArray(editParam.getDeliveryType()).toString());
        mallGoodsMapper.updateById(model);

        // 规格数据
        List<MallGoodsSku> skuData = new LinkedList<>();
        if (editParam.getSpecType().equals("single")) {
            List<MallGoodsSku> skuList = mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().eq("goods_id", id));
            if (skuList.size() > 1) {
                // 删除原来的多规格
                mallGoodsSkuMapper.delete(new QueryWrapper<MallGoodsSku>().eq("goods_id", id));

                MallGoodsSku sku = new MallGoodsSku();
                BeanUtils.copyProperties(editParam, sku);
                sku.setSiteId(siteId);
                sku.setGoodsId(model.getGoodsId());
                sku.setSkuImage(model.getGoodsCover());
                sku.setSalePrice(editParam.getPrice());
                sku.setIsDefault(1);
                sku.setSkuSpecFormat("");
                skuData.add(sku);
            } else {
                MallGoodsSku sku = skuList.get(0);
                sku.setSkuImage(model.getGoodsCover());
                sku.setSkuNo(editParam.getSkuNo());
                sku.setSkuName("");
                sku.setSkuSpecFormat("");
                sku.setWeight(editParam.getWeight());
                sku.setVolume(editParam.getVolume());
                sku.setCostPrice(editParam.getCostPrice());
                sku.setMarketPrice(editParam.getMarketPrice());
                sku.setIsDefault(1);
                if (activeGoodsCount == 0) {
                    sku.setPrice(editParam.getPrice());
                    sku.setSalePrice(editParam.getPrice());
                    sku.setStock(editParam.getStock());
                }
                mallGoodsSkuMapper.updateById(sku);
            }
            // 删除原规格值
            mallGoodsSpecMapper.delete(new QueryWrapper<MallGoodsSpec>().eq("goods_id", id));
        } else {
            if (editParam.getGoodsSkuData().keySet().size() == 0) throw new CommonException("请添加商品规格");

            MallGoodsParam.SkuData firstSkuData = editParam.getGoodsSkuData().get(editParam.getGoodsSkuData().keySet().iterator().next());
            if (activeGoodsCount > 0 && firstSkuData.getSkuId() == null) {
                throw new CommonException("商品正在参与营销活动，禁止修改");
            }

            // 检测规格项是否发生变化
            if (firstSkuData.getSkuId() != null) {
                List<Integer> skuIdArr = new LinkedList<>();

                Integer defaultCount = 0;
                for (String key : editParam.getGoodsSkuData().keySet()) {
                    MallGoodsParam.SkuData skuParam = editParam.getGoodsSkuData().get(key);

                    List<String> skuSpecFormat = new LinkedList<>();
                    for (int i = 0; i < skuParam.getSkuSpec().size(); i++) {
                        JSONObject spec = skuParam.getSkuSpec().getJSONObject(i);
                        skuSpecFormat.add(spec.getStr("spec_value_name", ""));
                    }

                    MallGoodsSku sku = new MallGoodsSku();
                    if (skuParam.getSkuId() != null) {
                        sku.setSkuId(skuParam.getSkuId());
                        skuIdArr.add(skuParam.getSkuId());
                    }
                    sku.setSiteId(siteId);
                    sku.setGoodsId(id);
                    sku.setSkuNo(skuParam.getSkuNo());
                    sku.setSkuName(skuParam.getSpecName());
                    sku.setSkuImage(skuParam.getSkuImage().isEmpty() ? model.getGoodsCover() : skuParam.getSkuImage());
                    sku.setSkuSpecFormat(String.join(",", skuSpecFormat));
                    sku.setWeight(skuParam.getWeight());
                    sku.setVolume(skuParam.getVolume());
                    sku.setCostPrice(skuParam.getCostPrice());
                    sku.setMarketPrice(skuParam.getMarketPrice());
                    sku.setIsDefault(skuParam.getIsDefault());
                    if (activeGoodsCount == 0) {
                        sku.setPrice(skuParam.getPrice());
                        sku.setSalePrice(skuParam.getPrice());
                        sku.setStock(skuParam.getStock());
                    }

                    if (skuParam.getIsDefault().equals(1)) {
                        defaultCount++;
                        if (defaultCount > 1) throw new CommonException("商品不能有多个默认规格");
                    }
                    skuData.add(sku);
                }
                if (defaultCount.equals(0)) throw new CommonException("商品缺少默认规格");

                // 查询移除的规格
                Integer[] removeSkuIdArr = mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().eq("goods_id", id)
                        .notIn("sku_id", skuIdArr).select("sku_id")
                ).stream().map(i -> i.getSkuId()).toArray(Integer[]::new);

                if (removeSkuIdArr.length > 0) {
                    if (getSkuTransactionCount(removeSkuIdArr) > 0)
                        throw new CommonException("存在未完成的订单，不允许删除规格");
                    mallGoodsSkuMapper.delete(new QueryWrapper<MallGoodsSku>().in("sku_id", Arrays.asList(removeSkuIdArr)));
                }

                // 添加商品规格值
                List<MallGoodsSpec> specData = new LinkedList<>();
                List<Integer> specIdArr = new LinkedList<>();
                for (int i = 0; i < editParam.getGoodsSpecFormat().size(); i++) {
                    JSONObject goodsSpec = editParam.getGoodsSpecFormat().getJSONObject(i);

                    MallGoodsSpec spec = new MallGoodsSpec();
                    if (goodsSpec.getInt("spec_id") != null) {
                        spec.setSpecId(goodsSpec.getInt("spec_id"));
                        specIdArr.add(goodsSpec.getInt("spec_id"));
                    }
                    spec.setGoodsId(model.getGoodsId());
                    spec.setSiteId(siteId);
                    spec.setSpecName(goodsSpec.getStr("spec_name", ""));

                    List<String> specValues = new LinkedList<>();
                    for (int v = 0; v < goodsSpec.getJSONArray("values").size(); v++) {
                        JSONObject value = goodsSpec.getJSONArray("values").getJSONObject(v);
                        specValues.add(value.getStr("spec_value_name", ""));
                    }

                    spec.setSpecValues(String.join(",", specValues));
                    specData.add(spec);
                }
                // 删除移除的规格值
                if (specIdArr.size() > 0)
                    mallGoodsSpecMapper.delete(new QueryWrapper<MallGoodsSpec>().eq("goods_id", id).notIn("spec_id", specIdArr));

                shopGoodsSpecService.insertAll(specData);
            } else {
                if (getGoodsTransactionCount(id) > 0) throw new CommonException("存在未完成的订单，无法编辑商品");

                // 删除原来的规格
                mallGoodsSkuMapper.delete(new QueryWrapper<MallGoodsSku>().eq("goods_id", id));
                // 删除原规格值
                mallGoodsSpecMapper.delete(new QueryWrapper<MallGoodsSpec>().eq("goods_id", id));

                Integer defaultCount = 0;
                for (String key : editParam.getGoodsSkuData().keySet()) {
                    MallGoodsParam.SkuData skuParam = editParam.getGoodsSkuData().get(key);

                    List<String> skuSpecFormat = new LinkedList<>();
                    for (int i = 0; i < skuParam.getSkuSpec().size(); i++) {
                        JSONObject spec = skuParam.getSkuSpec().getJSONObject(i);
                        skuSpecFormat.add(spec.getStr("spec_value_name", ""));
                    }

                    MallGoodsSku sku = new MallGoodsSku();
                    BeanUtils.copyProperties(skuParam, sku);
                    sku.setSiteId(siteId);
                    sku.setSkuImage(skuParam.getSkuImage().isEmpty() ? model.getGoodsCover() : skuParam.getSkuImage());
                    sku.setSalePrice(skuParam.getPrice());
                    sku.setGoodsId(model.getGoodsId());
                    sku.setSkuSpecFormat(String.join(",", skuSpecFormat));
                    if (skuParam.getIsDefault().equals(1)) {
                        defaultCount++;
                        if (defaultCount > 1) throw new CommonException("商品不能有多个默认规格");
                    }
                    skuData.add(sku);
                }
                if (defaultCount.equals(0)) throw new CommonException("商品缺少默认规格");

                // 添加商品规格值
                List<MallGoodsSpec> specData = new LinkedList<>();
                for (int i = 0; i < editParam.getGoodsSpecFormat().size(); i++) {
                    JSONObject goodsSpec = editParam.getGoodsSpecFormat().getJSONObject(i);

                    MallGoodsSpec spec = new MallGoodsSpec();
                    spec.setGoodsId(model.getGoodsId());
                    spec.setSiteId(siteId);
                    spec.setSpecName(goodsSpec.getStr("spec_name", ""));

                    List<String> specValues = new LinkedList<>();
                    for (int v = 0; v < goodsSpec.getJSONArray("values").size(); v++) {
                        JSONObject value = goodsSpec.getJSONArray("values").getJSONObject(v);
                        specValues.add(value.getStr("spec_value_name", ""));
                    }

                    spec.setSpecValues(String.join(",", specValues));
                    specData.add(spec);
                }
                shopGoodsSpecService.insertAll(specData);
            }
        }

        if (skuData.size() > 0) shopGoodsSkuService.insertAll(skuData);

        // 商品添加之后事件
        AfterGoodsEditEvent event = new AfterGoodsEditEvent();
        event.setSiteId(siteId);
        event.addAppSign("mall");
        event.setName("AfterGoodsEditEvent");
        event.setGoodsId(model.getGoodsId());
        event.setGoodsData(model);
        event.setSkuData(skuData);
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    /**
     * 编辑商品校验商品规格价格和状态
     * @param goodsId 商品ID
     * @param editParam 编辑参数
     * @return 商品ID
     */
    public Integer editCheckGoodsSkuPrice(Integer goodsId,MallGoodsParam editParam) {
        int supplyGoodsId = 0;

        if (checkAddon("supply")) {
            // 查询供应商品ID
            MallGoods model = mallGoodsMapper.selectOne(new QueryWrapper<MallGoods>().eq("goods_id", goodsId).eq("site_id", RequestUtils.siteId()));
            if(model != null){
                supplyGoodsId = model.getSupplyGoodsId();
            }
            // 查询商品SKU数据
            List<MallGoodsSku> skuData = mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().eq("goods_id", goodsId));
            // 移除不存在的SKU并设置价格
            Map<Integer,BigDecimal> filteredSkuData = new HashMap<>();
            if (supplyGoodsId != 0 && !skuData.isEmpty()) {
                if ("single".equals(editParam.getSpecType())) {
                    // 单规格处理
                    skuData.forEach(sku -> {
                        filteredSkuData.put(sku.getSupplySkuId(), editParam.getPrice());
                    });
                } else if ("multi".equals(editParam.getSpecType())) {
                    // 多规格处理
                    if (!editParam.getGoodsSkuData().keySet().isEmpty()){
                        Map<Integer, BigDecimal> priceMap = new HashMap<>();
                        for (String key : editParam.getGoodsSkuData().keySet()) {
                            MallGoodsParam.SkuData skuParam = editParam.getGoodsSkuData().get(key);
                            if (ObjectUtil.isNotEmpty(skuParam.getSkuId())){
                                priceMap.put(skuParam.getSkuId(), skuParam.getPrice());
                            }
                        }
                        if(!priceMap.isEmpty()){
                            for (MallGoodsSku sku : skuData) {
                                if (priceMap.get(sku.getSkuId()) != null){
                                    filteredSkuData.put(sku.getSupplySkuId(), priceMap.get(sku.getSkuId()));
                                }
                            }
                        }
                    }
                }
                // 触发事件
                CheckGoodsSkuPriceEvent event = new CheckGoodsSkuPriceEvent();
                event.setGoodsId(supplyGoodsId);
                event.setStatus(editParam.getStatus());
                event.setSkuData(filteredSkuData);
                EventAndSubscribeOfPublisher.publishAll(event);
            }
        }
        return supplyGoodsId;
    }

    /**
     * 检查插件是否安装
     * @param key
     */
    private Boolean checkAddon(String key) {
        Map<String, InstallAddonListVo> addonListVoMap= coreAddonService.getInstallAddonList();
        return addonListVoMap.get(key) != null;
    }

    /**
     * 商品删除
     *
     * @param param
     */
    @Override
    public void del(GetGoodsIdsParam param) {
        Integer[] goodsIds = null;
        if (param.getGoodsIds() instanceof Integer) {
            goodsIds = new Integer[]{(Integer) param.getGoodsIds()};
        } else if (param.getGoodsIds() instanceof ArrayList) {
            goodsIds = ((ArrayList<?>) param.getGoodsIds()).toArray(new Integer[0]);
        }
        if (getActiveGoodsCount(goodsIds) > 0) throw new CommonException("商品正在参与营销活动，禁止删除");
        if (getSupplyGoodsCount(goodsIds) > 0) throw new CommonException("供货商商品不允许删除");
        MallGoods model = new MallGoods();
        model.setStatus(0);
        model.setDeleteTime(System.currentTimeMillis() / 1000);
        mallGoodsMapper.update(model, new QueryWrapper<MallGoods>().eq("site_id", RequestUtils.siteId()).in("goods_id", goodsIds));
    }

    /**
     * 恢复商品
     *
     * @param param
     */
    @Override
    public void recycle(GetGoodsIdsParam param) {
        Integer[] goodsIds = null;
        if (param.getGoodsIds() instanceof Integer) {
            goodsIds = new Integer[]{(Integer) param.getGoodsIds()};
        } else if (param.getGoodsIds() instanceof ArrayList) {
            goodsIds = ((ArrayList<?>) param.getGoodsIds()).toArray(new Integer[0]);
        }

        MallGoods model = new MallGoods();
        model.setDeleteTime(Long.valueOf(0));
        mallGoodsMapper.update(model, new QueryWrapper<MallGoods>().eq("site_id", RequestUtils.siteId()).in("goods_id", goodsIds));
    }

    /**
     * 商品回收站
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsListVo>
     */
    @Override
    public PageResult<MallGoodsListVo> recyclePage(PageParam pageParam, MallGoodsSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<MallGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("goods_id,site_id,goods_name,goods_type,goods_cover,stock,sale_num,status,sort,create_time,member_discount");
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.ne("delete_time", 0);

        if (ObjectUtil.isNotEmpty(searchParam.getGoodsName()))
            queryWrapper.like("goods_name", searchParam.getGoodsName());
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsType()))
            queryWrapper.eq("goods_type", searchParam.getGoodsType());
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsCategory()))
            queryWrapper.like("goods_category", "\"" + searchParam.getGoodsCategory() + "\"");
        queryWrapper.orderByDesc("goods_id");

        IPage<MallGoods> iPage = mallGoodsMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<MallGoodsListVo> list = new LinkedList<>();
        for (MallGoods item : iPage.getRecords()) {
            MallGoodsListVo vo = new MallGoodsListVo();
            BeanUtils.copyProperties(item, vo);

            MallGoodsSku goodsSku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>()
                    .select("sku_id", "goods_id", "price", "member_price")
                    .eq("goods_id", item.getGoodsId()).eq("is_default", 1));
            MallGoodsListVo.GoodsSku goodsSkuVo = new MallGoodsListVo.GoodsSku();
            BeanUtils.copyProperties(goodsSku, goodsSkuVo);
            vo.setGoodsSku(goodsSkuVo);
            vo.setGoodsCoverThumbSmall(coreUploadService.thumb(RequestUtils.siteId(), item.getGoodsCover(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType()));

            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 修改商品上下架状态
     *
     * @param param
     */
    @Override
    public void editStatus(EditGoodsStatusParam param) {
        Integer[] goodsIds = null;
        if (param.getGoodsIds() instanceof Integer) {
            goodsIds = new Integer[]{(Integer) param.getGoodsIds()};
        } else if (param.getGoodsIds() instanceof ArrayList) {
            goodsIds = ((ArrayList<?>) param.getGoodsIds()).toArray(new Integer[0]);
        }

        MallGoods model = new MallGoods();
        model.setStatus(param.getStatus());
        mallGoodsMapper.update(model, new QueryWrapper<MallGoods>().eq("site_id", RequestUtils.siteId()).in("goods_id", goodsIds));

        AfterMallGoodsStatusEditEvent event = new AfterMallGoodsStatusEditEvent();
        event.setSiteId(RequestUtils.siteId());
        event.setGoodsIds(Arrays.asList(goodsIds));
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    /**
     * 修改商品排序
     *
     * @param param
     */
    @Override
    public void editSort(EditGoodsSortParam param) {
        MallGoods model = new MallGoods();
        model.setSort(param.getSort());
        mallGoodsMapper.update(model, new QueryWrapper<MallGoods>().eq("goods_id", param.getGoodsId()));
    }

    /**
     * 获取商品添加/编辑数据
     *
     * @param goodsId
     * @return
     */
    @Override
    public MallGoodsInitVo getInit(Integer goodsId) {
        MallGoodsInitVo vo = new MallGoodsInitVo();
        if (goodsId > 0) {
            MallGoods model = mallGoodsMapper.selectOne(
                    new QueryWrapper<MallGoods>()
                            .eq("goods_id", goodsId)
                            .eq("site_id", RequestUtils.siteId())
                            .eq("delete_time", 0));
            if (model == null) return null;

            MallGoodsInitVo.GoodsInfo goodsInfo = new MallGoodsInitVo.GoodsInfo();
            BeanUtils.copyProperties(model, goodsInfo);
            goodsInfo.setGoodsCategory(strArrToIntArr(Convert.toStrArray(JSONUtil.parseArray(model.getGoodsCategory()))));
            goodsInfo.setLabelIds(strArrToIntArr(Convert.toStrArray(JSONUtil.parseArray(model.getLabelIds()))));
            goodsInfo.setServiceIds(strArrToIntArr(Convert.toStrArray(JSONUtil.parseArray(model.getServiceIds()))));
            goodsInfo.setStatus(String.valueOf(model.getStatus()));

            List<MallGoodsInitVo.SkuInfo> skuList = CollectUtils.convert(mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().eq("goods_id", model.getGoodsId())), MallGoodsInitVo.SkuInfo.class);
            goodsInfo.setSkuList(skuList);

            List<MallGoodsInitVo.SkuInfo> tempSkuList = skuList.stream().filter(i -> i.getSkuSpecFormat() != null && !i.getSkuSpecFormat().isEmpty()).collect(Collectors.toList());
            if (tempSkuList.size() > 0) {
                goodsInfo.setSpecType("multi");

                List<MallGoodsInitVo.SpecInfo> specList = CollectUtils.convert(mallGoodsSpecMapper.selectList(new QueryWrapper<MallGoodsSpec>().eq("goods_id", model.getGoodsId())), MallGoodsInitVo.SpecInfo.class);
                goodsInfo.setSpecList(specList);
            }

            if (model.getBrandId().equals(0)) goodsInfo.setBrandId("");
            if (model.getSupplyId().equals(0)) goodsInfo.setSupplyId("");
            //if (model.getPosterId().equals(0)) goodsInfo.setPosterId("");

            goodsInfo.setActiveGoodsCount(getActiveGoodsCount(goodsId));

            vo.setGoodsInfo(goodsInfo);
        }
        return vo;
    }

    /**
     * 查询商品正在交易中的数量
     *
     * @return
     */
    public Long getGoodsTransactionCount(Integer goodsId) {
        Integer[] orderStatus = {1, 2, 3};
        MPJQueryWrapper<MallOrderGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("og")
                .leftJoin("?_mall_order o ON og.order_id = o.order_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.eq("og.goods_id", goodsId);
        queryWrapper.in("o.status", Arrays.asList(orderStatus));
        return mallOrderGoodsMapper.selectJoinCount(queryWrapper);
    }

    /**
     * 查询商品正在交易中的数量
     *
     * @return
     */
    public Long getSkuTransactionCount(Integer[] skuIds) {
        Integer[] orderStatus = {1, 2, 3};
        MPJQueryWrapper<MallOrderGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("og")
                .leftJoin("?_mall_order o ON og.order_id = o.order_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.in("og.sku_id", Arrays.asList(skuIds));
        queryWrapper.in("o.status", Arrays.asList(orderStatus));
        return mallOrderGoodsMapper.selectJoinCount(queryWrapper);
    }

    /**
     * 查询商品参与营销活动的数量
     *
     * @return
     */
    public Long getActiveGoodsCount(Integer goodsId) {
        String[] activeType = {ActiveGoodsTypeEnum.GOODS_SINGLE.getType(), ActiveGoodsTypeEnum.GOODS_INDEPENDENT.getType()};
        return mallActiveGoodsMapper.selectCount(new QueryWrapper<MallActiveGoods>()
                .eq("site_id", RequestUtils.siteId())
                .eq("active_goods_status", "active")
                .in("active_goods_type", Arrays.asList(activeType))
                .eq("goods_id", goodsId)
        );
    }

    /**
     * 查询商品参与营销活动的数量
     *
     * @return
     */
    public Long getActiveGoodsCount(Integer[] goodsIds) {
        String[] activeType = {ActiveGoodsTypeEnum.GOODS_SINGLE.getType(), ActiveGoodsTypeEnum.GOODS_INDEPENDENT.getType()};
        return mallActiveGoodsMapper.selectCount(new QueryWrapper<MallActiveGoods>()
                .eq("site_id", RequestUtils.siteId())
                .eq("active_goods_status", "active")
                .in("active_goods_type", Arrays.asList(activeType))
                .in("goods_id", Arrays.asList(goodsIds))
        );
    }

    /**
     * 查询商品是否是供应商
     *
     * @return
     */
    public Long getSupplyGoodsCount(Integer[] goodsIds) {
        return mallGoodsMapper.selectCount(new QueryWrapper<MallGoods>()
                .eq("site_id", RequestUtils.siteId())
                .in("goods_id", Arrays.asList(goodsIds)).gt("supply_id", 0)
        );
    }

    /**
     * 复制商品
     *
     * @param goodsId
     */
    @Override
    @Transactional
    public void copy(Integer goodsId) {
        Integer siteId = RequestUtils.siteId();
        MallGoods model = mallGoodsMapper.selectOne(new QueryWrapper<MallGoods>().eq("goods_id", goodsId).eq("site_id", siteId).eq("delete_time", 0));
        Assert.notNull(model, "商品不存在");

        model.setGoodsId(null);
        model.setGoodsName(model.getGoodsName() + "_副本");
        model.setSort(0);
        model.setStatus(0);
        model.setSaleNum(0);
        model.setCreateTime(System.currentTimeMillis() / 1000);
        mallGoodsMapper.insert(model);

        // 复制sku
        List<MallGoodsSku> skuList = mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().eq("goods_id", goodsId));
        for (MallGoodsSku item : skuList) {
            item.setSkuId(null);
            item.setGoodsId(model.getGoodsId());
            item.setSaleNum(0);
        }
        shopGoodsSkuService.insertAll(skuList);

        // 复制规格
        List<MallGoodsSpec> specList = mallGoodsSpecMapper.selectList(new QueryWrapper<MallGoodsSpec>().eq("goods_id", goodsId));
        for (MallGoodsSpec item : specList) {
            item.setSpecId(null);
            item.setGoodsId(model.getGoodsId());
        }
        shopGoodsSpecService.insertAll(specList);
    }

    /**
     * 商品选择分页列表
     *
     * @param pageParam
     * @param searchParam
     * @return
     */
    @Override
    public MallSelectGoodsListVo getSelectPage(PageParam pageParam, MallGoodsSelectSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        MPJQueryWrapper<MallGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("g")
                .innerJoin("?_mall_goods_sku gs ON g.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("g.goods_id,g.site_id,g.goods_image,goods_name,goods_type,goods_cover,g.stock,g.sale_num,status,sort,create_time,member_discount");
        queryWrapper.eq("delete_time", 0);
        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) {
            queryWrapper.and(i -> i.like("goods_name", searchParam.getKeyword()).or().like("sub_title", searchParam.getKeyword()));
        }
        if (ObjectUtil.isNotEmpty(searchParam.getSkuIds())) {
            List<MallGoodsSku> selectGoodsList = mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().select("goods_id").in("sku_id", searchParam.getSkuIds()).groupBy("goods_id"));
            if (ObjectUtil.isNotEmpty(selectGoodsList)) {
                List<Integer> goodsIds = selectGoodsList.stream().map(i -> i.getGoodsId()).toList();
                if (ObjectUtil.isEmpty(searchParam.getGoodsIds())) searchParam.setGoodsIds(goodsIds);
            }
        }
        if ("all".equals(searchParam.getSelectType())) {
            queryWrapper.gt("g.stock", 0);
            queryWrapper.eq("g.status", 1);
        }
        //商品大分类
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsMallCategory())) {
            queryWrapper.eq("g.goods_mall_category", searchParam.getGoodsMallCategory());
        }
        //站点ID
        if (RequestUtils.siteId()!=0){
            queryWrapper.eq("g.site_id", RequestUtils.siteId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getSiteId())) {
            queryWrapper.eq("g.site_id", searchParam.getSiteId());
        }
        if ("selected".equals(searchParam.getSelectType()) && ObjectUtil.isNotEmpty(searchParam.getGoodsIds())) {
            queryWrapper.in("g.goods_id", searchParam.getGoodsIds());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsType()))
            queryWrapper.eq("g.goods_type", searchParam.getGoodsType());
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsCategory()))
            queryWrapper.like("g.goods_category", "\"" + searchParam.getGoodsCategory() + "\"");

        List<Integer> verifyGoodsIds = new LinkedList<>();
        List<Integer> verifySkuIds = new LinkedList<>();
        // 检测商品id集合是否存在，移除不存在的商品id，纠正数据准确性
        if (ObjectUtil.isNotEmpty(searchParam.getVerifyGoodsIds())) {
            List<MallGoods> verifyGoodsList = mallGoodsMapper.selectList(new QueryWrapper<MallGoods>()
                    .select("goods_id").in("goods_id", searchParam.getVerifyGoodsIds())
//                    .eq("site_id", RequestUtils.siteId())
                    .eq("delete_time", 0)
                    .eq("status", 1));
            if (ObjectUtil.isNotEmpty(verifyGoodsList)) {
                verifyGoodsIds = verifyGoodsList.stream().map(i -> i.getGoodsId()).collect(Collectors.toList());
            }
        }
        if (ObjectUtil.isNotEmpty(searchParam.getVerifySkuIds())) {
            List<MallGoodsSku> verifySkuList = mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().in("sku_id", searchParam.getVerifySkuIds()).select("sku_id"));
            if (ObjectUtil.isNotEmpty(verifySkuList)) {
                verifySkuIds = verifySkuList.stream().map(i -> i.getSkuId()).collect(Collectors.toList());
            }
        }
        queryWrapper.orderByDesc("g.goods_id");
        IPage<MallSelectGoodsListVo.ListVo> iPage = mallGoodsMapper.selectJoinPage(new Page<>(page, limit), MallSelectGoodsListVo.ListVo.class, queryWrapper);
        List<MallSelectGoodsListVo.ListVo> list = new LinkedList<>();
        for (MallSelectGoodsListVo.ListVo vo : iPage.getRecords()) {
            vo.setGoodsImage(vo.getGoodsImage());
            List<MallGoodsSku> skuList = mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().eq("goods_id", vo.getGoodsId()));
            //
            vo.setSkuList(null);
            List<Site> sites = siteMapper.selectList(new LambdaQueryWrapper<Site>().eq(Site::getSiteId, vo.getSiteId()));
            if (CollectionUtil.isNotEmpty(sites)) {
                Site site = sites.get(0);
                MallSelectGoodsListVo.Site setSite = new MallSelectGoodsListVo.Site();
                BeanUtils.copyProperties(site, setSite);
                vo.setSite(setSite);
                vo.setSiteName(setSite.getSiteName());
            }
            for (MallGoodsSku item : skuList) {
                if (item.getIsDefault().equals(1)) {
                    MallSelectGoodsListVo.GoodsSku goodsSkuVo = new MallSelectGoodsListVo.GoodsSku();
                    BeanUtils.copyProperties(item, goodsSkuVo);
                    vo.setGoodsSku(goodsSkuVo);
                    vo.setSkuId(goodsSkuVo.getSkuId());
                }
            }
            vo.setGoodsCoverThumbSmall(coreUploadService.thumb(RequestUtils.siteId(), vo.getGoodsCover(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType()));
            vo.setGoodsCoverThumbMid(coreUploadService.thumb(RequestUtils.siteId(), vo.getGoodsCover(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType()));
            list.add(vo);
        }
        PageResult pageResutl = PageResult.build(page, limit, iPage.getTotal()).setData(list);
        MallSelectGoodsListVo vo = new MallSelectGoodsListVo();
        BeanUtils.copyProperties(pageResutl, vo);
        vo.setVerifyGoodsIds(verifyGoodsIds);
        vo.setVerifySkuIds(verifySkuIds);
        return vo;
    }



    /**
     * 获取商品选择列表带ku
     *
     * @param searchParam
     */
    @Override
    public List<MallSelectGoodsListVo.ListVo> getSelectGoodsSku(MallGoodsSelectSearchParam searchParam) {
        MPJQueryWrapper<MallGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("g")
                .innerJoin("?_mall_goods_sku gs ON g.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("g.goods_id,g.site_id,g.goods_image,goods_name,goods_type,goods_cover,g.stock,g.sale_num,status,sort,create_time,member_discount");
        queryWrapper.eq("g.status", 1);
        queryWrapper.eq("delete_time", 0);

        // 检测商品id集合是否存在，移除不存在的商品id，纠正数据准确性
        if (ObjectUtil.isNotEmpty(searchParam.getVerifyGoodsIds())) {
            List<MallGoods> verifyGoodsList = mallGoodsMapper.selectList(new QueryWrapper<MallGoods>()
                    .select("goods_id").in("goods_id", searchParam.getVerifyGoodsIds())
//                    .eq("site_id", RequestUtils.siteId())
                    .eq("delete_time", 0).eq("status", 1));
            if (ObjectUtil.isNotEmpty(verifyGoodsList)) {
                List<Integer> verifyGoodsIds = verifyGoodsList.stream().map(i -> i.getGoodsId()).collect(Collectors.toList());
                queryWrapper.in("g.goods_id", verifyGoodsIds);
            }
        }

        if (ObjectUtil.isNotEmpty(searchParam.getVerifySkuIds())) {
            List<MallGoodsSku> verifySkuList = mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().in("sku_id", searchParam.getVerifySkuIds()).select("goods_id").groupBy("goods_id"));
            if (ObjectUtil.isNotEmpty(verifySkuList)) {
                List<Integer> verifyGoodsIds = verifySkuList.stream().map(i -> i.getGoodsId()).collect(Collectors.toList());
                queryWrapper.in("g.goods_id", verifyGoodsIds);
            }
        }

        List<MallSelectGoodsListVo.ListVo> list = mallGoodsMapper.selectJoinList(MallSelectGoodsListVo.ListVo.class, queryWrapper);
        for (MallSelectGoodsListVo.ListVo vo : list) {
            vo.setGoodsImage(vo.getGoodsImage());

            List<MallGoodsSku> skuList = mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().eq("goods_id", vo.getGoodsId()));
            vo.setSkuList(skuList);

            for (MallGoodsSku item : skuList) {
                if (item.getIsDefault().equals(1)) {
                    MallSelectGoodsListVo.GoodsSku goodsSkuVo = new MallSelectGoodsListVo.GoodsSku();
                    BeanUtils.copyProperties(item, goodsSkuVo);
                    vo.setGoodsSku(goodsSkuVo);
                }
            }
            vo.setGoodsCoverThumbSmall(coreUploadService.thumb(RequestUtils.siteId(), vo.getGoodsCover(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType()));
            vo.setGoodsCoverThumbMid(coreUploadService.thumb(RequestUtils.siteId(), vo.getGoodsCover(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType()));
        }
        return list;
    }

    /**
     * 查询商品SKU规格列表
     *
     * @param goodsId
     * @return
     */
    @Override
    public List<MallGoodsSkuListVo> getSkuList(Integer goodsId) {
        return CollectUtils.convert(mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().eq("goods_id", goodsId).eq("site_id", RequestUtils.siteId())), MallGoodsSkuListVo.class);
    }

    /**
     * 编辑商品规格列表库存
     *
     * @param param
     */
    @Override
    @Transactional
    public void editGoodsListStock(EditGoodsListStockParam param) {
        Integer siteId = RequestUtils.siteId();
        MallGoods model = mallGoodsMapper.selectOne(new QueryWrapper<MallGoods>().eq("goods_id", param.getGoodsId()).eq("site_id", siteId).select("goods_id"));
        Assert.notNull(model, "商品不存在");

        List<MallGoodsSku> skuList = new LinkedList<>();
        Integer stock = 0;
        for (EditGoodsListStockParam.Sku item : param.getSkuList()) {
            MallGoodsSku sku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>().eq("sku_id", item.getSkuId()).eq("goods_id", param.getGoodsId()).select("sku_id"));
            Assert.notNull(sku, "商品规格不存在");
            sku.setStock(item.getStock());
            skuList.add(sku);
            stock += item.getStock();
        }

        shopGoodsSkuService.insertAll(skuList);

        model.setStock(stock);
        mallGoodsMapper.updateById(model);
    }

    /**
     * 编辑商品规格列表价格
     *
     * @param param
     */
    @Override
    @Transactional
    public void editGoodsListPrice(EditGoodsListPriceParam param) {
        Integer siteId = RequestUtils.siteId();
        MallGoods model = mallGoodsMapper.selectOne(new QueryWrapper<MallGoods>().eq("goods_id", param.getGoodsId()).eq("site_id", siteId).select("goods_id"));
        Assert.notNull(model, "商品不存在");

        Long activeGoodsCount = getActiveGoodsCount(param.getGoodsId());

        List<MallGoodsSku> skuList = new LinkedList<>();
        for (EditGoodsListPriceParam.Sku item : param.getSkuList()) {
            MallGoodsSku sku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>().eq("sku_id", item.getSkuId()).eq("goods_id", param.getGoodsId()).select("sku_id"));
            Assert.notNull(sku, "商品规格不存在");
            sku.setCostPrice(item.getCostPrice());
            sku.setMarketPrice(item.getMarketPrice());
            if (activeGoodsCount == 0) {
                sku.setPrice(item.getPrice());
                sku.setSalePrice(item.getPrice());
            }
            skuList.add(sku);
        }

        shopGoodsSkuService.insertAll(skuList);
    }

    /**
     * 编辑商品规格列表会员价
     *
     * @param param
     */
    @Override
    @Transactional
    public void editGoodsListMemberPrice(EditGoodsListMemberPriceParam param) {
        Integer siteId = RequestUtils.siteId();
        MallGoods model = mallGoodsMapper.selectOne(new QueryWrapper<MallGoods>().eq("goods_id", param.getGoodsId()).eq("site_id", siteId).select("goods_id"));
        Assert.notNull(model, "商品不存在");

        List<MallGoodsSku> skuList = new LinkedList<>();
        for (EditGoodsListMemberPriceParam.Sku item : param.getSkuList()) {
            MallGoodsSku sku = mallGoodsSkuMapper.selectOne(new QueryWrapper<MallGoodsSku>().eq("sku_id", item.getSkuId()).eq("goods_id", param.getGoodsId()).select("sku_id"));
            Assert.notNull(sku, "商品规格不存在");
            sku.setMemberPrice(item.getMemberPrice().toString());
            skuList.add(sku);
        }

        shopGoodsSkuService.insertAll(skuList);

        model.setMemberDiscount(param.getMemberDiscount());
        mallGoodsMapper.updateById(model);
    }

    @Override
    public PageResult<GetBuyGoodsSelectVo> getBuyGoodsSelect(PageParam pageParam, BuyGoodsSelectParam param) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<MallGoods> goodsQueryWrapper = new QueryWrapper<>();
        goodsQueryWrapper.eq("site_id", RequestUtils.siteId())
                .gt("stock", 0)
                .eq("status", 1)
                .eq("is_gift", 0)
                .orderByDesc("sort", "create_time");

        if (ObjectUtil.isNotEmpty(param.getGoodsCategory())) {
            goodsQueryWrapper.like("goods_category", param.getGoodsCategory());
        }

        if (ObjectUtil.isNotEmpty(param.getGoodsType())) {
            goodsQueryWrapper.eq("goods_type", param.getGoodsType());
        }

        if (ObjectUtil.isNotEmpty(param.getKeyword())) {
            goodsQueryWrapper.and(wrapper -> wrapper
                    .like("goods_name", param.getKeyword())
                    .or()
                    .like("sub_title", param.getKeyword()));
        }

        Page<MallGoods> iPage = mallGoodsMapper.selectPage(new Page<>(page, limit), goodsQueryWrapper);

        Set<Integer> goodsIds = CollStreamUtil.toSet(iPage.getRecords(), MallGoods::getGoodsId);
        Map<Integer, List<MallGoodsSku>> skuMap = new HashMap<>(goodsIds.size(), 1);
        if (ObjectUtil.isNotEmpty(goodsIds)) {
            QueryWrapper<MallGoodsSku> skuQueryWrapper = new QueryWrapper<>();
            skuQueryWrapper.in("goods_id", goodsIds);
            skuMap.putAll(mallGoodsSkuMapper.selectList(skuQueryWrapper)
                    .stream().collect(Collectors.groupingBy(MallGoodsSku::getGoodsId)));
        }

        IPage<GetBuyGoodsSelectVo> convert = iPage.convert(o -> {
            GetBuyGoodsSelectVo vo = new GetBuyGoodsSelectVo();
            BeanUtils.copyProperties(o, vo);
            vo.setGoodsTypeName(GoodsTypeEnum.getNameByType(vo.getGoodsType()));

            String mid = coreUploadService.thumb(RequestUtils.siteId(), vo.getGoodsCover(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
            vo.setGoodsCoverThumbMid(mid);
            String small = coreUploadService.thumb(RequestUtils.siteId(), vo.getGoodsCover(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType());
            vo.setGoodsCoverThumbSmall(small);

            List<MallGoodsSku> skus = skuMap.get(vo.getGoodsId());
            if (ObjectUtil.isNotEmpty(skus)) {
                MallGoodsSku sku = skus.get(0);
                MemberLevelInfoVo memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());
                JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(sku.getMemberPrice()) ? JSONUtil.parseObj(sku.getMemberPrice()) : new JSONObject();
                BigDecimal price = goodsService.calculateMemberPrice(memberLevel, vo.getMemberDiscount(), memberPriceConfig, sku.getPrice());
                sku.setMemberPrice(price.toString());
                vo.setGoodsSku(sku);

            }

            Integer hasBuy = coreGoodsLimitBuyService.getGoodsHasBuyNumber(RequestUtils.siteId(), param.getMemberId(), vo.getGoodsId());
            vo.setHasBuy(hasBuy);

            return vo;
        });

        return PageResult.build(convert);
    }

    @Override
    public GetBuySkuSelectVo getBuySkuSelect(BuySkuSelectParam param) {
        QueryWrapper<MallGoodsSku> goodsSkuQueryWrapper = new QueryWrapper<>();
        goodsSkuQueryWrapper.eq("site_id", RequestUtils.siteId())
                .eq("sku_id", param.getSkuId());
        MallGoodsSku mallGoodsSku = mallGoodsSkuMapper.selectOne(goodsSkuQueryWrapper);
        if (mallGoodsSku == null) {
            return null;
        }

        GetBuySkuSelectVo vo = new GetBuySkuSelectVo();
        BeanUtils.copyProperties(mallGoodsSku, vo);

        String small = coreUploadService.thumb(RequestUtils.siteId(), mallGoodsSku.getSkuImage(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType());
        String mid = coreUploadService.thumb(RequestUtils.siteId(), mallGoodsSku.getSkuImage(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
        String big = coreUploadService.thumb(RequestUtils.siteId(), mallGoodsSku.getSkuImage(), UploadThumbTypeEnum.BIG.getType()).getDataMap().get(UploadThumbTypeEnum.BIG.getType());

        vo.setSkuImageThumbSmall(small);
        vo.setSkuImageThumbMid(mid);
        vo.setSkuImageThumbBig(big);

        MallGoods goods = mallGoodsMapper.selectById(vo.getGoodsId());
        GetBuySkuSelectVo.GoodsDTO goodsDTO = new GetBuySkuSelectVo.GoodsDTO();
        vo.setGoods(goodsDTO);

        QueryWrapper<MallGoodsSku> shopGoodsSkuQueryWrapper = new QueryWrapper<>();
        shopGoodsSkuQueryWrapper.eq("goods_id", goods.getGoodsId());
        List<MallGoodsSku> mallGoodsSkuses = mallGoodsSkuMapper.selectList(shopGoodsSkuQueryWrapper);
        vo.setSkuList(mallGoodsSkuses);

        QueryWrapper<MallGoodsSpec> shopGoodsSpecQueryWrapper = new QueryWrapper<>();
        shopGoodsSpecQueryWrapper.eq("goods_id", goods.getGoodsId());
        List<MallGoodsSpec> mallGoodsSpecs = mallGoodsSpecMapper.selectList(shopGoodsSpecQueryWrapper);
        vo.setGoodsSpec(mallGoodsSpecs);

        if (ObjectUtil.isNotEmpty(param.getMemberId())) {
            MemberLevelInfoVo memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());
            JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(mallGoodsSku.getMemberPrice()) ? JSONUtil.parseObj(mallGoodsSku.getMemberPrice()) : new JSONObject();
            BigDecimal memberPrice = goodsService.calculateMemberPrice(memberLevel, goods.getMemberDiscount(), memberPriceConfig, mallGoodsSku.getPrice());

            vo.setMemberPrice(memberPrice.toString());
            vo.setSkuList(getMemberPriceByList(memberLevel, goods.getMemberDiscount(), vo.getSkuList()));

            Integer hasBuy = coreGoodsLimitBuyService.getGoodsHasBuyNumber(RequestUtils.siteId(), param.getMemberId(), vo.getGoodsId());
            vo.setHasBuy(hasBuy);

        }
        return vo;
    }

    @Override
    public List<GetBuyGoodsSelectedVo> getBuyGoodsSelected(BuyGoodsSelectedParam param) {
        if (ObjectUtil.isEmpty(param.getSkuIds())) {
            return Collections.emptyList();
        }

        QueryWrapper<MallGoodsSku> shopGoodsSkuQueryWrapper = new QueryWrapper<>();
        shopGoodsSkuQueryWrapper.eq("site_id", RequestUtils.siteId())
                .in("sku_id", param.getSkuIds());
        List<MallGoodsSku> mallGoodsSkuses = mallGoodsSkuMapper.selectList(shopGoodsSkuQueryWrapper);
        if (ObjectUtil.isEmpty(mallGoodsSkuses)) {
            return Collections.emptyList();
        }

        Set<Integer> goodsIds = CollStreamUtil.toSet(mallGoodsSkuses, MallGoodsSku::getGoodsId);
        Map<Integer, MallGoods> goodsMap = new HashMap<>(goodsIds.size(), 1);
        if (ObjectUtil.isNotEmpty(goodsIds)) {
            goodsMap.putAll(mallGoodsMapper.selectBatchIds(goodsIds).stream().collect(Collectors.toMap(MallGoods::getGoodsId, o -> o)));
        }

        List<GetBuyGoodsSelectedVo> skuList = mallGoodsSkuses.stream()
                .map(sku -> {
                    GetBuyGoodsSelectedVo vo = new GetBuyGoodsSelectedVo();
                    BeanUtils.copyProperties(sku, vo);
                    vo.setGoods(goodsMap.get(vo.getGoodsId()));

                    String small = coreUploadService.thumb(RequestUtils.siteId(), sku.getSkuImage(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType());
                    vo.setGoodsCoverThumbMid(small);
                    String mid = coreUploadService.thumb(RequestUtils.siteId(), sku.getSkuImage(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
                    vo.setGoodsCoverThumbMid(mid);
                    return vo;
                })
                .toList();

        if (ObjectUtil.isNotEmpty(param.getMemberId())) {
            MemberLevelInfoVo memberLevel = memberLevelService.findMemberLevel(param.getMemberId());

            for (GetBuyGoodsSelectedVo sku : skuList) {
                if (ObjectUtil.isNotEmpty(sku.getGoods())) {
                    JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(sku.getMemberPrice()) ? JSONUtil.parseObj(sku.getMemberPrice()) : new JSONObject();
                    BigDecimal memberPrice = goodsService.calculateMemberPrice(memberLevel, sku.getGoods().getMemberDiscount(), memberPriceConfig, sku.getPrice());
                    sku.setMemberPrice(memberPrice.toString());
                }

                Integer hasBuy = coreGoodsLimitBuyService.getGoodsHasBuyNumber(RequestUtils.siteId(), param.getMemberId(), sku.getGoodsId());
                sku.setHasBuy(hasBuy);
            }
        }

        return skuList;
    }


    private List<MallGoodsSku> getMemberPriceByList(MemberLevelInfoVo memberLevelInfoVo, String memberDiscount, List<MallGoodsSku> skuList) {
        boolean isDefault = ObjectUtil.hasEmpty(memberDiscount, memberLevelInfoVo);
        for (MallGoodsSku sku : skuList) {
            if (isDefault) {
                sku.setMemberPrice(sku.getPrice().toString());
                continue;
            }

            BigDecimal memberPrice = switch (memberDiscount) {
                case "discount" -> {
                    if (ObjectUtil.isEmpty(memberLevelInfoVo.getLevelBenefits()) || ObjectUtil.isEmpty(memberLevelInfoVo.getLevelBenefits().getJSONObject("discount")) ||
                            ObjectUtil.isEmpty(memberLevelInfoVo.getLevelBenefits().getJSONObject("discount").get("is_use"))) {
                        yield sku.getPrice();
                    }

                    yield sku.getPrice().multiply(memberLevelInfoVo.getLevelBenefits().getJSONObject("discount").getBigDecimal("discount").divide(BigDecimal.TEN));
                }
                case "fixed_price" -> {
                    if (ObjectUtil.isEmpty(sku.getMemberPrice())) {
                        throw new RuntimeException("价格计算失败");
                    }

                    JSONObject mp = JSONUtil.parseObj(sku.getMemberPrice());
                    BigDecimal levelPrice = mp.getBigDecimal("level" + memberLevelInfoVo.getLevelName());
                    yield ObjectUtil.isEmpty(levelPrice) ? sku.getPrice() : levelPrice;
                }
                default -> throw new RuntimeException("价格计算失败");
            };

            sku.setMemberPrice(memberPrice.toString());
        }
        return skuList;
    }



    /**
     * 商品审核通过
     * 520
     * @param param
     * @return
     */
    @Override
    public int auditStatus(EditGoodsAuditStatusParam param) {
        int row = 0;
        MallGoods mallGoods = mallGoodsMapper.selectById(param.getGoodsIds());
        if(mallGoods != null){
            //查询商铺信息
            SiteInfoVo site = coreSiteService.getSiteCache(RequestUtils.siteId());
            if (site != null){
                if(SiteStatusEnum.EXPIRE.getCode().equals(site.getStatus())){
                    throw new AdminException("GOODS_SITE_EXPIRE");
                }
                if(SiteStatusEnum.CLOSE.getCode().equals(site.getStatus())){
                    throw new AdminException("GOODS_SITE_CLOSE_NOT_ALLOW");
                }
            }
            //审核通过商品上架时校验商品规格价格
            if (GoodsStatusEnum.ONLINE.getStatus().equals(mallGoods.getAuditStatus())){
                checkGoodsSkuPrice(mallGoods);
            }
            if(mallGoods.getStatus() == GoodsStatusEnum.WAIT_AUDIT.getStatus()){
                mallGoods.setStatus(GoodsStatusEnum.ONLINE.getStatus());
                row = mallGoodsMapper.updateById(mallGoods);

            }

            AfterMallGoodsStatusEditEvent event = new AfterMallGoodsStatusEditEvent();
            event.setSiteId(RequestUtils.siteId());
            event.setGoodsIds(Arrays.asList(mallGoods.getGoodsId()));
            EventAndSubscribeOfPublisher.publishAll(event);
        }

        return row;
    }

    /**
     * 检测商品sku价格
     * @param goods
     */
    public void checkGoodsSkuPrice(MallGoods goods) {
        Integer supplyGoodsId = 0;
        if (checkAddon("supply")) {
            // 查询供应商品ID
            MallGoods model = mallGoodsMapper.selectOne(new QueryWrapper<MallGoods>().eq("goods_id", goods.getGoodsId()).eq("site_id", RequestUtils.siteId()));
            if(model != null){
                supplyGoodsId = model.getSupplyGoodsId();
            }

            if (supplyGoodsId != null && supplyGoodsId > 0) {
                // 查询商品SKU数据
                List<MallGoodsSku> skuData = mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>().eq("goods_id", goods.getGoodsId()));

                Map<Integer,BigDecimal> filteredSkuData = new HashMap<>();
                skuData.forEach(sku -> {
                    filteredSkuData.put(sku.getSupplySkuId(), sku.getPrice());
                });

                // 触发事件
                CheckGoodsSkuPriceEvent event = new CheckGoodsSkuPriceEvent();
                event.setGoodsId(supplyGoodsId);
                event.setStatus(goods.getStatus());
                event.setSkuData(filteredSkuData);
                EventAndSubscribeOfPublisher.publishAll(event);
            }
        }
    }

    /**
     * 商品审核拒绝
     * 520
     * @param param
     * @return
     */
    @Override
    public int refuseStatus(EditGoodsRefuseStatusParam param) {
        int row = 0;
        MallGoods mallGoods = mallGoodsMapper.selectById(param.getGoodsIds());
        if(mallGoods != null){
            if(mallGoods.getStatus() == GoodsStatusEnum.WAIT_AUDIT.getStatus()){
                mallGoods.setStatus(GoodsStatusEnum.AUDIT_REFUSE.getStatus());
                mallGoods.setAuditReason(param.getAuditReason());
                row = mallGoodsMapper.updateById(mallGoods);
                AfterMallGoodsStatusEditEvent event = new AfterMallGoodsStatusEditEvent();
                event.setSiteId(RequestUtils.siteId());
                event.setGoodsIds(Arrays.asList(mallGoods.getGoodsId()));
                EventAndSubscribeOfPublisher.publishAll(event);
            }
        }
        return row;
    }

    /**
     * 商品强制下架
     * 520
     * @param param
     * @return
     */
    @Override
    public int offlineStatus(EditGoodsRefuseStatusParam param) {
        int row = 0;
        MallGoods mallGoods = mallGoodsMapper.selectById(param.getGoodsIds());
        if(mallGoods != null){
            if(mallGoods.getStatus() == GoodsStatusEnum.ONLINE.getStatus()){
                mallGoods.setStatus(GoodsStatusEnum.AUDI_TO_FINE.getStatus());
                mallGoods.setAuditReason(param.getAuditReason());
                row = mallGoodsMapper.updateById(mallGoods);
                AfterMallGoodsStatusEditEvent event = new AfterMallGoodsStatusEditEvent();
                event.setSiteId(RequestUtils.siteId());
                event.setGoodsIds(Arrays.asList(mallGoods.getGoodsId()));
                EventAndSubscribeOfPublisher.publishAll(event);
            }
        }
        return row;
    }

    @Override
    public List<Map<String, Object>> getSource() {
        JSONObject sourceType = GoodsSourceEnum.getSourceType();
        List<Map<String, Object>> result = new ArrayList<>();
        for (String key : sourceType.keySet()) {
            JSONObject item = sourceType.getJSONObject(key);
            Map<String, Object> map = new HashMap<>();
            map.put("key", key);
            map.put("name", item.getStr("name"));
            result.add(map);
        }

        return result;
    }

    /**
     * 编辑商品标签
     * @param param
     * @return
     */
    @Override
    public void editLabel(EditGoodsLabelParam param) {
        if (param.getGoodsIds() == null || param.getGoodsIds().isEmpty()) {
            return;
        }
        List<String> labelIdsStr = param.getLabelIds() != null ?
            param.getLabelIds().stream()
                .map(String::valueOf)
                .collect(Collectors.toList()) :
            new ArrayList<>();
        UpdateWrapper<MallGoods> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("goods_id", param.getGoodsIds())
                .set("label_ids", JSONUtil.toJsonStr(labelIdsStr));

        mallGoodsMapper.update(null, updateWrapper);
    }

}
