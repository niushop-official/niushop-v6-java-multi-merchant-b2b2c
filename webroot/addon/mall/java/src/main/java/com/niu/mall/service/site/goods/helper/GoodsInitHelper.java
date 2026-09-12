package com.niu.mall.service.site.goods.helper;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.delivery.DeliveryShippingTemplate;
import com.niu.core.entity.diy.DiyPage;
import com.niu.core.entity.diy_form.DiyForm;
import com.niu.core.entity.goods.GoodsAttr;
import com.niu.core.entity.goods.GoodsBrand;
import com.niu.core.entity.goods.GoodsCategory;
import com.niu.core.mapper.delivery.DeliveryShippingTemplateMapper;
import com.niu.core.mapper.diy.DiyPageMapper;
import com.niu.core.mapper.diy_form.DiyFormMapper;
import com.niu.core.mapper.goods.GoodsAttrMapper;
import com.niu.core.mapper.goods.GoodsBrandMapper;
import com.niu.core.mapper.goods.GoodsCategoryMapper;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.entity.goods.MallGoods;
import com.niu.core.entity.goods.MallGoodsSku;
import com.niu.mall.entity.goods.MallGoodsSpec;
import com.niu.mall.enums.goods.GoodsFieldEnum;
import com.niu.mall.event.goods.SupplyGoodsSyncEventDefiner;
import com.niu.core.mapper.goods.MallGoodsSkuMapper;
import com.niu.mall.mapper.goods.MallGoodsSpecMapper;
import com.niu.mall.service.site.goods.vo.MallVirtualGoodsInitVo;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品编辑 init 数据组装公共逻辑
 * <p>对齐 PHP：GoodsService::getInit、VirtualGoodsService::getInit</p>
 */
@Component
public class GoodsInitHelper {

    /** SKU 查询字段（与 PHP sku_field 一致） */
    private static final String SKU_SELECT = "sku_id,sku_name,sku_image,sku_no,goods_id,sku_spec_format,price,market_price,cost_price,member_price,min_price,max_price,stock,weight,volume,is_default,is_sell";
    /** 规格项查询字段 */
    private static final String SPEC_SELECT = "spec_id,goods_id,spec_name,spec_values";
    /** 品牌查询字段 */
    private static final String BRAND_SELECT = "brand_id,brand_name,logo,`desc`,sort,create_time";

    @Resource
    private GoodsCategoryMapper goodsCategoryMapper;
    @Resource
    private GoodsBrandMapper goodsBrandMapper;
    @Resource
    private GoodsAttrMapper goodsAttrMapper;
    @Resource
    private DiyFormMapper diyFormMapper;
    @Resource
    private DiyPageMapper diyPageMapper;
    @Resource
    private DeliveryShippingTemplateMapper deliveryShippingTemplateMapper;
    @Resource
    private MallGoodsSkuMapper mallGoodsSkuMapper;
    @Resource
    private MallGoodsSpecMapper mallGoodsSpecMapper;
    @Resource
    private ICoreAddonService coreAddonService;

    /**
     * 供货商 init 同步结果（含禁用编辑字段与覆盖后的展示字段）
     */
    @Data
    public static class SupplyInitResult {
        /** 是否展示供货商同步设置 */
        private int supplySyncShow = 0;
        /** 禁止编辑字段 */
        private String[] disableEditField = new String[]{};
        /** 禁止编辑区域 */
        private String[] disableEditArea = new String[]{};
        /** 供货商同步后的商品名称 */
        private String goodsName;
        private String subTitle;
        private String goodsCover;
        private String goodsImage;
        private String goodsVideo;
        private String goodsDesc;
    }

    /**
     * 检查插件是否已安装
     */
    public boolean checkAddon(String key) {
        Map<String, InstallAddonListVo> addonListVoMap = coreAddonService.getInstallAddonList();
        return addonListVoMap.get(key) != null;
    }

    /**
     * 构建供货商商品 init 同步数据（事件 GoodsSync + 禁用编辑字段）
     */
    public SupplyInitResult buildSupplyInit(MallGoods model) {
        SupplyInitResult result = new SupplyInitResult();
        if (!checkAddon("supply") || model.getSupplyGoodsId() == null || model.getSupplyGoodsId() <= 0) {
            return result;
        }
        result.setSupplySyncShow(1);
        // 开启同步时，拉取供货商商品展示字段
        if (Integer.valueOf(1).equals(model.getSupplySync())) {
            SupplyGoodsSyncEventDefiner.SupplyGoodsSyncEvent event = new SupplyGoodsSyncEventDefiner.SupplyGoodsSyncEvent();
            event.setGoodsId(model.getSupplyGoodsId());
            List<SupplyGoodsSyncEventDefiner.SupplyGoodsSyncResult> supplyData = EventAndSubscribeOfPublisher.publishAndCallback(event);
            for (SupplyGoodsSyncEventDefiner.SupplyGoodsSyncResult item : supplyData) {
                if (item == null) continue;
                if (ObjectUtil.isNotEmpty(item.getGoodsName())) result.setGoodsName(item.getGoodsName());
                if (ObjectUtil.isNotEmpty(item.getSubTitle())) result.setSubTitle(item.getSubTitle());
                if (ObjectUtil.isNotEmpty(item.getGoodsCover())) result.setGoodsCover(item.getGoodsCover());
                if (ObjectUtil.isNotEmpty(item.getGoodsImage())) result.setGoodsImage(item.getGoodsImage());
                if (ObjectUtil.isNotEmpty(item.getGoodsVideo())) result.setGoodsVideo(item.getGoodsVideo());
                if (ObjectUtil.isNotEmpty(item.getGoodsDesc())) result.setGoodsDesc(item.getGoodsDesc());
            }
        }
        // 供货商商品禁止编辑字段（开启同步时合并 ENABLE_SYNC_FIELD）
        result.setDisableEditField(model.getSupplySync() != null && model.getSupplySync() == 1
                ? StringUtils.mergeArrays(GoodsFieldEnum.DISABLE_EDIT_FIELD.getValue(), GoodsFieldEnum.ENABLE_SYNC_FIELD.getValue())
                : GoodsFieldEnum.DISABLE_EDIT_FIELD.getValue());
        result.setDisableEditArea(GoodsFieldEnum.DISABLE_EDIT_AREA.getValue());
        return result;
    }

    /**
     * 将供货商同步结果写入商品 init 展示字段
     */
    public void applySupplyToGoodsInfo(SupplyInitResult supply, GoodsInitMutable target) {
        target.setSupplySyncShow(supply.getSupplySyncShow());
        if (ObjectUtil.isNotEmpty(supply.getGoodsName())) target.setGoodsName(supply.getGoodsName());
        if (ObjectUtil.isNotEmpty(supply.getSubTitle())) target.setSubTitle(supply.getSubTitle());
        if (ObjectUtil.isNotEmpty(supply.getGoodsCover())) target.setGoodsCover(supply.getGoodsCover());
        if (ObjectUtil.isNotEmpty(supply.getGoodsImage())) target.setGoodsImage(supply.getGoodsImage());
        if (ObjectUtil.isNotEmpty(supply.getGoodsVideo())) target.setGoodsVideo(supply.getGoodsVideo());
        if (ObjectUtil.isNotEmpty(supply.getGoodsDesc())) target.setGoodsDesc(supply.getGoodsDesc());
    }

    /**
     * 过滤已删除的店铺商品分类 id
     */
    public Integer[] filterGoodsCategory(Integer[] categories) {
        if (ObjectUtil.isEmpty(categories)) {
            return new Integer[]{};
        }
        Integer siteId = RequestUtils.siteId();
        List<Integer> valid = new ArrayList<>();
        for (Integer categoryId : categories) {
            if (categoryId == null) continue;
            Long count = goodsCategoryMapper.selectCount(new QueryWrapper<GoodsCategory>()
                    .eq("category_id", categoryId).eq("site_id", siteId));
            if (count > 0) valid.add(categoryId);
        }
        return valid.toArray(new Integer[0]);
    }

    /**
     * 校验平台商品分类，已删除则返回空字符串
     */
    public Object normalizeGoodsMallCategory(Integer goodsMallCategory) {
        if (goodsMallCategory == null || goodsMallCategory <= 0) {
            return "";
        }
        Long count = goodsCategoryMapper.selectCount(new QueryWrapper<GoodsCategory>()
                .eq("category_id", goodsMallCategory).eq("site_id", RequestUtils.defaultSiteId()));
        return count > 0 ? goodsMallCategory : "";
    }

    /**
     * 处理品牌：不存在则 brand_id 置空
     */
    public void applyBrand(Integer brandId, BrandTarget target) {
        target.setBrandOption(new GoodsBrand());
        if (brandId == null || brandId <= 0) {
            target.setBrandId("");
            return;
        }
        GoodsBrand brand = goodsBrandMapper.selectOne(new QueryWrapper<GoodsBrand>()
                .select(BRAND_SELECT.split(","))
                .eq("brand_id", brandId));
        if (brand == null) {
            target.setBrandId("");
        } else {
            target.setBrandId(brandId);
            target.setBrandOption(brand);
        }
    }

    /**
     * JSON 数组字符串转 Integer 数组
     */
    public Integer[] parseIntIdArray(String json) {
        if (ObjectUtil.isEmpty(json) || "[]".equals(json.trim())) {
            return new Integer[]{};
        }
        try {
            return strArrToIntArr(Convert.toStrArray(JSONUtil.parseArray(json)));
        } catch (Exception e) {
            return new Integer[]{};
        }
    }

    /**
     * 解析支持的支付方式，元素转为 string
     */
    public List<String> parseSupportPayType(String json) {
        if (ObjectUtil.isEmpty(json) || "[]".equals(json.trim())) {
            return new ArrayList<>();
        }
        try {
            return JSONUtil.parseArray(json).stream().map(String::valueOf).collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 根据店铺参数 id 查询参数名称列表
     */
    public List<String> loadShopAttrNameArr(Integer[] shopAttrIds) {
        if (ObjectUtil.isEmpty(shopAttrIds)) {
            return new ArrayList<>();
        }
        return goodsAttrMapper.selectList(new QueryWrapper<GoodsAttr>()
                        .select("attr_name")
                        .in("attr_id", Arrays.asList(shopAttrIds)))
                .stream().map(GoodsAttr::getAttrName).collect(Collectors.toList());
    }

    /**
     * 校验万能表单 id，不存在则置空
     */
    public Object normalizeFormId(Integer formId) {
        if (formId == null || formId <= 0) {
            return "";
        }
        Long count = diyFormMapper.selectCount(new QueryWrapper<DiyForm>().eq("form_id", formId));
        return count > 0 ? formId : "";
    }

    /**
     * 查询万能表单页面标题
     */
    public String loadPageTitle(Object formId) {
        if (formId == null || "".equals(formId)) {
            return "";
        }
        DiyForm form = diyFormMapper.selectOne(new QueryWrapper<DiyForm>().eq("form_id", formId));
        return form != null ? ObjectUtil.defaultIfEmpty(form.getPageTitle(), "") : "";
    }

    /**
     * 校验自定义详情页 id，不存在则置空
     */
    public Object normalizeDiyDetailId(Integer diyDetailId) {
        if (diyDetailId == null || diyDetailId <= 0) {
            return "";
        }
        Long count = diyPageMapper.selectCount(new QueryWrapper<DiyPage>().eq("id", diyDetailId));
        return count > 0 ? diyDetailId : "";
    }

    /**
     * 填充运费模板 id 与名称（实物商品）
     */
    public void applyDeliveryTemplate(MallGoods model, DeliveryTarget target) {
        if (model.getDeliveryTemplateId() == null || model.getDeliveryTemplateId() <= 0) {
            target.setDeliveryTemplateId("");
            target.setDeliveryTemplateName("");
            return;
        }
        DeliveryShippingTemplate template = deliveryShippingTemplateMapper.selectOne(
                new QueryWrapper<DeliveryShippingTemplate>().eq("template_id", model.getDeliveryTemplateId()));
        target.setDeliveryTemplateId(model.getDeliveryTemplateId());
        target.setDeliveryTemplateName(template != null ? ObjectUtil.defaultIfEmpty(template.getTemplateName(), "") : "");
    }

    /**
     * 配送方式空值规范化
     */
    public String normalizeDeliveryType(String deliveryType) {
        if (ObjectUtil.isEmpty(deliveryType) || "[]".equals(deliveryType.trim())) {
            return "[]";
        }
        return deliveryType;
    }

    /**
     * 查询 SKU 列表（字段与排序对齐 PHP）
     */
    public List<MallGoodsSku> loadSkuList(Integer goodsId) {
        return mallGoodsSkuMapper.selectList(new QueryWrapper<MallGoodsSku>()
                .select(SKU_SELECT.split(","))
                .eq("goods_id", goodsId)
                .orderByAsc("sku_id"));
    }

    /**
     * 查询规格项列表
     */
    public List<MallGoodsSpec> loadSpecList(Integer goodsId) {
        return mallGoodsSpecMapper.selectList(new QueryWrapper<MallGoodsSpec>()
                .select(SPEC_SELECT.split(","))
                .eq("goods_id", goodsId)
                .orderByAsc("spec_id"));
    }

    /**
     * 解析商品参数 attr_format JSON
     */
    public List<MallVirtualGoodsInitVo.MallGoodsAttrVo> parseAttrFormat(String attrFormat) {
        if (ObjectUtil.isEmpty(attrFormat)) {
            return new ArrayList<>();
        }
        return new Gson().fromJson(attrFormat, new TypeToken<List<MallVirtualGoodsInitVo.MallGoodsAttrVo>>() {}.getType());
    }

    /**
     * 解析关联推荐商品 id 数组
     */
    public Integer[] parseRecommendGoodsIds(String recommendGoodsIds) {
        if (ObjectUtil.isEmpty(recommendGoodsIds) || "[]".equals(recommendGoodsIds.trim())) {
            return new Integer[]{};
        }
        try {
            return strArrToIntArr(Convert.toStrArray(JSONUtil.parseArray(recommendGoodsIds)));
        } catch (Exception e) {
            return new Integer[]{};
        }
    }

    /**
     * 秒级时间戳转 Long（供前端时间组件序列化）
     */
    public Long toLongTime(Integer time) {
        if (time == null || time <= 0) {
            return null;
        }
        return time.longValue();
    }

    private Integer[] strArrToIntArr(String[] arr) {
        Integer[] intArr = new Integer[arr.length];
        for (int i = 0; i < arr.length; i++) {
            intArr[i] = Integer.valueOf(arr[i]);
        }
        return intArr;
    }

    /** 供货商同步可覆盖的 init 展示字段 */
    public interface GoodsInitMutable {
        void setSupplySyncShow(Integer supplySyncShow);
        void setGoodsName(String goodsName);
        void setSubTitle(String subTitle);
        void setGoodsCover(String goodsCover);
        void setGoodsImage(String goodsImage);
        void setGoodsVideo(String goodsVideo);
        void setGoodsDesc(String goodsDesc);
    }

    /** 品牌 init 字段 */
    public interface BrandTarget {
        void setBrandId(Object brandId);
        void setBrandOption(GoodsBrand brandOption);
    }

    /** 运费模板 init 字段（实物商品） */
    public interface DeliveryTarget {
        void setDeliveryTemplateId(Object deliveryTemplateId);
        void setDeliveryTemplateName(String deliveryTemplateName);
    }
}
