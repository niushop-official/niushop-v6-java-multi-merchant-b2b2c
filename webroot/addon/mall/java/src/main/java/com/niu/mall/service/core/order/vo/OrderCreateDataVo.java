package com.niu.mall.service.core.order.vo;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.api.member.vo.MemberLevelInfoVo;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.core.entity.delivery.DeliveryStore;
import com.niu.core.service.core.delivery.vo.DeliveryConfigVo;
import com.niu.mall.service.core.order.param.InvoiceCreateParam;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@Data
public class OrderCreateDataVo implements Serializable {
    protected static final long serialVersionUID = 1L;

    protected String orderKey = "";

    protected Integer orderId;

    protected Integer groupId;

    protected Integer siteId;

    protected SiteInfo siteInfo = new SiteInfo();

    protected Integer memberId;

    protected String orderFrom;

    protected List<Long> cartIds = new ArrayList<>();

    protected Map<String, Discount> discount = new HashMap<>();

    //计算扩展
    protected Map<String, Map<Integer, Discount>> discountList = new HashMap<>();

    //限购
    protected Map<String, LimitBuy> limitBuy;

    protected JSONObject extendData;

    protected Basic basic = new Basic();

    protected Buyer buyer;

    protected Map<Integer, ? extends Sku> goodsData;

    protected Map<Integer, ? extends Sku> giftGoods;

    protected Delivery delivery = new Delivery();

    protected List<String> error = new LinkedList<>();

    protected String memberRemark;

    protected String OrderType;


    protected JSONObject formData;

    private Integer formId;

    protected InvoiceCreateParam invoice;

    protected Integer supplyId;


    protected ConfigInfo config;

    @Data
    public static class Basic {
        protected String body = "";

        protected BigDecimal deliveryMoney = new BigDecimal(0);

        protected BigDecimal discountMoney = new BigDecimal(0);

        protected BigDecimal mallDiscountMoney = new BigDecimal(0);

        protected BigDecimal mallCouponMoney = new BigDecimal(0);

        protected BigDecimal couponMoney = new BigDecimal(0);

        protected BigDecimal goodsMoney = new BigDecimal(0);

        protected BigDecimal costGoodsMoney = new BigDecimal(0);

        protected BigDecimal orderMoney = new BigDecimal(0);

        protected Integer totalNum = 0;

        protected Set<String> hasGoodsTypes = new HashSet<>();
        protected Set<String> hasDeliveryTypes = new HashSet<>();

        protected Integer virtualVerifyMobile;
    }

    @Data
    public static class Buyer {
        protected String nickname;

        protected Integer point;

        protected MemberLevelInfoVo memberLevel;
    }

    @Data
    public static class LimitBuy {
        protected Integer goodsId;
        protected Integer siteId;
        protected String goodsName;
        protected Integer stock;
        protected Integer num;
        protected Integer isLimit;
        protected Integer limitType;
        protected Integer maxBuy;
        protected Integer minBuy;
    }

    @Data
    public static class Sku {
        protected Integer skuId; // 商品sku_id
        protected Integer num;
        protected Integer siteId; // 站点id
        protected String skuName; // 商品sku名称
        protected String skuImage; // sku主图
        protected String skuNo; // 商品sku编码
        protected Integer goodsId; // 商品id
        protected String skuSpecFormat; // sku规格格式
        protected BigDecimal price; // sku单价
        protected BigDecimal costPrice;
        protected BigDecimal salePrice; // 实际卖价（有活动显示活动价，默认原价）
        protected BigDecimal originalPrice;
        protected Integer stock; // 商品sku库存
        protected BigDecimal weight; // 重量（单位kg）
        protected BigDecimal volume; // 体积（单位立方米）
        protected BigDecimal memberPrice;
        protected BigDecimal discountMoney = new BigDecimal(0);
        protected BigDecimal mallDiscountMoney = new BigDecimal(0);
        protected BigDecimal costGoodsMoney = new BigDecimal(0);
        protected BigDecimal mallCouponMoney = new BigDecimal(0);
        protected BigDecimal goodsMoney;
        protected Goods goods;
        protected Integer notSupportDelivery = 0;
        protected BigDecimal deliveryMoney = new BigDecimal(0);
        protected Integer isFreeShipping = 0;
        protected BigDecimal newcomerPrice;
        protected int isNewcomer;
        protected JSONObject extend;
        protected Integer isGift = 0;
        protected Integer supplyId = 0; // 供应商id
        protected Integer supplySkuId; // 关联供应商商品规格id
        protected Long cardId = 0L;
    }

    @Data
    public static class Goods {
        protected Integer goodsId; // 商品id
        protected Integer siteId; // 站点id
        protected String goodsName; // 商品名称
        protected String goodsType; // 商品类型
        protected String subTitle; // 副标题
        protected String goodsCover; // 商品封面
        protected String goodsCoverThumbBig;
        protected String goodsCoverThumbSmall;
        protected String goodsCoverThumbMid;
        @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
        protected String goodsCategory; // 商品分类
        protected Integer goodsMallCategory;
        protected String goodsDesc; // 商品介绍
        protected Integer brandId; // 商品品牌id
        @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
        protected String labelIds; // 标签组
        @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
        protected String serviceIds; // 商品服务
        protected String unit; // 单位
        protected Integer stock; // 商品库存（总和）
        protected Integer saleNum; // 销量
        protected Integer virtualSaleNum; // 虚拟销量
        protected Integer status; // 商品状态（1.正常0下架）
        protected Integer sort; // 排序
        @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
        protected String deliveryType; // 支持的配送方式
        protected Integer isFreeShipping; // 是否免邮
        protected String feeType; // 运费设置，选择模板：template，固定运费：fixed
        protected BigDecimal deliveryMoney; // 固定运费
        protected Integer deliveryTemplateId; // 运费模板
        protected Integer virtualAutoDelivery; // 虚拟商品是否自动发货
        protected String virtualReceiveType; // 虚拟商品收货方式，auto：自动收货，artificial：买家确认收货，verify：到店核销
        protected Integer virtualVerifyType; // 虚拟商品核销有效期类型，0：不限，1：购买后几日有效，2：指定过期日期
        protected Integer virtualVerifyMobile;
        protected Integer virtualIndate; // 虚拟到期时间
        protected Integer supplyId; // 供应商id
        protected Integer attrId; // 商品参数id
        protected String attrFormat; // 商品参数内容，json格式
        protected Integer isDiscount; // 是否参与限时折扣
        protected String memberDiscount; // 会员等级折扣，不参与：空，会员折扣：discount，指定会员价：fixed_price
        private Integer isLimit;
        private Integer limitType;
        private Integer maxBuy;
        private Integer minBuy;
        private Integer formId;

        /** 供应商id */
        private Integer supplyGoodsId;
        protected ICoreUploadService coreUploadService() {
            return SpringContext.bean(ICoreUploadService.class);
        }

        public String getGoodsCoverThumbSmall() {
            return coreUploadService().thumb(this.siteId, this.getGoodsCover(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType());
        }

        public String getGoodsCoverThumbMid() {
            return coreUploadService().thumb(this.siteId, this.getGoodsCover(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
        }

        public String getGoodsCoverThumbBig() {
            return coreUploadService().thumb(this.siteId, this.getGoodsCover(), UploadThumbTypeEnum.BIG.getType()).getDataMap().get(UploadThumbTypeEnum.BIG.getType());
        }
    }

    @Data
    public static class Delivery {
        protected Map<String, DeliveryConfigVo> deliveryTypeList = new HashMap();

        protected String deliveryType = "";

        protected DeliveryStore takeStore;

        protected Object takeAddress;

        private Integer takeAddressId = 0;

        private Integer takeStoreId = 0;

        private String takerMobile = "";

        private String error = "";
    }

    @Data
    public static class Discount {
        protected Integer[] matchGoodsIds;
        protected String type;
        protected Integer num;
        protected BigDecimal money;
        protected String discountType = "";
        protected Integer discountTypeId;
        protected String content;
        protected String title;
        private Integer couponId;
        private Boolean isCan;
    }

    @Data
    public static class SiteInfo {
        protected Integer siteId;
        protected String siteName;
        protected ShopInfo shop = new ShopInfo();

    }

    @Data
    public static class ShopInfo {
        protected Integer siteId;
        protected Integer isSelf;
        protected BigDecimal commissionRate;
    }

    @Data
    public static class ConfigInfo{

        protected CloseOrder closeOrderInfo;

        protected Map<String,Object> deliveryType;

        protected InvoiceInfo invoice;

        @Data
        public static class CloseOrder{
            protected Integer isClose;
            protected Integer closeLength;
        }

        @Data
        public static class InvoiceInfo{
            @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
            protected String invoiceContent;
            @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
            protected String invoiceType;
            protected String isInvoice;
        }
    }
}
