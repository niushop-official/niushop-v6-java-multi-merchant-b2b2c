package com.niu.mall.service.core.goods.param;

import cn.hutool.json.JSONArray;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class GoodsPartyParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String goodsName;

    private String skuName;

    private String subTitle;

    private String goodsType;

    private String goodsImage;

    private String goodsVideo = "";

    private Integer[] goodsCategory;

    private Integer goodsMallCategory;

    private String goodsDesc;

    private Integer brandId = 0;

    private Integer[] labelIds;

    private Integer[] serviceIds;

    private Integer supplierId = 0;

    private Integer status;

    private Integer shopSort = 0;

    private JSONArray attrIds = new JSONArray();

    private JSONArray shopAttrIds = new JSONArray();

    private JSONArray attrFormat = new JSONArray();

    private Integer isGift = 0;

    private String specType;

    private BigDecimal price = new BigDecimal(0);

    private BigDecimal marketPrice = new BigDecimal(0);

    private BigDecimal costPrice = new BigDecimal(0);

    private BigDecimal weight = new BigDecimal(0);

    private BigDecimal volume = new BigDecimal(0);

    private Integer stock = 0;

    private String skuNo;

    private String unit = "件";

    private Integer isLimit = 0;

    private Integer limitType = 1;

    private Integer maxBuy = 0;

    private Integer minBuy = 0;

    private Map<String, SkuData> goodsSkuData;
    private Integer sourceSkuId;

    private JSONArray skuSpecFormat;

    private JSONArray goodsSpecFormat;

    private String[] deliveryType;

    private Integer isFreeShipping = 0;

    private String feeType = "";

    private BigDecimal deliveryMoney = new BigDecimal(0);

    private Integer deliveryTemplateId = 0;

    private String memberDiscount;

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

    @Data
    public static class SkuData {
        private Integer skuId;

        private String skuNo;

        private String skuImage;

        private BigDecimal price = new BigDecimal(0);

        private BigDecimal marketPrice = new BigDecimal(0);

        private BigDecimal costPrice = new BigDecimal(0);

        private BigDecimal weight = new BigDecimal(0);

        private BigDecimal volume = new BigDecimal(0);

        private Integer stock;

        private JSONArray skuSpec;

        private String specName;

        private Integer isDefault = 0;

        private Integer isSell = 1;

        private Integer sourceType;
        private Integer sourceGoodsId;
        private Integer sourceSkuId;

    }

    /**
     * 虚拟商品是否核销手机号，1.开启，0.关闭
     */
    private Integer virtualVerifyMobile = 0;

    /**
     * 站点id
     */
    private Integer siteId;

    private Integer accessNum; // 访问次数(浏览量)
    private Integer cartNum; // 加入购物车数量
    private Integer payNum; // 支付件数
    private BigDecimal payMoney; // 支付总金额
    private Integer collectNum; // 收藏数量
    private Integer evaluateNum; // 评论数量
    private Integer refundNum; // 退款件数
    private BigDecimal refundMoney; // 退款总额

    /**
     * 商品封面
     */
    private String goodsCover;

    /**
     * 销量
     */
    private Integer saleNum;

    /**
     * 虚拟销量
     */
    private Integer virtualSaleNum;
    /**
     * 待审核状态（审核之前状态）
     */
    private Integer auditStatus;

    /**
     * 审核原因（拒绝或者强制下架）
     */
    private String auditReason;

    /**
     * 自动上架开关，1开启 0关闭
     */
    private Integer timerOnStatus;

    /**
     * 自动上架时间
     */
    private Integer timerOnTime;

    /**
     * 自动下架开关，1开启 0关闭
     */
    private Integer timerOffStatus;

    /**
     * 自动下架时间
     */
    private Integer timerOffTime;

    /**
     * 支持的支付方式
     */
    private String supportPayType;

    /**
     * 是否允许退款
     */
    private Integer isEnableRefund;

    private Integer isEnablePointDeduct;

    /**
     * 商品来源 0本地 1导入 2采集
     */
    private Integer sourceType;

    /**
     * 导入/采集商品关联goods_id/spu_id
     */
    private String sourceGoodsId;

    /**
     * 采集商品平台标识
     */
    private String sourceFlatform;

    /**
     * 采集商品第三方标识
     */
    private String sourceParty;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 虚拟商品是否自动发货
     */
    private Integer virtualAutoDelivery;

    /**
     * 虚拟商品收货方式，auto：自动收货，artificial：买家确认收货，verify：到店核销
     */
    private String virtualReceiveType;

    /**
     * 虚拟商品核销有效期类型，0：不限，1：购买后几日有效，2：指定过期日期
     */
    private Integer virtualVerifyType;

    /**
     * 虚拟到期时间
     */
    private Integer virtualIndate;

    /**
     * 虚拟商品发货方式，auto：自动发货，manual：手动发货，verify：到店核销
     */
    private String virtualDeliveryType;

    /**
     * 虚拟商品发货内容
     */
    private String virtualDeliveryContent;

    /**
     * 虚拟商品退款，0：不允许退款，1：付款成功后指定天数内有效，2：过期自动退款（核销有效期为1、2时生效）
     */
    private Integer virtualRefund;

    /**
     * 付款成功后几天内可退款，为0不限制
     */
    private Integer virtualRefundDay;

    /**
     * 供应商id
     */
    private Integer supplyId;

    /**
     * 供应商id
     */
    private Integer supplyGoodsId;

    /**
     * 是否参与限时折扣
     */
    private Integer isDiscount;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 修改时间
     */
    private Long updateTime;

    /**
     * 删除时间
     */
    private Long deleteTime;


    /**
     * 供应商是否有更新
     */
    private Integer isSupplyUpdate;

    /**
     * 申请审核时间
     */
    private Long applyTime;

}
