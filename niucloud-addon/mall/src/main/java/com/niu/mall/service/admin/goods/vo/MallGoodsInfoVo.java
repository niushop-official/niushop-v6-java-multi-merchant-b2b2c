package com.niu.mall.service.admin.goods.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ShopGoods视图
 */
@Data
public class MallGoodsInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    // 按照提供的数据结构1比1还原
    private Integer auditStatus;
    private Integer brandId;
    private String createTime;
    private String deliveryMoney;
    private Integer deliveryTemplateId;
    private List<String> deliveryType = new ArrayList<>();
    private String feeType;
    private BrandInfo goodsBrand;  // 品牌信息
    private List<Integer> goodsCategory = new ArrayList<>();
    private List<CategoryName> goodsCategoryName;
    private String goodsCover;
    private String goodsDesc;
    private Integer goodsId;
    private String goodsImage;
    private List<LabelName> goodsLabelName = new ArrayList<>();
    private Integer goodsMallCategory;
    private String goodsMallCategoryName;
    private String goodsName;
    private List<ServiceName> goodsServiceName = new ArrayList<>();
    private String goodsType;
    private Integer isFreeShipping;
    private Integer isSupplyUpdate;
    private List<String> labelIds = new ArrayList<>();
    private Integer saleNum;
    private List<String> serviceIds = new ArrayList<>();
    private Integer siteId;
    private List<SkuList> skuList = new ArrayList<>();
    private List<SpecList> specList = new ArrayList<>();
    private Integer sort;
    private String specType;
    private Integer status;
    private Integer stock;
    private String subTitle;
    private Integer supplierId;
    private String unit;
    private String updateTime;
    private Integer virtualSaleNum;

    @Data
    public static class CategoryName {
        private Integer categoryId;
        private String categoryName;
    }

    @Data
    public static class LabelName {
        private Integer labelId;
        private String labelName;
        private String styleType;
        private String icon;
        private ColorJson colorJson;
    }

    @Data
    public static class ColorJson {
        private String textColor;
        private String bgColor;
        private String borderColor;
    }

    @Data
    public static class ServiceName {
        private Integer serviceId;
        private String serviceName;
    }

    @Data
    public static class BrandInfo {
        private Integer brandId;
        private String brandName;
    }

    @Data
    public static class SkuList {
        private String costPrice;
        private Integer goodsId;
        private Integer isDefault;
        private Integer isSell;
        private String marketPrice;
        private String maxPrice;
        private String minPrice;
        private String price;
        private String skuId;
        private String skuImage;
        private String skuName;
        private String skuNo;
        private String skuSpecFormat;
        private Integer stock;
        private String volume;
        private String weight;
    }

    @Data
    public static class SpecList {
        private Integer specId;
        private Integer goodsId;
        private String specName;
        private String specValues;
    }
}
