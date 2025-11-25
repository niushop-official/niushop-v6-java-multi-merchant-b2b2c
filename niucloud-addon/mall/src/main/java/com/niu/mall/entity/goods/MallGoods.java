package com.niu.mall.entity.goods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品实体
 */
@Data
public class MallGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 商品id */
    @TableId(value="goods_id", type= IdType.AUTO)
    private Integer goodsId;

    /** 站点id */
    private Integer siteId;

    /** 商品名称 */
    private String goodsName;

    private String goodsType; // 商品类型(1.实物 2.虚拟)
    private Integer isLimit; // 商品是否限购(0:否 1:是)
    private Integer limitType; // 限购类型，1：单次限购，2：单人限购
    private Integer maxBuy; // 限购数
    private Integer minBuy; // 起购数
    private Integer accessNum; // 访问次数(浏览量)
    private Integer cartNum; // 加入购物车数量
    private Integer payNum; // 支付件数
    private BigDecimal payMoney; // 支付总金额
    private Integer collectNum; // 收藏数量
    private Integer evaluateNum; // 评论数量
    private Integer refundNum; // 退款件数
    private BigDecimal refundMoney; // 退款总额

    /** 副标题 */
    private String subTitle;

    /** 商品封面 */
    private String goodsCover;

    /** 商品图片 */
    private String goodsImage;

    /** 商品分类 */
    private String goodsCategory;

    /** 平台分类 */
    private Integer goodsMallCategory;

    /** 商品介绍 */
    private String goodsDesc;

    /** 商品品牌id */
    private Integer brandId;

    /** 标签组 */
    private String labelIds;

    /** 商品服务 */
    private String serviceIds;

    /** 单位 */
    private String unit;

    /** 商品库存（总和） */
    private Integer stock;

    /** 销量 */
    private Integer saleNum;

    /** 虚拟销量 */
    private Integer virtualSaleNum;

    /** 商品状态（1.正常0下架-1待审核-2审核拒绝-3强制下架） */
    private Integer status;

    /** 待审核状态（审核之前状态） */
    private Integer auditStatus;

    /** 审核原因（拒绝或者强制下架） */
    private String auditReason;

    /** 排序 */
    private Integer sort;

    /** 支持的配送方式 */
    private String deliveryType;

    /** 是否免邮 */
    private Integer isFreeShipping;

    /** 运费设置，选择模板：template，固定运费：fixed */
    private String feeType;

    /** 固定运费 */
    private BigDecimal deliveryMoney;

    /** 运费模板 */
    private Integer deliveryTemplateId;

    /** 虚拟商品是否自动发货 */
    private Integer virtualAutoDelivery;

    /** 虚拟商品收货方式，auto：自动收货，artificial：买家确认收货，verify：到店核销 */
    private String virtualReceiveType;

    /** 虚拟商品核销有效期类型，0：不限，1：购买后几日有效，2：指定过期日期 */
    private Integer virtualVerifyType;

    /** 虚拟到期时间 */
    private Integer virtualIndate;

    /** 供应商id */
    private Integer supplyId;

    /** 供应商id */
    private Integer supplyGoodsId;

    /** 商品参数内容，json格式 */
    private String attrFormat;

    /** 是否参与限时折扣 */
    private Integer isDiscount;

    /** 会员等级折扣，不参与：空，会员折扣：discount，指定会员价：fixed_price */
    private String memberDiscount;

    /** 万能表单id */
    private Integer formId;

    /** 创建时间 */
    private Long createTime;

    /** 修改时间 */
    private Long updateTime;

    /** 删除时间 */
    private Long deleteTime;

    /** 商品排序 */
    private Integer shopSort;


    /** 供应商设置同步，1.开启，0.关闭 */
    private Integer supplySync;

    /** 供应商是否有更新 */
    private Integer isSupplyUpdate;

    /** 申请审核时间 */
    private Long applyTime;

    /** 虚拟商品是否核销手机号，1.开启，0.关闭 */
    private Integer virtualVerifyMobile;

    /** 商品视频 */
    private String goodsVideo;

    /** 平台商品参数id，支持多个 */
    private String attrIds;

    /** 店铺商品参数id，支持多个 */
    private String shopAttrIds;

    /** 自定义详情 */
    private Integer diyDetailId;

}