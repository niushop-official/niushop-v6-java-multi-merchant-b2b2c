package com.niu.mall.listener.goods;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.goods.GoodsBrand;
import com.niu.core.entity.goods.GoodsLabel;
import com.niu.core.entity.goods.GoodsService;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteCategory;
import com.niu.core.event.sys.ExportDataEventDefiner;
import com.niu.core.mapper.goods.GoodsBrandMapper;
import com.niu.core.mapper.goods.GoodsLabelMapper;
import com.niu.core.mapper.goods.GoodsServiceMapper;
import com.niu.core.mapper.site.SiteCategoryMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.entity.goods.MallGoods;
import com.niu.core.entity.goods.MallGoodsSku;
import com.niu.mall.enums.goods.*;
import com.niu.core.enums.goods.GoodsTypeEnum;
import com.niu.core.enums.goods.GoodsAuditStatusEnum;
import com.niu.core.enums.goods.GoodsStatusEnum;
import com.niu.core.mapper.goods.MallGoodsMapper;
import com.niu.core.mapper.goods.MallGoodsSkuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
@EventCallback("mall")
public class ShopGoodsExportDataListener extends ExportDataEventDefiner {

    @Autowired
    private MallGoodsMapper goodsMapper;
    @Autowired
    private MallGoodsSkuMapper goodsSkuMapper;
    @Autowired
    private SiteMapper siteMapper;
    @Autowired
    private GoodsBrandMapper brandMapper;
    @Autowired
    private SiteCategoryMapper siteCategoryMapper;
    @Autowired
    private GoodsLabelMapper goodsLabelMapper;
    @Autowired
    private GoodsServiceMapper goodsServiceMapper;

    @Override
    public ExportDataEventResult handleCallback(ExportDataEvent event) {
        if (!"shop_goods".equals(event.getType())) {
            return new ExportDataEventResult();
        }

        Integer siteId = event.getSiteId();
        JSONObject where = event.getWhere();
        PageParam pageParam = event.getPageParam();

        // 1. 构建商品查询条件
        LambdaQueryWrapper<MallGoods> goodsWrapper = new LambdaQueryWrapper<>();
        goodsWrapper.eq(MallGoods::getSiteId, siteId)
                .eq(MallGoods::getDeleteTime, 0);
        if (where != null) {
            String goodsName = where.getStr("goods_name");
            if (StrUtil.isNotBlank(goodsName)) {
                goodsWrapper.like(MallGoods::getGoodsName, goodsName);
            }
            String goodsType = where.getStr("goods_type");
            if (StrUtil.isNotBlank(goodsType)) {
                goodsWrapper.eq(MallGoods::getGoodsType, goodsType);
            }
            Integer brandId = where.getInt("brand_id");
            if (brandId != null) {
                goodsWrapper.eq(MallGoods::getBrandId, brandId);
            }
            Integer goodsCategory = where.getInt("goods_category");
            if (goodsCategory != null) {
                // 分类字段存储 JSON 数组，例如 "[1,2]"，使用 LIKE 匹配
                goodsWrapper.apply("JSON_CONTAINS(goods_category, {0})", goodsCategory);
            }
            Integer labelId = where.getInt("label_ids");
            if (labelId != null) {
                goodsWrapper.apply("JSON_CONTAINS(label_ids, {0})", labelId);
            }
            Integer serviceId = where.getInt("service_ids");
            if (serviceId != null) {
                goodsWrapper.apply("JSON_CONTAINS(service_ids, {0})", serviceId);
            }
            Integer saleNum = where.getInt("sale_num");
            if (saleNum != null) {
                goodsWrapper.ge(MallGoods::getSaleNum, saleNum);
            }
            String statusType = where.getStr("status_type");
            if (StrUtil.isNotBlank(statusType)) {
                var optional = GoodsStatusTypeEnum.getEnumByStatus(Integer.parseInt(statusType));
                if (!optional.isEmpty()) {
                    GoodsStatusTypeEnum statusTypeEnum = optional.get();
                    switch (statusTypeEnum) {
                        case ON_SALE -> goodsWrapper.eq(MallGoods::getStatus, GoodsStatusEnum.ONLINE.getStatus())
                                .eq(MallGoods::getAuditStatus, GoodsAuditStatusEnum.AUDIT_ADOPT.getStatus());
                        case IN_WAREHOUSE -> goodsWrapper.eq(MallGoods::getStatus, GoodsStatusEnum.OFFLINE.getStatus())
                                .eq(MallGoods::getAuditStatus, GoodsAuditStatusEnum.AUDIT_ADOPT.getStatus())
                                .eq(MallGoods::getIsSupplyUpdate, 0);
                        case PENDING_REVIEW ->
                                goodsWrapper.eq(MallGoods::getAuditStatus, GoodsAuditStatusEnum.WAIT_AUDIT.getStatus());
                        case REVIEW_FAILS ->
                                goodsWrapper.eq(MallGoods::getAuditStatus, GoodsAuditStatusEnum.AUDIT_REFUSE.getStatus());
                        case FORCE_REMOVAL ->
                                goodsWrapper.eq(MallGoods::getStatus, GoodsStatusEnum.AUDI_TO_FINE.getStatus());
                        case SUPPLY_UPDATE -> goodsWrapper.eq(MallGoods::getStatus, GoodsStatusEnum.OFFLINE.getStatus())
                                .eq(MallGoods::getAuditStatus, GoodsAuditStatusEnum.AUDIT_ADOPT.getStatus())
                                .eq(MallGoods::getIsSupplyUpdate, 1);
                        default -> {
                        }
                    }
                }
            }
            String goodsSource = where.getStr("goods_source");
            if (StrUtil.isNotBlank(goodsSource)) {
                if (goodsSource.equals("self_goods")) {
                    goodsWrapper.eq(MallGoods::getSupplyGoodsId, 0);
                } else {
                    goodsWrapper.ne(MallGoods::getSupplyGoodsId, 0);
                }
            }
            // 处理时间范围（如果有）
            if (where.getJSONArray("create_time") != null) {
                QueryMapperUtils.buildByTime(goodsWrapper, "create_time",
                        StringUtils.jsonArrayToStringArray(where.getJSONArray("create_time")));
            }
        }

        // 排序
        String order = where != null ? where.getStr("order") : null;
        String sort = where != null ? where.getStr("sort") : null;
        if (StrUtil.isNotBlank(order)) {
            boolean isAsc = "asc".equalsIgnoreCase(sort);
            if ("sale_num".equals(order)) {
                goodsWrapper.orderBy(true, isAsc, MallGoods::getSaleNum);
            } else if ("price".equals(order)) {
                // 价格排序需关联默认 SKU，此处不处理，在导出后整体排序
                // 暂不排序
            } else {
                goodsWrapper.orderBy(true, isAsc, MallGoods::getGoodsId);
            }
        } else {
            goodsWrapper.orderByDesc(MallGoods::getCreateTime);
        }

        // 2. 分页查询商品
        List<MallGoods> goodsList;
        if (pageParam.getPage() > 0 && pageParam.getLimit() > 0) {
            Page<MallGoods> page = goodsMapper.selectPage(new Page<>(pageParam.getPage(), pageParam.getLimit()), goodsWrapper);
            goodsList = page.getRecords();
        } else {
            goodsList = goodsMapper.selectList(goodsWrapper);
        }
        if (CollUtil.isEmpty(goodsList)) {
            return new ExportDataEventResult();
        }

        // 3. 批量查询 SKU 并过滤价格区间
        List<Integer> goodsIds = goodsList.stream().map(MallGoods::getGoodsId).collect(Collectors.toList());
        LambdaQueryWrapper<MallGoodsSku> skuWrapper = new LambdaQueryWrapper<>();
        skuWrapper.in(MallGoodsSku::getGoodsId, goodsIds);
        // 价格区间过滤
        BigDecimal startPrice = where != null && !where.getStr("start_price").isEmpty() ? where.getBigDecimal("start_price") : null;
        BigDecimal endPrice = where != null && !where.getStr("end_price").isEmpty() ? where.getBigDecimal("end_price") : null;
        if (startPrice != null && endPrice != null) {
            skuWrapper.between(MallGoodsSku::getPrice, startPrice, endPrice);
        } else if (startPrice != null) {
            skuWrapper.ge(MallGoodsSku::getPrice, startPrice);
        } else if (endPrice != null) {
            skuWrapper.le(MallGoodsSku::getPrice, endPrice);
        }
        List<MallGoodsSku> allSkus = goodsSkuMapper.selectList(skuWrapper);
        Map<Integer, List<MallGoodsSku>> skuMap = allSkus.stream().collect(Collectors.groupingBy(MallGoodsSku::getGoodsId));

        // 4. 查询供货商店铺名称
        Set<Integer> supplyIds = goodsList.stream().map(MallGoods::getSupplyId).filter(id -> id != null && id > 0).collect(Collectors.toSet());
        Map<Integer, String> supplySiteNameMap = new HashMap<>();
        if (CollUtil.isNotEmpty(supplyIds)) {
            List<Site> sites = siteMapper.selectList(new LambdaQueryWrapper<Site>().in(Site::getSiteId, supplyIds));
            supplySiteNameMap = sites.stream().collect(Collectors.toMap(Site::getSiteId, Site::getSiteName, (k1, k2) -> k1));
        }

        // 5. 查询品牌、分类、标签、服务等关联数据
        Set<Integer> brandIds = goodsList.stream().map(MallGoods::getBrandId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Integer, GoodsBrand> brandMap = new HashMap<>();
        if (CollUtil.isNotEmpty(brandIds)) {
            brandMap = brandMapper.selectByIds(brandIds).stream().collect(Collectors.toMap(GoodsBrand::getBrandId, b -> b));
        }
        // 解析商品分类ID列表（goods_category 存储 JSON 数组）
        Set<Integer> categoryIds = new HashSet<>();
        for (MallGoods goods : goodsList) {
            String goodsCategory = goods.getGoodsCategory();
            if (StrUtil.isNotBlank(goodsCategory)) {
                JSONArray jsonArray = new JSONArray(goodsCategory);
                for (Object id : jsonArray) {
                    categoryIds.add(Integer.valueOf(id.toString()));
                }
            }
        }
        Map<Integer, SiteCategory> categoryMap = new HashMap<>();
        if (CollUtil.isNotEmpty(categoryIds)) {
            categoryMap = siteCategoryMapper.selectByIds(categoryIds).stream()
                    .collect(Collectors.toMap(SiteCategory::getCategoryId, c -> c));
        }
        // 标签
        Set<Integer> labelIds = new HashSet<>();
        for (MallGoods goods : goodsList) {
            String labelIdsStr = goods.getLabelIds();
            if (StrUtil.isNotBlank(labelIdsStr)) {
                JSONArray jsonArray = new JSONArray(labelIdsStr);
                for (Object id : jsonArray) {
                    labelIds.add(Integer.valueOf(id.toString()));
                }
            }
        }
        Map<Integer, GoodsLabel> labelMap = new HashMap<>();
        if (CollUtil.isNotEmpty(labelIds)) {
            labelMap = goodsLabelMapper.selectByIds(labelIds).stream()
                    .collect(Collectors.toMap(GoodsLabel::getLabelId, l -> l));
        }
        // 服务
        Set<Integer> serviceIdsSet = new HashSet<>();
        for (MallGoods goods : goodsList) {
            String serviceIdsStr = goods.getServiceIds();
            if (StrUtil.isNotBlank(serviceIdsStr)) {
                JSONArray jsonArray = new JSONArray(serviceIdsStr);
                for (Object id : jsonArray) {
                    serviceIdsSet.add(Integer.valueOf(id.toString()));
                }
            }
        }
        Map<Integer, GoodsService> serviceMap = new HashMap<>();
        if (CollUtil.isNotEmpty(serviceIdsSet)) {
            serviceMap = goodsServiceMapper.selectByIds(serviceIdsSet).stream()
                    .collect(Collectors.toMap(GoodsService::getServiceId, s -> s));
        }

        // 6. 组装导出数据（每个 SKU 一行）
        JSONArray exportArray = new JSONArray();
        for (MallGoods goods : goodsList) {
            List<MallGoodsSku> skus = skuMap.getOrDefault(goods.getGoodsId(), Collections.emptyList());
            if (skus.isEmpty()) continue;

            // 预计算商品级别的拼接字段
            String categoryNames = buildNames(goods.getGoodsCategory(), categoryMap, "categoryName");
            String labelNames = buildNames(goods.getLabelIds(), labelMap, "labelName");
            String serviceNames = buildNames(goods.getServiceIds(), serviceMap, "serviceName");
            String deliveryTypeNames = ""; // 配送方式名称，根据商品配送类型字段补充
            String isFreeShippingName = (goods.getIsFreeShipping() != null && goods.getIsFreeShipping() == 1) ? "是" : "否";
            String isDiscountName = (goods.getIsDiscount() != null && goods.getIsDiscount() == 1) ? "是" : "否";
            String isLimitName = (goods.getIsLimit() != null && goods.getIsLimit() == 1) ? "是" : "否";
            String limitTypeName = "";
            if (goods.getIsLimit() != null && goods.getIsLimit() == 1 && goods.getLimitType() != null) {
                limitTypeName = GoodsLimitTypeEnum.getNameByCode(goods.getLimitType());
            }
            String statusName = GoodsStatusEnum.getNameByStatus(goods.getStatus());
            String goodsTypeName = GoodsTypeEnum.getNameByType(goods.getGoodsType());
            String brandName = brandMap.get(goods.getBrandId()) != null ? brandMap.get(goods.getBrandId()).getBrandName() : "";
            String supplySiteName = supplySiteNameMap.getOrDefault(goods.getSupplyId(), "");

            for (MallGoodsSku sku : skus) {
                JSONObject row = new JSONObject();
                row.set("goods_id", goods.getGoodsId());
                row.set("sku_id", sku.getSkuId());
                row.set("goods_name", goods.getGoodsName());
                row.set("sku_name", sku.getSkuName());
                row.set("sku_no", sku.getSkuNo());
                row.set("sku_spec_format", sku.getSkuSpecFormat());
                row.set("goods_type_name", goodsTypeName);
                row.set("sub_title", goods.getSubTitle());
                row.set("goods_image", goods.getGoodsImage());
                row.set("sku_image", sku.getSkuImage());
                row.set("category_name", categoryNames);
                row.set("goods_mall_category_name", ""); // 平台分类，根据实际补充
                row.set("goods_desc", goods.getGoodsDesc());
                row.set("brand_name", brandName);
                row.set("label_name", labelNames);
                row.set("service_name", serviceNames);
                row.set("unit", goods.getUnit());
                row.set("price", sku.getPrice());
                row.set("stock", sku.getStock());
                row.set("sale_num", sku.getSaleNum());
                row.set("status_name", statusName);
                row.set("delivery_type_name", deliveryTypeNames);
                row.set("is_free_shipping_name", isFreeShippingName);
                row.set("is_discount_name", isDiscountName);
                row.set("is_limit_name", isLimitName);
                row.set("limit_type_name", limitTypeName);
                row.set("supply_site_name", supplySiteName);
                exportArray.add(row);
            }
        }

        ExportDataEventResult result = new ExportDataEventResult();
        result.setResultData(exportArray);
        return result;
    }

    /**
     * 将 JSON 格式的 ID 数组转换为对应的名称列表拼接字符串
     *
     * @param idsJson   JSON 数组字符串，例如 "[1,2,3]"
     * @param idMap     id 到实体的映射
     * @param nameField 实体中名称字段的 getter 方法名（通过反射或直接传入 Function，此处简化）
     */
    private String buildNames(String idsJson, Map<Integer, ? extends Object> idMap, String nameField) {
        if (StrUtil.isBlank(idsJson)) return "";
        JSONArray idsArray = new JSONArray(idsJson);
        List<String> names = new ArrayList<>();
        for (Object idObj : idsArray) {
            Integer id = Integer.valueOf(idObj.toString());
            Object entity = idMap.get(id);
            if (entity != null) {
                try {
                    String name = (String) entity.getClass().getMethod("get" + Character.toUpperCase(nameField.charAt(0)) + nameField.substring(1)).invoke(entity);
                    if (StrUtil.isNotBlank(name)) {
                        names.add(name);
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        return String.join(";", names);
    }
}