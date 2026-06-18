package com.niu.mall.service.site.goods.param;

import cn.hutool.json.JSONArray;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品参数
 */
@Data
public class MallVirtualGoodsParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "goods_name参数缺失")
    @NotEmpty(message = "商品名称不能为空")
    private String goodsName;

    @NotNull(message = "sub_title参数缺失")
    private String subTitle;

    @NotNull(message = "goods_type参数缺失")
    @NotEmpty(message = "商品类型不能为空")
    private String goodsType;

    @NotNull(message = "goods_image参数缺失")
    @NotNull(message = "商品图片不能为空")
    private String goodsImage;

    private String goodsVideo = "";

    @NotNull(message = "goods_category参数缺失")
    private Integer[] goodsCategory;

    @NotNull(message = "goods_desc参数缺失")
    @NotNull(message = "商品详情不能为空")
    private String goodsDesc;

    private Integer brandId = 0;

    @NotNull(message = "labelIds参数缺失")
    private Integer[] labelIds;

    @NotNull(message = "serviceIds参数缺失")
    private Integer[] serviceIds;

    private Integer supplierId = 0;

    @NotNull(message = "status参数缺失")
    private Integer status;

    private Integer shopSort = 0;

    private JSONArray attrIds = new JSONArray();

    private JSONArray shopAttrIds = new JSONArray();

    @NotNull(message = "attr_format参数缺失")
    private JSONArray attrFormat;

    private Integer isGift = 0;

    @NotNull(message = "spec_type参数缺失")
    private String specType;

    private BigDecimal price = new BigDecimal(0);

    private BigDecimal marketPrice = new BigDecimal(0);

    private BigDecimal costPrice = new BigDecimal(0);

    private Integer stock = 0;

    @NotNull(message = "sku_no参数缺失")
    private String skuNo;

    @NotNull(message = "unit参数缺失")
    private String unit = "件";

    private Integer isLimit = 0;

    private Integer limitType = 1;

    private Integer maxBuy = 0;

    private Integer minBuy = 0;

    @NotNull(message = "goods_sku_data参数缺失")
    private Map<String, SkuData> goodsSkuData;

    @NotNull(message = "goods_spec_format参数缺失")
    private JSONArray goodsSpecFormat;

    private Integer virtualAutoDelivery = 0;

    private String virtualReceiveType = "artificial";

    private Integer virtualVerifyType = 0;

    private Integer virtualIndate = 0;

    /**
     * 虚拟商品是否核销手机号，1.开启，0.关闭
     */
    private Integer virtualVerifyMobile = 0;

    @NotNull(message = "member_discount参数缺失")
    private String memberDiscount;

    private Integer posterId = 0;

    private Integer formId = 0;

    /**
     * 供货商设置同步
     */
    private Integer supplySync = 0;

    /**
     * 商品平台分类
     */
    private Integer goodsMallCategory;

    /**
     * 自定义详情
     */
    private Integer diyDetailId;

    /**
     * 积分抵扣类型 custom:自定义 site:继承店铺
     */
    private String pointDeductType;

    /**
     * 积分抵扣比例
     */
    private BigDecimal pointDeductRate;

    @Data
    public static class SkuData {
        private Integer skuId;

        @NotNull(message = "sku_no参数缺失")
        private String skuNo;

        @NotNull(message = "sku_image参数缺失")
        private String skuImage;

        @NotNull(message = "price参数缺失")
        private BigDecimal price = new BigDecimal(0);

        private BigDecimal marketPrice = new BigDecimal(0);

        private BigDecimal costPrice = new BigDecimal(0);

        @NotNull(message = "stock参数缺失")
        private Integer stock;

        @NotNull(message = "sku_spec参数缺失")
        private JSONArray skuSpec;

        @NotNull(message = "spec_name参数缺失")
        private String specName;

        @NotNull(message = "is_default参数缺失")
        private Integer isDefault = 0;

        private Integer isSell = 1;
    }

    //	定时上架状态
    private Integer timerOnStatus;
    //定时上架时间
    @JsonDeserialize(using = BeanJsonSerializer.UnixMillisDeserializer.class)
    private Long timerOnTime;
    //定时下架状态
    private Integer timerOffStatus;
    //定时下架时间
    @JsonDeserialize(using = BeanJsonSerializer.UnixMillisDeserializer.class)
    private Long timerOffTime;
    //支持的支付方式
    private List<String> supportPayType;
    //是否允许退款
    private Integer isEnableRefund;
    //虚拟商品发货方式 auto/manual/verify
    private String virtualDeliveryType;
    //虚拟商品自动发货内容
    private String virtualDeliveryContent;
    //虚拟商品退款设置类型
    private Integer virtualRefund;
    //虚拟商品退款有效天数
    private Integer virtualRefundDay;
}
