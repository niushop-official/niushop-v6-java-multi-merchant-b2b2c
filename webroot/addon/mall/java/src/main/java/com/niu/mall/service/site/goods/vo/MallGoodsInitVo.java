package com.niu.mall.service.site.goods.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.goods.GoodsBrand;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 实物商品添加/编辑 init 视图（对齐 PHP GoodsService::getInit）
 */
@Data
public class MallGoodsInitVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 商品信息 */
    private GoodsInfo goodsInfo;

    /** 插件信息 */
    private Addon addonShopSupplier;

    /** 禁止编辑区域 */
    private String[] disableEditArea = new String[]{};

    /** 禁止编辑字段（供货商商品场景由后端计算） */
    private String[] disableEditField = new String[]{};

    @Data
    public static class GoodsInfo {
        private Integer siteId;  // 站点id
        private Integer goodsId;  // 商品id
        private String goodsName;  // 商品名称
        private String goodsType;  // 商品类型
        private String subTitle;  // 副标题
        private String goodsCover;  // 商品封面
        private String goodsImage;  // 商品图片
        private String goodsVideo;  // 商品视频
        private Integer[] goodsCategory;  // 商品分类
        private Object goodsMallCategory;  // 平台商品分类（已删除时为空字符串）
        private String goodsDesc;  // 商品介绍
        private Object brandId;  // 商品品牌id
        private GoodsBrand brandOption;  // 商品品牌
        private Integer[] labelIds;  // 标签组
        private Integer[] serviceIds;  // 商品服务
        private Object supplierId;  // 供应商id（表单选择）
        private String unit;  // 单位
        private Integer stock;  // 商品库存（总和）
        private Integer isLimit = 0;  // 是否限购
        private Integer limitType = 1;  // 限购类型
        private Integer maxBuy = 0;  // 限购数
        private Integer minBuy = 0;  // 起购数
        private String status;  // 商品状态（1.正常0下架）
        private Integer sort;  // 排序
        private Integer shopSort;  // 店铺排序
        @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
        private String deliveryType;  // 支持的配送方式
        private Integer isFreeShipping;  // 是否免邮
        private String feeType;  // 运费设置，选择模板：template，固定运费：fixed
        private BigDecimal deliveryMoney;  // 固定运费
        private Object deliveryTemplateId;  // 运费模板id
        private String deliveryTemplateName;  // 运费模板名称
        private Object supplyId;  // 供货商站点id
        private Integer supplyGoodsId;  // 供货商商品id
        /**
         * 供应商设置同步，1.开启，0.关闭
         */
        private Integer supplySync;
        private Integer[] attrIds;  // 平台商品参数id
        private Integer[] shopAttrIds;  // 店铺商品参数id
        private List<String> shopAttrNameArr;  // 店铺商品参数名称
        private List<MallVirtualGoodsInitVo.MallGoodsAttrVo> attrFormat;  // 商品参数内容
        private String memberDiscount;  // 会员等级折扣，不参与：空，会员折扣：discount，指定会员价：fixed_price
        private String vipCardDiscount;  // 付费会员折扣
        private Object posterId;  // 海报id
        private Object formId;  // 万能表单id
        private String pageTitle;  // 万能表单页面标题
        private List<SkuInfo> skuList;  // sku列表
        private String specType = "single";  // 规格类型 single/multi
        private List<SpecInfo> specList;  // 规格项列表
        private Long activeGoodsCount;  // 参与营销活动数量
        private Integer isGift;  // 是否赠品
        private Integer supplySyncShow = 0;  // 是否展示供货商同步设置
        private Object diyDetailId;  // 自定义详情页id
        private Integer isSupplyUpdate;  // 供货商是否有更新
        private String pointDeductType;  // 积分抵扣类型 custom:自定义 site:继承店铺
        private BigDecimal pointDeductRate;  // 积分抵扣比例
        private String timerOnStatus;  // 定时上架开关
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long timerOnTime;  // 定时上架时间
        private String timerOffStatus;  // 定时下架开关
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long timerOffTime;  // 定时下架时间
        private List<String> supportPayType;  // 支持的支付方式
        private String isEnableRefund;  // 是否允许退款
        private String virtualReceiveType;  // 虚拟收货方式（实物 field 列表包含）
        private Integer virtualVerifyType;  // 虚拟核销有效期类型
        private Integer virtualIndate;  // 虚拟到期时间
        private Integer[] recommendGoodsIds;  // 关联推荐商品id
        private Integer auditStatus;  // 审核状态
        private String auditReason;  // 审核原因
    }

    @Data
    public static class MallGoodsAttrVo {
        private Object attr_child_value_id;
        private Object attr_child_value_name;
        private Long attr_value_id;
        private String attr_value_name;
        private String sort;
        private String type;
    }

    @Data
    public static class SkuInfo {
        private Integer skuId;  // 商品sku_id
        private Integer siteId;  // 站点id
        private String skuName;  // 商品sku名称
        private String skuImage;  // sku主图
        private String skuNo;  // 商品sku编码
        private Integer goodsId;  // 商品id
        private String skuSpecFormat;  // sku规格格式
        private BigDecimal price;  // sku单价
        private BigDecimal marketPrice;  // 划线价
        private BigDecimal salePrice;  // 实际卖价
        private BigDecimal costPrice;  // sku成本价
        /** 会员价，json格式：{"level_1":"10.00"}；空字符串序列化为 {} */
        @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
        private String memberPrice;
        @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
        private String svipPrice;  // json格式
        @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
        private String svipDiscount;  // json格式
        private Integer stock;  // 商品sku库存
        private BigDecimal weight;  // 重量（单位kg）
        private BigDecimal volume;  // 体积（单位立方米）
        private Integer isDefault;  // 是否默认
        private Integer isSell;  // 是否可售
        private BigDecimal maxPrice;  // 控价区间最大值
        private BigDecimal minPrice;  // 控价区间最小值
    }

    @Data
    public static class SpecInfo {
        private Integer specId;  // 规格id
        private Integer goodsId;  // 关联商品id
        private String specName;  // 规格项名称
        private String specValues;  // 规格值名称，多个逗号隔开
    }
}
