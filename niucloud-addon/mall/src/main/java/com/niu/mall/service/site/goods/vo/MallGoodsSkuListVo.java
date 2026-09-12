package com.niu.mall.service.site.goods.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.enums.goods.GoodsTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ShopGoodsSku列表视图
 */
@Data
public class MallGoodsSkuListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer skuId;  // 商品sku_id
    private Integer siteId;  // 站点id
    private String skuName;  // 商品sku名称
    private String skuImage;  // sku主图
    private String skuNo;  // 商品sku编码
    private Integer goodsId;  // 商品id
    private String skuSpecFormat;  // sku规格格式
    private BigDecimal price;  // sku单价
    private BigDecimal marketPrice;  // 划线价
    private BigDecimal salePrice;  // 实际卖价（有活动显示活动价，默认原价）
    private BigDecimal costPrice;  // sku成本价
    private Integer stock;  // 商品sku库存
    private BigDecimal weight;  // 重量（单位kg）
    private BigDecimal volume;  // 体积（单位立方米）
    private Integer saleNum;  // 销量
    private Integer isDefault;  // 是否默认
    private String memberPrice;  // 会员价，json格式，指定会员价，数据结构为：{"level_1":"10.00","level_2":"10.00"}
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String svipPrice;  // json格式
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String svipDiscount;  // json格式
    private GoodsInfo goods;

    @Data
    public static class GoodsInfo {
        private Integer goodsId;  // 商品id
        private Integer siteId;  // 站点id
        private String goodsName;  // 商品名称
        private String goodsType;  // 商品类型
        private String subTitle;  // 副标题
        private String goodsCover;  // 商品封面
        private String goodsImage;  // 商品图片
        private String goodsCategory;  // 商品分类
        private String goodsDesc;  // 商品介绍
        private Integer brandId;  // 商品品牌id
        private String labelIds;  // 标签组
        private String serviceIds;  // 商品服务
        private String unit;  // 单位
        private Integer stock;  // 商品库存（总和）
        private Integer saleNum;  // 销量
        private Integer virtualSaleNum;  // 虚拟销量
        private Integer status;  // 商品状态（1.正常0下架）
        private Integer sort;  // 排序
        private String deliveryType;  // 支持的配送方式
        private Integer isLimit = 0;
        private Integer limitType = 1;
        private Integer maxBuy = 0;
        private Integer minBuy = 0;
        private Integer isFreeShipping;  // 是否免邮
        private String feeType;  // 运费设置，选择模板：template，固定运费：fixed
        private BigDecimal deliveryMoney;  // 固定运费
        private Integer deliveryTemplateId;  // 运费模板
        private Integer virtualAutoDelivery;  // 虚拟商品是否自动发货
        private String virtualReceiveType;  // 虚拟商品收货方式，auto：自动收货，artificial：买家确认收货，verify：到店核销
        private Integer virtualVerifyType;  // 虚拟商品核销有效期类型，0：不限，1：购买后几日有效，2：指定过期日期
        private Integer virtualIndate;  // 虚拟到期时间
        private Integer supplierId;  // 供应商id
        private Integer attrId;  // 商品参数id
        private String attrFormat;  // 商品参数内容，json格式
        private Integer isDiscount;  // 是否参与限时折扣
        private String memberDiscount;  // 会员等级折扣，不参与：空，会员折扣：discount，指定会员价：fixed_price
        @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
        private String vipCardDiscount;  // 付费会员价，json格式，指定会员价
        private Integer posterId;  // 海报id
        private Integer formId;
        private Integer isGift;
        private String goodsTypeName;
        private String goodsCoverThumbSample;
        private String goodsCoverThumbMid;
        private String goodsCoverThumbBig;
        private String goodsImageThumbSample;
        private String goodsImageThumbMid;
        private String goodsImageThumbBig;

        private String getGoodsTypeName() {
            return GoodsTypeEnum.getNameByType(this.goodsType);
        }

        private String getGoodsCoverThumbSample() {
            return CommonUtils.thumbImageSmall(this.siteId, this.goodsCover);
        }

        private String getGoodsCoverThumbMid() {
            return CommonUtils.thumbImageMiddle(this.siteId, this.goodsCover);
        }

        private String getGoodsCoverThumbBig() {
            return CommonUtils.thumbImageBig(this.siteId, this.goodsCover);
        }

        private String getGoodsImageThumbSample() {
            return CommonUtils.thumbImageSmall(this.siteId, this.goodsImage);
        }

        private String getGoodsImageThumbMid() {
            return CommonUtils.thumbImageMiddle(this.siteId, this.goodsImage);
        }

        private String getGoodsImageThumbBig() {
            return CommonUtils.thumbImageBig(this.siteId, this.goodsImage);
        }
    }
}
