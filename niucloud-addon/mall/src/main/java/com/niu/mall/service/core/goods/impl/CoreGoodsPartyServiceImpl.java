package com.niu.mall.service.core.goods.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.entity.goods.GoodsBrand;
import com.niu.core.entity.goods.GoodsCategory;
import com.niu.core.entity.goods.GoodsService;
import com.niu.core.entity.site.Site;
import com.niu.core.mapper.goods.GoodsBrandMapper;
import com.niu.core.mapper.goods.GoodsCategoryMapper;
import com.niu.core.mapper.goods.GoodsServiceMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.entity.goods.MallGoodsLabel;
import com.niu.mall.enums.goods.GoodsStatusEnum;
import com.niu.mall.enums.goods.GoodsTypeEnum;
import com.niu.mall.enums.goods.MallGoodsSourceTypeEnum;
import com.niu.mall.mapper.goods.MallGoodsLabelMapper;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import com.niu.mall.service.core.goods.DTO.GoodsImportCollectDTO;
import com.niu.mall.service.core.goods.ICoreGoodsPartyService;
import com.niu.mall.service.core.goods.param.GoodsPartyParam;
import com.niu.mall.service.site.goods.IMallGoodsService;
import com.niu.mall.service.site.goods.IMallVirtualGoodsService;
import com.niu.mall.service.site.goods.param.MallGoodsParam;
import com.niu.mall.service.site.goods.param.MallVirtualGoodsParam;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品导入/采集核心服务（统一支持导入+采集）
 */
@Slf4j
@Service

public class CoreGoodsPartyServiceImpl implements ICoreGoodsPartyService {

    @Resource
    private MallGoodsMapper goodsMapper;
    @Resource
    private GoodsCategoryMapper categoryMapper;
    @Resource
    private GoodsBrandMapper brandMapper;
    @Resource
    private MallGoodsLabelMapper labelMapper;
    @Resource
    private GoodsServiceMapper serviceMapper;
    @Resource
    private SiteMapper siteMapper;
    @Resource
    private IMallGoodsService goodsService;
    @Resource
    private IMallVirtualGoodsService virtualGoodsService;

    // ================== 公开方法 ==================

    /**
     * 导入业务入口
     */
    @Override
    public Map<String, Object> importGoods(List<Map<String, Object>> params, String goodsType, Integer siteId) {
        try {
            if (GoodsTypeEnum.REAL.getType().equals(goodsType) || GoodsTypeEnum.VIRTUAL.getType().equals(goodsType)) {
                return handle(params, MallGoodsSourceTypeEnum.PART_IMPORT.getType(), goodsType, siteId);
            } else {
                // 卡密商品暂不支持
                return Map.of("success", new ArrayList<>(), "fail", new ArrayList<>());
            }
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 采集业务入口（后续扩展）
     */
    @Override
    public Map<String, Object> collect(List<Map<String, Object>> params, Integer siteId) {
        try {
            return handle(params, MallGoodsSourceTypeEnum.PART_COLLECT.getType(), null, siteId);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 统一处理入口
     */
    private Map<String, Object> handle(List<Map<String, Object>> goodsData, Integer bizType, String goodsType, Integer siteId) {
        String sourceGoodsId = goodsData.get(0).get("goods_id").toString();
        Map<String, Object> result = new HashMap<>();
        try {
            // 检查商品是否已存在
            long count = goodsMapper.selectCount(new LambdaQueryWrapper<MallGoods>()
                    .eq(MallGoods::getSourceType, bizType)
                    .eq(MallGoods::getSourceGoodsId, sourceGoodsId));
            if (count > 0) {
                result.put("fail", Map.of("goods_id", sourceGoodsId, "msg", "商品已存在或重复的goods_id"));
            } else {
                // 判断规格类型
                String specType = getSpecType(goodsData);
                for (Map<String, Object> info : goodsData) {
                    info.put("spec_type", specType);
                }

                // 封装DTO
                GoodsImportCollectDTO dto = new GoodsImportCollectDTO(goodsData, bizType, goodsType);
                // 数据校验
                validateData(dto);
                // 数据转换
                GoodsPartyParam goodsCreateData = convertToGoodsCreateData(dto, siteId);
                // 创建商品
                Integer localGoodsId = createGoods(goodsCreateData);
                result.put("success", Map.of("goods_id", sourceGoodsId, "local_goods_id", localGoodsId, "msg", "处理成功"));
            }
        } catch (Exception e) {
            result.put("fail", Map.of("goods_id", sourceGoodsId, "msg", e.getMessage()));
        }
        return result;
    }

    /**
     * 按 goods_id 分组数据
     */
    public static Map<Long, List<Map<String, Object>>> groupDataByGoodsId(List<Map<String, Object>> params) {
        Map<Long, List<Map<String, Object>>> group = new HashMap<>();
        for (Map<String, Object> item : params) {
            if (!item.containsKey("goods_id")) {
                throw new CommonException("参数goods_id不能为空");
            }
            Long goodsId = Long.parseLong(item.get("goods_id").toString());
            group.computeIfAbsent(goodsId, k -> new ArrayList<>()).add(item);
        }
        return group;
    }

    // ================== 私有方法 ==================

    /**
     * 数据校验
     */
    private void validateData(GoodsImportCollectDTO dto) {
        List<Map<String, Object>> rawData = dto.getRawData();
        Integer bizType = dto.getBizType();
        String goodsType = dto.getGoodsType();
        Map<String, Object> firstItem = rawData.get(0);

        // 获取所有允许的字段
        List<String> allFields;
        if (bizType.equals(MallGoodsSourceTypeEnum.PART_IMPORT.getType())) {
            if (GoodsTypeEnum.REAL.getType().equals(goodsType)) {
                allFields = ImportCollectDict.importRealMap().stream().map(m -> m.get("key")).collect(Collectors.toList());
            } else {
                allFields = ImportCollectDict.importVirtualMap().stream().map(m -> m.get("key")).collect(Collectors.toList());
            }
        } else {
            allFields = ImportCollectDict.collectMap().stream().map(m -> m.get("key")).collect(Collectors.toList());
        }
        allFields.add("spec_type");

        // 非法字段校验
        for (String field : firstItem.keySet()) {
            if (!allFields.contains(field)) {
                throw new CommonException("非法参数：" + field);
            }
        }

        // 必填字段列表
        Map<String, String> requiredFields = ImportCollectDict.getRequiredField();
        List<String> requiredFieldKeys = requiredFields.keySet().stream().toList();
        for (Map<String, Object> row : rawData) {
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                String field = entry.getKey();
                String value = entry.getValue() != null ? entry.getValue().toString() : "";
                // 必传字段校验
                if (requiredFieldKeys.contains(field) && StrUtil.isBlank(value)) {
                    throw new CommonException("参数必填：" + requiredFields.get(field));
                }
                // 商品类型检测
                if ("goods_type".equals(field)) {
                    if (!(GoodsTypeEnum.REAL.getType().equals(value) || GoodsTypeEnum.VIRTUAL.getType().equals(value))) {
                        throw new CommonException("参数值非法：" + requiredFields.get(field));
                    }
                }
                // 商品状态校验
                if ("status".equals(field)) {
                    Integer status = Integer.parseInt(value);
                    if (!(GoodsStatusEnum.ONLINE.getStatus().equals(status) || GoodsStatusEnum.OFFLINE.getStatus().equals(status))) {
                        throw new CommonException("参数值非法：" + requiredFields.get(field));
                    }
                }
                // 价格校验
                if ("price".equals(field)) {
                    try {
                        BigDecimal price = new BigDecimal(value);
                        if (price.compareTo(BigDecimal.ZERO) <= 0) {
                            throw new CommonException("参数值必须大于0：" + requiredFields.get(field));
                        }
                    } catch (NumberFormatException e) {
                        throw new CommonException("参数值非法：" + requiredFields.get(field));
                    }
                }
                // 库存校验
                if ("stock".equals(field)) {
                    try {
                        int stock = Integer.parseInt(value);
                        if (stock <= 0) {
                            throw new CommonException("参数值必须大于0：" + requiredFields.get(field));
                        }
                    } catch (NumberFormatException e) {
                        throw new CommonException("参数值非法：" + requiredFields.get(field));
                    }
                }
                // 规格类型校验
                if ("spec_type".equals(field) && !("single".equals(value) || "multi".equals(value))) {
                    throw new CommonException("参数值非法：" + requiredFields.get(field));
                }
            }

            // 多规格校验
            if ("multi".equals(row.get("spec_type"))) {
                String goodsSpecFormat = (String) row.get("goods_spec_format");
                if (StrUtil.isBlank(goodsSpecFormat)) {
                    throw new CommonException("参数值不能为空：多规格项参数");
                }
            }

            // 实物商品配送方式必填
            if ("real".equals(goodsType)) {
                String deliveryType = (String) row.get("delivery_type");
                if (StrUtil.isBlank(deliveryType)) {
                    throw new CommonException("参数值不能为空：支持配送方式");
                }
            }

            // 虚拟商品校验
            if ("virtual".equals(goodsType)) {
                String virtualDeliveryType = (String) row.get("virtual_delivery_type");
                if (StrUtil.isBlank(virtualDeliveryType)) {
                    throw new CommonException("参数值不能为空：虚拟商品发货方式");
                }
                // 自动/手动发货校验
                if ("auto".equals(virtualDeliveryType) || "manual".equals(virtualDeliveryType)) {
                    String receiveType = (String) row.get("virtual_receive_type");
                    if (StrUtil.isBlank(receiveType)) {
                        throw new CommonException("参数值不能为空：虚拟商品收货方式");
                    }
                    if (!("auto".equals(receiveType) || "artificial".equals(receiveType))) {
                        throw new CommonException("参数值非法：虚拟商品收货方式");
                    }
                }
                // 到店核销校验
                if ("verify".equals(virtualDeliveryType)) {
                    Integer verifyType = (Integer) row.get("virtual_verify_type");
                    if (verifyType == null) {
                        throw new CommonException("参数值不能为空：虚拟商品核销有效期类型");
                    }
                    if (!(verifyType == 0 || verifyType == 1 || verifyType == 2)) {
                        throw new CommonException("参数值非法：虚拟商品核销有效期类型");
                    }
                    if (verifyType == 1 || verifyType == 2) {
                        Object indate = row.get("virtual_indate");
                        if (!(indate instanceof Number)) {
                            throw new CommonException("参数值非法：虚拟到期时间");
                        }
                    }
                    Integer refund = (Integer) row.get("virtual_refund");
                    if (refund == null) {
                        throw new CommonException("参数值不能为空：虚拟商品退款类型");
                    }
                    if (!(refund == 0 || refund == 1 || refund == 2)) {
                        throw new CommonException("参数值非法：虚拟商品退款类型");
                    }
                    if (refund == 1) {
                        Object refundDay = row.get("virtual_refund_day");
                        if (!(refundDay instanceof Number)) {
                            throw new CommonException("参数值非法：退款有效天数");
                        }
                    }
                }
            }
        }
    }

    /**
     * 数据转换（适配商品创建格式）
     */
    private GoodsPartyParam convertToGoodsCreateData(GoodsImportCollectDTO dto, Integer siteId) {
        List<Map<String, Object>> rawData = dto.getRawData();
        GoodsPartyParam goodsData = buildGoodsBaseData(dto, siteId);
        String specType = getSpecType(rawData);

        goodsData.setSiteId(siteId);
        goodsData.setSpecType(specType);
        goodsData.setSourceType(dto.getBizType());

        if ("multi".equals(specType)) {
            // 多规格
            Map<String, GoodsPartyParam.SkuData> skuData = buildGoodsSkuData(rawData, dto.getBizType());
            goodsData.setGoodsSkuData(skuData);
            // 总库存
            int totalStock = skuData.values().stream().mapToInt(GoodsPartyParam.SkuData::getStock).sum();
            goodsData.setStock(totalStock);
            if (totalStock <= 0) {
                goodsData.setStock(GoodsStatusEnum.OFFLINE.getStatus());
            }
            // goods_spec_format
            goodsData.setGoodsSpecFormat(JSONUtil.parseArray(buildGoodsSpecFormat(rawData.get(0))));
        } else {
            // 单规格
            Map<String, Object> firstItem = rawData.get(0);
            goodsData.setPrice(getBigDecimalValue("price", firstItem.get("price"), BigDecimal.ZERO));
            goodsData.setMarketPrice(getBigDecimalValue("market_price", firstItem.get("market_price"), BigDecimal.ZERO));
            goodsData.setCostPrice(getBigDecimalValue("cost_price", firstItem.get("cost_price"), BigDecimal.ZERO));
            goodsData.setWeight(getBigDecimalValue("weight", firstItem.get("weight"), BigDecimal.ZERO));
            goodsData.setVolume(getBigDecimalValue("volume", firstItem.get("volume"), BigDecimal.ZERO));
            int stock = getIntValue("stock", firstItem.get("stock"), 0);
            goodsData.setStock(stock);
            if (stock <= 0) {
                goodsData.setStock(GoodsStatusEnum.OFFLINE.getStatus());
            }
            goodsData.setGoodsSkuData(new HashMap<>());
            goodsData.setSourceSkuId(Integer.parseInt((String) firstItem.get("sku_id")));
        }
        return goodsData;
    }

    /**
     * 构建商品基础数据
     */
    private GoodsPartyParam buildGoodsBaseData(GoodsImportCollectDTO dto, Integer siteId) {
        Map<String, Object> firstItem = dto.getRawData().get(0);
        String goodsType = dto.getGoodsType();
        GoodsPartyParam partyParam = new GoodsPartyParam();
        partyParam.setGoodsName((String) firstItem.get("goods_name"));
        partyParam.setSkuName((String) firstItem.get("sku_name"));
        partyParam.setSubTitle((String) firstItem.get("sub_title"));
        partyParam.setGoodsType(goodsType);
        partyParam.setGoodsImage(buildGoodsImage(firstItem.get("goods_image")));
        partyParam.setGoodsMallCategory(handleMallCategory((String) firstItem.get("goods_mall_category")));
        partyParam.setGoodsCategory(handleCategory((String) firstItem.get("goods_category"), siteId));
        partyParam.setBrandId(handleBrand((String) firstItem.get("brand_name"), siteId));
        partyParam.setLabelIds(handleLabels((String) firstItem.get("label_names")));
        partyParam.setServiceIds(handleServices((String) firstItem.get("service_names")));
        partyParam.setSupplierId(handleSupplier((String) firstItem.get("supplier_name")));
        partyParam.setStatus(Integer.parseInt((String) firstItem.get("status")));
        partyParam.setAttrFormat(new JSONArray());

        partyParam.setSkuNo((String) getValue("sku_no", firstItem.get("sku_no"), ""));
        partyParam.setUnit((String) getValue("unit", firstItem.get("unit"), "件"));
        partyParam.setSkuSpecFormat(JSONUtil.parseArray(getJSONArray("sku_spec_format", firstItem.get("sku_spec_format"), "1")));
        partyParam.setGoodsSpecFormat(new JSONArray());
        partyParam.setGoodsDesc(buildGoodsDesc((String) firstItem.get("goods_desc")));
        partyParam.setDeliveryType(handleDeliveryType((String) firstItem.get("delivery_type")));

        // 默认字段
        partyParam.setShopSort(getIntValue("shop_sort", firstItem.get("shop_sort"), 0));
        partyParam.setGoodsVideo(buildGoodsVideo((String) firstItem.get("goods_video")));
        partyParam.setIsLimit(getIntValue("is_limit", firstItem.get("is_limit"), 0));
        partyParam.setLimitType(getIntValue("limit_type", firstItem.get("limit_type"), 0));
        partyParam.setMaxBuy(getIntValue("max_buy", firstItem.get("max_buy"), 0));
        partyParam.setMinBuy(getIntValue("min_buy", firstItem.get("min_buy"), 0));
        partyParam.setIsFreeShipping(getIntValue("is_free_shipping", firstItem.get("is_free_shipping"), 0));
        partyParam.setFeeType((String) getValue("fee_type", firstItem.get("fee_type"), ""));
        partyParam.setDeliveryMoney(getBigDecimalValue("delivery_money", firstItem.get("delivery_money"), BigDecimal.ZERO));
        partyParam.setDeliveryTemplateId(getIntValue("delivery_template_id", firstItem.get("delivery_template_id"), 0));
        partyParam.setIsDiscount(getIntValue("is_discount", firstItem.get("is_discount"), 0));
        partyParam.setMemberDiscount((String) getValue("member_discount", firstItem.get("member_discount"), ""));
        partyParam.setFormId(getIntValue("form_id", firstItem.get("form_id"), 0));
        partyParam.setAttrIds(JSONUtil.parseArray(firstItem.getOrDefault("attr_ids", "[]")));
        partyParam.setShopAttrIds(JSONUtil.parseArray(firstItem.getOrDefault("shop_attr_ids", "[]")));

        // 新字段
        partyParam.setTimerOffStatus(getIntValue("timer_off_status", firstItem.get("timer_off_status"), 0));
        partyParam.setTimerOffTime(getIntValue("timer_off_time", firstItem.get("timer_off_time"), 0));
        partyParam.setTimerOnStatus(getIntValue("timer_on_status", firstItem.get("timer_on_status"), 0));
        partyParam.setTimerOnTime(getIntValue("timer_on_time", firstItem.get("timer_on_time"), 0));
        partyParam.setSupportPayType((String) firstItem.getOrDefault("support_pay_type", "[]"));
        partyParam.setIsEnableRefund(getIntValue("is_enable_refund", firstItem.get("is_enable_refund"), 0));
        partyParam.setIsEnablePointDeduct(getIntValue("is_enable_point_deduct", firstItem.get("is_enable_point_deduct"), 0));

        // 商品参数
        String attrFormat = (String) firstItem.get("attr_format");
        if (StrUtil.isNotBlank(attrFormat)) {
            partyParam.setAttrFormat(new JSONArray(buildAttrFormat(firstItem)));
        }

        // 虚拟商品额外字段
        if ("virtual".equals(goodsType)) {
            partyParam.setVirtualDeliveryType((String) getValue("virtual_delivery_type", firstItem.get("virtual_delivery_type"), "auto"));
            partyParam.setVirtualReceiveType((String) getValue("virtual_receive_type", firstItem.get("virtual_receive_type"), "artificial"));
            partyParam.setVirtualDeliveryContent((String) getValue("virtual_delivery_content", firstItem.get("virtual_delivery_content"), ""));
            partyParam.setVirtualVerifyType(getIntValue("virtual_verify_type", firstItem.get("virtual_verify_type"), 0));
            partyParam.setVirtualIndate(getIntValue("virtual_indate", firstItem.get("virtual_indate"), 0));
            partyParam.setVirtualRefund(getIntValue("virtual_refund", firstItem.get("virtual_refund"), 0));
            partyParam.setVirtualRefundDay(getIntValue("virtual_refund_day", firstItem.get("virtual_refund_day"), 0));
            partyParam.setVirtualVerifyMobile(getIntValue("virtual_verify_mobile", firstItem.get("virtual_verify_mobile"), 0));
        }

        partyParam.setSourceGoodsId((String) firstItem.get("goods_id"));
        return partyParam;
    }

    // ========== 辅助构建方法 ==========
    private String buildGoodsImage(Object goodsImage) {
        if (goodsImage == null) return "";
        List<String> imgList;
        if (goodsImage instanceof List) {
            imgList = (List<String>) goodsImage;
        } else {
            imgList = Arrays.asList(goodsImage.toString().split(","));
        }
        // 本地化图片逻辑（省略，可调用文件服务）
        return String.join(",", imgList);
    }

    private String buildSkuImage(Object skuImage) {
        // 本地化图片逻辑
        return skuImage != null ? skuImage.toString() : "";
    }

    private String buildGoodsVideo(String goodsVideo) {
        // 本地化视频逻辑
        return goodsVideo != null ? goodsVideo : "";
    }

    private String buildGoodsDesc(String goodsDesc) {
        // 解析图片视频并本地化（暂不实现）
        return goodsDesc != null ? goodsDesc : "";
    }

    private Map<String, GoodsPartyParam.SkuData> buildGoodsSkuData(List<Map<String, Object>> rawData, int bizType) {
        Map<String, GoodsPartyParam.SkuData> skuList = new HashMap<>();
        for (int i = 0; i < rawData.size(); i++) {
            Map<String, Object> item = rawData.get(i);
            String skuSpecFormat = (String) item.get("sku_spec_format");
            List<Map<String, String>> skuSpec = new ArrayList<>();
            if (StrUtil.isNotBlank(skuSpecFormat)) {
                for (String val : skuSpecFormat.split(",")) {
                    Map<String, String> spec = new HashMap<>();
                    spec.put("spec_value_name", val.trim());
                    skuSpec.add(spec);
                }
            }
            GoodsPartyParam.SkuData sku = new GoodsPartyParam.SkuData();
            sku.setSpecName((String) item.get("sku_name"));
            sku.setSkuSpec(JSONUtil.parseArray(skuSpec));
            sku.setSkuNo((String) getValue("sku_no", item.get("sku_no"), ""));
            sku.setSkuImage(buildSkuImage(item.get("sku_image")));
            sku.setPrice(getBigDecimalValue("price", item.get("price"), BigDecimal.ZERO));
            sku.setMarketPrice(getBigDecimalValue("market_price", item.get("market_price"), BigDecimal.ZERO));
            sku.setCostPrice(getBigDecimalValue("cost_price", item.get("cost_price"), BigDecimal.ZERO));
            sku.setStock(getIntValue("stock", item.get("stock"), 0));
            sku.setWeight(getBigDecimalValue("weight", item.get("weight"), BigDecimal.ZERO));
            sku.setVolume(getBigDecimalValue("volume", item.get("volume"), BigDecimal.ZERO));
            sku.setIsDefault(i == 0 ? 1 : 0);
            sku.setIsSell(1);
            sku.setSourceType(bizType);
            sku.setSourceGoodsId(item.containsKey("goods_id") && item.get("goods_id") != null ?
                    Integer.parseInt((String) item.get("goods_id")) : null);
            sku.setSourceSkuId(item.containsKey("sku_id") && item.get("sku_id") != null ?
                    Integer.parseInt((String) item.get("sku_id")) : null);
            var key = "sku_" + i;
            skuList.put(key, sku);
        }
        return skuList;
    }

    private List<Map<String, Object>> buildGoodsSpecFormat(Map<String, Object> firstItem) {
        String specStr = (String) firstItem.get("goods_spec_format");
        if (StrUtil.isBlank(specStr)) return new ArrayList<>();
        List<Map<String, Object>> result = new ArrayList<>();
        String[] items = specStr.split(";");
        for (String item : items) {
            String[] parts = item.split(":");
            if (parts.length < 2) continue;
            String specName = parts[0].trim();
            String[] values = parts[1].split(",");
            List<Map<String, String>> valueList = new ArrayList<>();
            for (String val : values) {
                Map<String, String> v = new HashMap<>();
                v.put("spec_value_name", val.trim());
                valueList.add(v);
            }
            Map<String, Object> specItem = new HashMap<>();
            specItem.put("spec_name", specName);
            specItem.put("values", valueList);
            result.add(specItem);
        }
        return result;
    }

    private List<Map<String, Object>> buildAttrFormat(Map<String, Object> firstItem) {
        String attrStr = (String) firstItem.get("attr_format");
        if (StrUtil.isBlank(attrStr)) return new ArrayList<>();
        List<Map<String, Object>> result = new ArrayList<>();
        String[] items = attrStr.split(";");
        for (String item : items) {
            String[] parts = item.split(":");
            if (parts.length < 2) continue;
            Map<String, Object> attr = new HashMap<>();
            attr.put("attr_value_id", "-" + (100 + new Random().nextInt(900)));
            attr.put("attr_value_name", parts[0].trim());
            attr.put("type", "text");
            attr.put("sort", 0);
            attr.put("attr_child_value_id", "");
            attr.put("attr_child_value_name", parts[1].trim());
            result.add(attr);
        }
        return result;
    }

    private String getSpecType(List<Map<String, Object>> rawData) {
        if (rawData.size() == 1) {
            String skuName = (String) rawData.get(0).get("sku_name");
            return StrUtil.isBlank(skuName) ? "single" : "multi";
        }
        return "multi";
    }

    // ========== 数据转换辅助 ==========
    private Object getValue(String key, Object value, Object defaultValue) {
        return value != null ? value.toString() : defaultValue;
    }

    private JSONArray getJSONArray(String key, Object value, Object defaultValue) {
        String json = value != null ? value.toString() : defaultValue.toString();
        return JSONUtil.parseArray(json.split(","));
    }

    private Integer getIntValue(String key, Object value, Integer defaultValue) {
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private BigDecimal getBigDecimalValue(String key, Object value, BigDecimal defaultValue) {
        if (value == null) return defaultValue;
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private long getLongValue(String key, Object value, long defaultValue) {
        if (value == null) return defaultValue;
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // ========== 关联数据处理 ==========
    private Integer handleMallCategory(String categoryName) {
        if (StrUtil.isBlank(categoryName)) throw new CommonException("平台类目不能为空");
        GoodsCategory category = categoryMapper.selectOne(new LambdaQueryWrapper<GoodsCategory>()
                .eq(GoodsCategory::getCategoryName, categoryName)
                .eq(GoodsCategory::getSiteId, 0));
        if (category != null) return category.getCategoryId();
        throw new CommonException("未在本地找到平台类目");
    }

    private Integer[] handleCategory(String categoryNames, Integer siteId) {
        if (StrUtil.isBlank(categoryNames)) return new Integer[0];
        List<String> nameList = Arrays.asList(categoryNames.split(";"));
        List<GoodsCategory> categories = categoryMapper.selectList(new LambdaQueryWrapper<GoodsCategory>()
                .eq(GoodsCategory::getSiteId, siteId)
                .in(GoodsCategory::getCategoryName, nameList));
        return categories.stream().map(GoodsCategory::getCategoryId).toArray(Integer[]::new);
    }

    private Integer handleBrand(String brandName, Integer siteId) {
        if (StrUtil.isBlank(brandName)) return 0;
        GoodsBrand brand = brandMapper.selectOne(new LambdaQueryWrapper<GoodsBrand>()
                .in(GoodsBrand::getSiteId, Arrays.asList(0, siteId))
                .eq(GoodsBrand::getBrandName, brandName)
                .eq(GoodsBrand::getStatus, 1));
        return brand != null ? brand.getBrandId() : 0;
    }

    private Integer[] handleLabels(String labelNames) {
        if (StrUtil.isBlank(labelNames)) return new Integer[0];
        List<String> nameList = Arrays.asList(labelNames.split(";"));
        List<MallGoodsLabel> labels = labelMapper.selectList(new LambdaQueryWrapper<MallGoodsLabel>()
                .in(MallGoodsLabel::getLabelName, nameList)
                .eq(MallGoodsLabel::getStatus, 1));
        return labels.stream().map(MallGoodsLabel::getLabelId).toArray(Integer[]::new);
    }

    private Integer[] handleServices(String serviceNames) {
        if (StrUtil.isBlank(serviceNames)) return new Integer[0];
        List<String> nameList = Arrays.asList(serviceNames.split(";"));
        List<GoodsService> services = serviceMapper.selectList(new LambdaQueryWrapper<GoodsService>()
                .in(GoodsService::getServiceName, nameList));
        return services.stream().map(GoodsService::getServiceId).toArray(Integer[]::new);
    }

    private String[] handleDeliveryType(String deliveryType) {
        if (StrUtil.isBlank(deliveryType)) return new String[0];
        return deliveryType.split(";");
    }

    private Integer handleSupplier(String supplierName) {
        if (StrUtil.isBlank(supplierName)) return 0;
        Site site = siteMapper.selectOne(new LambdaQueryWrapper<Site>()
                .eq(Site::getSiteName, supplierName)
                .eq(Site::getAppType, "site")
                .eq(Site::getStatus, 1)
                .like(Site::getApp, "%\"supply\"%"));
        return site != null ? site.getSiteId() : 0;
    }

    // ========== 商品创建 ==========
    @Transactional(rollbackFor = Exception.class)
    private Integer createGoods(GoodsPartyParam goodsCreateData) {
        String goodsType = goodsCreateData.getGoodsType();
        try {
            if (GoodsTypeEnum.VIRTUAL.getType().equals(goodsType)) {
                MallVirtualGoodsParam param = BeanUtil.copyProperties(goodsCreateData, MallVirtualGoodsParam.class);
                Map<String, MallVirtualGoodsParam.SkuData> goodsSkuData = new HashMap<>();
                for (var item : goodsCreateData.getGoodsSkuData().entrySet()) {
                    MallVirtualGoodsParam.SkuData skuData = BeanUtil.copyProperties(item.getValue(), MallVirtualGoodsParam.SkuData.class);
                    goodsSkuData.put(item.getKey(), skuData);
                }
                param.setGoodsSkuData(goodsSkuData);
                return virtualGoodsService.add(param);
            } else if (GoodsTypeEnum.REAL.getType().equals(goodsType)) {
                MallGoodsParam param = BeanUtil.copyProperties(goodsCreateData, MallGoodsParam.class);
                Map<String, MallGoodsParam.SkuData> goodsSkuData = new HashMap<>();
                for (var item : goodsCreateData.getGoodsSkuData().entrySet()) {
                    MallGoodsParam.SkuData skuData = BeanUtil.copyProperties(item.getValue(), MallGoodsParam.SkuData.class);
                    goodsSkuData.put(item.getKey(), skuData);
                }
                param.setGoodsSkuData(goodsSkuData);
                return goodsService.add(param);
            } else {
                throw new CommonException("不支持的商品类型：" + goodsType);
            }
        } catch (Exception e) {
            throw new CommonException("商品创建失败：" + e.getMessage());
        }
    }

    // ========== 文件相关方法（简化） ==========
    private boolean checkFile(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.connect();
            int responseCode = conn.getResponseCode();
            return responseCode == 200 || responseCode == 206;
        } catch (IOException e) {
            log.error("检查文件失败", e);
            return false;
        }
    }

    private String remoteFilePullToLocal(String fileUrl, String localPath) throws IOException {
        File localFile = new File(localPath);
        File parent = localFile.getParentFile();
        if (!parent.exists()) parent.mkdirs();
        HttpUtil.downloadFile(fileUrl, localFile);
        return localPath;
    }


    // ========== 商品导入字段映射字典 ==========

    /**
     * 商品导入字段映射字典
     */
    public static class ImportCollectDict {

        // ==================== 实物商品导入映射 ====================

        /**
         * 实物商品导入字段映射（完整列表）
         *
         * @return 字段映射列表
         */
        public static List<Map<String, String>> importRealMap() {
            return importRealMap(null);
        }

        /**
         * 实物商品导入字段映射（根据 key 获取单项）
         *
         * @param key 字段键名
         * @return 单项映射或 null，若 key 为空则返回完整列表
         */
        public static List<Map<String, String>> importRealMap(String key) {
            List<Map<String, String>> list = new ArrayList<>();
            list.add(createEntry("goods_id", "商品编号", "A"));
            list.add(createEntry("sku_id", "规格编号", "B"));
            list.add(createEntry("goods_name", "商品名称", "C"));
            list.add(createEntry("sku_name", "规格名称", "D"));
            list.add(createEntry("sku_no", "规格编码", "E"));
            list.add(createEntry("sku_spec_format", "规格值", "F"));
            list.add(createEntry("goods_spec_format", "规格项", "G"));
            list.add(createEntry("goods_image", "商品图片", "H"));
            list.add(createEntry("sku_image", "规格图片", "I"));
            list.add(createEntry("sub_title", "副标题", "J"));
            list.add(createEntry("goods_mall_category", "平台类目", "K"));
            list.add(createEntry("goods_category", "商品分类", "L"));
            list.add(createEntry("goods_desc", "商品介绍", "M"));
            list.add(createEntry("brand_name", "商品品牌", "N"));
            list.add(createEntry("price", "价格", "O"));
            list.add(createEntry("stock", "库存", "P"));
            list.add(createEntry("unit", "单位", "Q"));
            list.add(createEntry("status", "商品状态", "R"));
            list.add(createEntry("delivery_type", "配送方式", "S"));
            list.add(createEntry("is_free_shipping", "是否免邮", "T"));
            list.add(createEntry("fee_type", "运费设置", "U"));
            list.add(createEntry("delivery_template_id", "运费模板", "V"));
            list.add(createEntry("delivery_money", "固定运费", "W"));
            list.add(createEntry("label_names", "商品标签", "X"));
            list.add(createEntry("service_names", "商品服务", "Y"));
            list.add(createEntry("attr_format", "商品参数", "Z"));
            list.add(createEntry("supplier_name", "供货商名称", "AA"));

            // 如果 key 非空，返回匹配的单条记录
            if (key != null && !key.isEmpty()) {
                return Collections.singletonList(list.stream()
                        .filter(entry -> entry.get("key").equals(key))
                        .findFirst()
                        .orElse(null));
            }
            return list;
        }

        // ==================== 虚拟商品导入映射 ====================

        /**
         * 虚拟商品导入字段映射（完整列表）
         *
         * @return 字段映射列表
         */
        public static List<Map<String, String>> importVirtualMap() {
            return importVirtualMap(null);
        }

        /**
         * 虚拟商品导入字段映射（根据 key 获取单项）
         *
         * @param key 字段键名
         * @return 单项映射或 null，若 key 为空则返回完整列表
         */
        public static List<Map<String, String>> importVirtualMap(String key) {
            List<Map<String, String>> list = new ArrayList<>();
            list.add(createEntry("goods_id", "商品编号", "A"));
            list.add(createEntry("sku_id", "规格编号", "B"));
            list.add(createEntry("goods_name", "商品名称", "C"));
            list.add(createEntry("sku_name", "规格名称", "D"));
            list.add(createEntry("sku_no", "规格编码", "E"));
            list.add(createEntry("sku_spec_format", "规格值", "F"));
            list.add(createEntry("goods_spec_format", "规格项", "G"));
            list.add(createEntry("goods_image", "商品图片", "H"));
            list.add(createEntry("sku_image", "规格图片", "I"));
            list.add(createEntry("sub_title", "副标题", "J"));
            list.add(createEntry("goods_mall_category", "平台类目", "K"));
            list.add(createEntry("goods_category", "商品分类", "L"));
            list.add(createEntry("goods_desc", "商品介绍", "M"));
            list.add(createEntry("brand_name", "商品品牌", "N"));
            list.add(createEntry("price", "价格", "O"));
            list.add(createEntry("stock", "库存", "P"));
            list.add(createEntry("unit", "单位", "Q"));
            list.add(createEntry("status", "商品状态", "R"));
            list.add(createEntry("label_names", "商品标签", "S"));
            list.add(createEntry("service_names", "商品服务", "T"));
            list.add(createEntry("attr_format", "商品参数", "U"));
            list.add(createEntry("virtual_delivery_type", "虚拟商品发货方式", "V"));
            list.add(createEntry("virtual_receive_type", "虚拟商品收货方式", "W"));
            list.add(createEntry("virtual_delivery_content", "虚拟商品发货内容", "X"));
            list.add(createEntry("virtual_verify_type", "虚拟商品核销有效期类型", "Y"));
            list.add(createEntry("virtual_indate", "虚拟到期时间", "Z"));
            list.add(createEntry("virtual_refund", "退款设置类型", "AA"));
            list.add(createEntry("virtual_refund_day", "退款有效天数", "AB"));
            list.add(createEntry("virtual_verify_mobile", "虚拟商品是否核销手机号", "AC"));
            list.add(createEntry("supplier_name", "供货商名称", "AD"));

            if (key != null && !key.isEmpty()) {
                return Collections.singletonList(list.stream()
                        .filter(entry -> entry.get("key").equals(key))
                        .findFirst()
                        .orElse(null));
            }
            return list;
        }

        // ==================== 辅助方法 ====================

        public static List<Map<String, String>> collectMap() {
            return new ArrayList<>();
        }

        // =================== 验证类方法 ===================
        public static Map<String, String> getRequiredField() {
            Map<String, String> map = new HashMap<>();
            map.put("goods_id", "商品ID");
            map.put("sku_id", "规格ID");
            map.put("goods_name", "商品名称");
            map.put("goods_type", "商品类型");
            map.put("goods_image", "商品图片");
            map.put("goods_mall_category", "平台类目");
            map.put("status", "商品状态");
            map.put("spec_type", "规格类型");
            map.put("price", "售价");
            map.put("delivery_type", "支持配送方式");
            map.put("goods_desc", "商品介绍");
            map.put("virtual_delivery_type", "虚拟商品发货方式");
            return map;
        }

        // ==================== 辅助方法 ====================

        private static Map<String, String> createEntry(String key, String name, String sheetColumn) {
            Map<String, String> entry = new HashMap<>();
            entry.put("key", key);
            entry.put("name", name);
            entry.put("sheetColumn", sheetColumn);
            return entry;
        }

        /**
         * 根据 key 获取字段名称（实物商品）
         *
         * @param key 字段键名
         * @return 字段名称，未找到返回 null
         */
        public static String getRealNameByKey(String key) {
            Map<String, String> entry = importRealMap(key).get(0);
            return entry != null ? entry.get("name") : null;
        }

        /**
         * 根据 key 获取 Excel 列号（实物商品）
         *
         * @param key 字段键名
         * @return 列号字符串（如 "A"），未找到返回 null
         */
        public static String getRealSheetColumnByKey(String key) {
            Map<String, String> entry = importRealMap(key).get(0);
            return entry != null ? entry.get("sheetColumn") : null;
        }

        /**
         * 根据 key 获取字段名称（虚拟商品）
         *
         * @param key 字段键名
         * @return 字段名称，未找到返回 null
         */
        public static String getVirtualNameByKey(String key) {
            Map<String, String> entry = importVirtualMap(key).get(0);
            return entry != null ? entry.get("name") : null;
        }

        /**
         * 根据 key 获取 Excel 列号（虚拟商品）
         *
         * @param key 字段键名
         * @return 列号字符串（如 "A"），未找到返回 null
         */
        public static String getVirtualSheetColumnByKey(String key) {
            Map<String, String> entry = importVirtualMap(key).get(0);
            return entry != null ? entry.get("sheetColumn") : null;
        }
    }
}
