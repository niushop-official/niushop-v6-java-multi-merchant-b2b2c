package com.niu.mall.service.api.shop;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import java.io.Serializable;
import lombok.Data;
import java.math.BigDecimal;

/**
* 商品表详情
*/
@Data
public class MallGoodsInfoVo implements Serializable {

private static final long serialVersionUID = 1L;


    /** 访问次数(浏览量) */
    private Integer accessNum;


    /** 商品参数内容，json格式 */
    private String attrFormat;


    /** 审核原因（拒绝或者强制下架） */
    private String auditReason;


    /** 待审核状态（审核之前状态） */
    private Integer auditStatus;


    /** 商品品牌id */
    private Integer brandId;


    /** 加入购物车数量 */
    private Integer cartNum;


    /** 收藏数量 */
    private Integer collectNum;


    /** 创建时间 */
    private Long createTime;


    /** 删除时间 */
    private Long deleteTime;


    /** 固定运费 */
    private BigDecimal deliveryMoney;


    /** 运费模板 */
    private Integer deliveryTemplateId;


    /** 支持的配送方式 */
    private String deliveryType;


    /** 评论数量 */
    private Integer evaluateNum;


    /** 运费设置，选择模板：template，固定运费：fixed */
    private String feeType;


    /** 商品分类 */
    private String goodsCategory;


    /** 商品封面 */
    private String goodsCover;


    /** 商品介绍 */
    private String goodsDesc;


    /** 商品id */
    private Integer goodsId;


    /** 商品图片 */
    private String goodsImage;


    /** 平台分类 */
    private Integer goodsMallCategory;


    /** 商品名称 */
    private String goodsName;


    /** 商品类型 */
    private String goodsType;


    /** 是否参与限时折扣 */
    private Integer isDiscount;


    /** 是否免邮 */
    private Integer isFreeShipping;


    /** 商品是否限购(0:否 1:是) */
    private Integer isLimit;


    /** 标签组 */
    private String labelIds;


    /** 限购类型，1：单次限购，2：单人限购 */
    private Integer limitType;


    /** 平台商品参数id */
    private Integer mallAttrId;


    /** 限购数 */
    private Integer maxBuy;


    /** 会员等级折扣，不参与：空，会员折扣：discount，指定会员价：fixed_price */
    private String memberDiscount;


    /** 起购数 */
    private Integer minBuy;


    /** 支付总金额 */
    private BigDecimal payMoney;


    /** 支付件数 */
    private Integer payNum;


    /** 退款总额 */
    private BigDecimal refundMoney;


    /** 退款件数 */
    private Integer refundNum;


    /** 销量 */
    private Integer saleNum;


    /** 商品服务 */
    private String serviceIds;


    /** 店铺商品参数id */
    private Integer shopAttrId;


    /** 商品排序 */
    private Integer shopSort;


    /** 站点id */
    private Integer siteId;


    /** 排序 */
    private Integer sort;


    /** 商品状态（1.正常0下架-1待审核-2审核拒绝-3强制下架） */
    private Integer status;


    /** 商品库存（总和） */
    private Integer stock;


    /** 副标题 */
    private String subTitle;


    /** 供应商id */
    private Integer supplierId;


    /** 关联供应商商品id */
    private Integer supplyGoodsId;


    /** 供应商 */
    private Integer supplyId;


    /** 单位 */
    private String unit;


    /** 修改时间 */
    private Long updateTime;


    /** 虚拟商品是否自动发货 */
    private Integer virtualAutoDelivery;


    /** 虚拟到期时间 */
    private Integer virtualIndate;


    /** 虚拟商品收货方式，auto：自动收货，artificial：买家确认收货，verify：到店核销 */
    private String virtualReceiveType;


    /** 虚拟销量 */
    private Integer virtualSaleNum;


    /** 虚拟商品核销有效期类型，0：不限，1：购买后几日有效，2：指定过期日期 */
    private Integer virtualVerifyType;

}