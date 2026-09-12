package com.niu.core.entity.goods;

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

    /**
     * 商品id
     */
    @TableId(value = "goods_id", type = IdType.AUTO)
    private Integer goodsId;

    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 商品名称
     */
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

    /**
     * 副标题
     */
    private String subTitle;

    /**
     * 商品封面
     */
    private String goodsCover;

    /**
     * 商品图片
     */
    private String goodsImage;

    /**
     * 商品分类
     */
    private String goodsCategory;

    /**
     * 平台分类
     */
    private Integer goodsMallCategory;

    /**
     * 商品介绍
     */
    private String goodsDesc;

    /**
     * 商品品牌id
     */
    private Integer brandId;

    /**
     * 标签组
     */
    private String labelIds;

    /**
     * 商品服务
     */
    private String serviceIds;

    /**
     * 单位
     */
    private String unit;

    /**
     * 商品库存（总和）
     */
    private Integer stock;

    /**
     * 销量
     */
    private Integer saleNum;

    /**
     * 虚拟销量
     */
    private Integer virtualSaleNum;

    /**
     * 商品售卖状态（v104 升级后拆分为双字段模型）
     * 取值：1=销售中 0=仓库中 -3=强制下架
     * 审核状态见 {@link #auditStatus}（GoodsAuditStatusEnum）
     */
    private Integer status;

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

    /**
     * 积分抵扣类型 custom:自定义 site:继承店铺
     */
    private String pointDeductType;

    /**
     * 积分抵扣比例
     */
    private BigDecimal pointDeductRate;

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
     * 支持的配送方式
     */
    private String deliveryType;

    /**
     * 是否免邮
     */
    private Integer isFreeShipping;

    /**
     * 运费设置，选择模板：template，固定运费：fixed
     */
    private String feeType;

    /**
     * 固定运费
     */
    private BigDecimal deliveryMoney;

    /**
     * 运费模板
     */
    private Integer deliveryTemplateId;

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
     * 供应商id（表单选择）
     */
    private Integer supplierId;

    /**
     * 供货商id（supply 插件链路）
     */
    private Integer supplyId;

    /**
     * 供应商id
     */
    private Integer supplyGoodsId;

    /**
     * 商品参数内容，json格式
     */
    private String attrFormat;

    /**
     * 是否参与限时折扣
     */
    private Integer isDiscount;

    /**
     * 会员等级折扣，不参与：空，会员折扣：discount，指定会员价：fixed_price
     */
    private String memberDiscount;

    /**
     * 付费会员卡折扣，不参与：空，会员折扣：discount，指定会员价：fixed_price
     */
    private String vipCardDiscount;

    /**
     * 万能表单id
     */
    private Integer formId;

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
     * 商品排序
     */
    private Integer shopSort;


    /**
     * 供应商设置同步，1.开启，0.关闭
     */
    private Integer supplySync;

    /**
     * 供应商是否有更新
     */
    private Integer isSupplyUpdate;

    /**
     * 申请审核时间
     */
    private Long applyTime;

    /**
     * 虚拟商品是否核销手机号，1.开启，0.关闭
     */
    private Integer virtualVerifyMobile;

    /**
     * 商品视频
     */
    private String goodsVideo;

    /**
     * 平台商品参数id，支持多个
     */
    private String attrIds;

    /**
     * 店铺商品参数id，支持多个
     */
    private String shopAttrIds;

    /**
     * 自定义详情
     */
    private Integer diyDetailId;

    /**
     * 关联推荐商品id集合（JSON数组）
     */
    private String recommendGoodsIds;

}