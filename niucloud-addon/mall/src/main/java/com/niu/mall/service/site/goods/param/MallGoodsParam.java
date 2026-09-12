package com.niu.mall.service.site.goods.param;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
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
public class MallGoodsParam implements Serializable {

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

    @NotNull(message = "goods_mall_category参数缺失")
    private Integer goodsMallCategory;

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
    private JSONArray attrFormat = new JSONArray();

    private Integer isGift = 0;

    @NotNull(message = "spec_type参数缺失")
    private String specType;

    private BigDecimal price = new BigDecimal(0);

    private BigDecimal marketPrice = new BigDecimal(0);

    private BigDecimal costPrice = new BigDecimal(0);

    private BigDecimal weight = new BigDecimal(0);

    private BigDecimal volume = new BigDecimal(0);

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

    @NotNull(message = "delivery_type参数缺失")
    private String[] deliveryType;

    private Integer isFreeShipping = 0;

    @NotNull(message = "fee_type参数缺失")
    private String feeType = "";

    private BigDecimal deliveryMoney = new BigDecimal(0);

    private Integer deliveryTemplateId = 0;

    @NotNull(message = "member_discount参数缺失")
    private String memberDiscount;

    /**
     * 付费会员等级折扣，不参与：空，付费会员折扣：discount，指定付费会员价：fixed_price
     */
    private String vipCardDiscount = "";

    /**
     * 单规格指定会员价，json：{"level_1":"10.00"}（member_discount=fixed_price 时有效）
     */
    private JSONObject memberPrice;

    private Integer posterId = 0;

    private Integer formId = 0;
    /**
     * 自定义详情
     */
    private Integer diyDetailId;

    /**
     * 供应商是否有更新
     */
    private Integer supplySync;

    /**
     * 积分折扣类型 site=默认设置(店铺统一设置) custom=单独定制
     */
    private String pointDeductType;
    /**
     * 积分折扣比例
     */
    private BigDecimal pointDeductRate;

    /**
     * 定时上架状态
     */
    private Integer timerOnStatus;
    /**
     * 定时上架时间
     */
    @JsonDeserialize(using = BeanJsonSerializer.UnixMillisDeserializer.class)
    private Long timerOnTime;
    /**
     * 定时下架状态
     */
    private Integer timerOffStatus;
    /**
     * 定时下架时间
     */
    @JsonDeserialize(using = BeanJsonSerializer.UnixMillisDeserializer.class)
    private Long timerOffTime;
    /**
     * 支持的支付方式
     */
    private List<String> supportPayType;
    /**
     * 是否允许退款
     */
    private Integer isEnableRefund;
    /**
     * 关联推荐商品id集合
     */
    private Integer[] recommendGoodsIds;

    /**
     * 商品来源 0本地 1导入 2采集
     */
    private Integer sourceType;
    /**
     * 导入/采集商品关联 goods_id/spu_id
     */
    private String sourceGoodsId;
    /**
     * 单规格导入/采集关联 sku_id
     */
    private String sourceSkuId;
    /**
     * 采集商品平台标识
     */
    private String sourceFlatform;
    /**
     * 采集商品第三方标识
     */
    private String sourceParty;

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

        private BigDecimal weight = new BigDecimal(0);

        private BigDecimal volume = new BigDecimal(0);

        @NotNull(message = "stock参数缺失")
        private Integer stock;

        @NotNull(message = "sku_spec参数缺失")
        private JSONArray skuSpec;

        @NotNull(message = "spec_name参数缺失")
        private String specName;

        @NotNull(message = "is_default参数缺失")
        private Integer isDefault = 0;

        private Integer isSell = 1;

        private String sourceGoodsId;
        private String sourceSkuId;
        private String sourceFlatform;
        private String sourceParty;

        /**
         * 多规格指定会员价，json：{"level_1":"10.00"}
         */
        private JSONObject memberPrice;
    }
}
