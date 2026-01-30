package com.niu.mall.service.api.goods.vo;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.upload.model.ThumbModelResult;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.admin.site.vo.SiteAdminVo;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.mall.entity.goods.MallGoodsSpec;
import com.niu.mall.listener.treasure.SnowShowDataResult;
import com.niu.core.service.core.delivery.ICoreDeliveryConfigService;
import com.niu.core.service.core.delivery.vo.DeliveryConfigVo;
import com.niu.mall.service.api.marketing.vo.MallDiscountGoodsInfoVo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
@Data
public class MallGoodsDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer skuId; // 商品sku_id
    private Integer siteId; // 站点id
    private String skuName; // 商品sku名称
    private String skuImage; // sku主图
    private String skuNo; // 商品sku编码
    private Integer goodsId; // 商品id
    private String skuSpecFormat; // sku规格格式
    @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
    private BigDecimal price; // sku单价
    private BigDecimal marketPrice; // 划线价
    private BigDecimal salePrice; // 实际卖价（有活动显示活动价，默认原价）
    private BigDecimal costPrice; // sku成本价
    private Integer stock; // 商品sku库存
    private Integer point; // 商品sku库存
    private BigDecimal weight; // 重量（单位kg）
    private BigDecimal volume; // 体积（单位立方米）
    private Integer saleNum; // 销量
    private Integer isDefault; // 是否默认
    private BigDecimal memberPrice;
    private Integer evaluateIsShow;
    private MallDiscountGoodsInfoVo discountInfo;
    private Integer exchangeId;
    private Integer isLimit = 0;
    private Integer limitType = 1;
    private Integer maxBuy = 0;
    private Integer minBuy = 0;
    private ShopInfo shopInfo;
    private String showType;
    private BigDecimal showPrice;
    private MemberInfo memberInfo = new MemberInfo();

    @JsonProperty("skuList")
    private List<Sku> skuList;

    private Goods goods;

    @JsonProperty("goodsSpec")
    private List<Spec> goodsSpec;

    private List<Service> service = new LinkedList<>();

    private List<GoodsLabelListVo> labelInfo = new LinkedList<>();

    private Brand brandInfo;
    private int isNewcomer;
    private String type;
    private String typeName;

    private String newcomerPrice;

    private String newcomerDesc;

    private Object sowShowList;


    private Object diyDetailInfo;

    private ImageSize imageSize = new ImageSize();

    @Data
    public static class Goods {
        private Integer goodsId; // 商品id
        private Integer siteId; // 站点id
        private String goodsName; // 商品名称
        private String goodsType; // 商品类型
        private String subTitle; // 副标题
        private String goodsCover; // 商品封面
        private String goodsCoverThumbBig;
        private String goodsCoverThumbSmall;
        private String goodsCoverThumbMid;
        private String goodsImage; // 商品图片
        private List<String> goodsImageThumbBig;
        private List<String> goodsImageThumbSmall;
        private List<String> goodsImageThumbMid;
        private String goodsVideo = "";
        @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
        private String goodsCategory; // 商品分类
        private String goodsDesc; // 商品介绍
        private Integer brandId; // 商品品牌id
        @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
        private String labelIds; // 标签组
        @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
        private String serviceIds; // 商品服务
        private String unit = "件"; // 单位
        private Integer stock; // 商品库存（总和）
        private Integer saleNum; // 销量
        private Integer virtualSaleNum; // 虚拟销量
        private Integer status; // 商品状态（1.正常0下架）
        private Integer sort; // 排序
        @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
        private String deliveryType; // 支持的配送方式
        private Integer isFreeShipping; // 是否免邮
        private String feeType; // 运费设置，选择模板：template，固定运费：fixed
        private BigDecimal deliveryMoney; // 固定运费
        private Integer deliveryTemplateId; // 运费模板
        private Integer virtualAutoDelivery; // 虚拟商品是否自动发货
        private String virtualReceiveType; // 虚拟商品收货方式，auto：自动收货，artificial：买家确认收货，verify：到店核销
        private Integer virtualVerifyType; // 虚拟商品核销有效期类型，0：不限，1：购买后几日有效，2：指定过期日期
        private Integer virtualIndate; // 虚拟到期时间
        @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
        private String attrFormat; // 商品参数内容，json格式
        private Integer isDiscount; // 是否参与限时折扣
        private String memberDiscount; // 会员等级折扣，不参与：空，会员折扣：discount，指定会员价：fixed_price
        private Integer posterId; // 海报id
        private Integer isCollect = 0;
        private int hasBuy;
        private Integer isLimit = 0;
        private Integer limitType = 1;
        private Integer maxBuy = 0;
        private Integer minBuy = 0;
        private Integer isGift = 0;
        private Integer formId;
        @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
        private String deliveryTypeList;

        /** 平台商品参数id，支持多个 */
        private String attrIds;

        /** 店铺商品参数id，支持多个 */
        private String shopAttrIds;

        /** 自定义详情 */
        private Integer diyDetailId;

        private ICoreUploadService coreUploadService() {
            return SpringContext.bean(ICoreUploadService.class);
        }

        public String getGoodsCoverThumbSmall() {
            String imagStr = coreUploadService().thumb(this.siteId, this.getGoodsCover(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType());
            if(ObjectUtil.isEmpty(imagStr)){
                return this.getGoodsCover();
            }
            return imagStr;
        }

        public String getGoodsCoverThumbMid() {
            String imagStr = coreUploadService().thumb(this.siteId, this.getGoodsCover(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
            if(ObjectUtil.isEmpty(imagStr)){
                return this.getGoodsCover();
            }
            return imagStr;
        }

        public String getGoodsCoverThumbBig() {
            String imagStr = coreUploadService().thumb(this.siteId, this.getGoodsCover(), UploadThumbTypeEnum.BIG.getType()).getDataMap().get(UploadThumbTypeEnum.BIG.getType());
            if(ObjectUtil.isEmpty(imagStr)){
                return this.getGoodsCover();
            }
            return imagStr;
        }

        public List<String> getGoodsImageThumbSmall() {
            List<String> list = new LinkedList<>();
            List<String> images = Arrays.asList(this.goodsImage.split(","));
            for (String path : images) {
                String res = coreUploadService().thumb(this.siteId, path, UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType());
                if(res != null){
                    list.add(res);
                }
            }
            return list;
        }

        public List<String> getGoodsImageThumbMid() {
            List<String> list = new LinkedList<>();
            List<String> images = Arrays.asList(this.goodsImage.split(","));
            for (String path : images) {

                String res = coreUploadService().thumb(this.siteId, path, UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
                if(res != null){
                    list.add(res);
                }
            }
            return list;
        }

        public List<String> getGoodsImageThumbBig() {
            List<String> list = new LinkedList<>();
            List<String> images = Arrays.asList(this.goodsImage.split(","));
            for (String path : images) {
                String res = coreUploadService().thumb(this.siteId, path, UploadThumbTypeEnum.BIG.getType()).getDataMap().get(UploadThumbTypeEnum.BIG.getType());
                if(res != null){
                    list.add(res);
                }
            }
            return list;
        }
        public String getDeliveryType() {
            if (ObjectUtil.isNotEmpty(this.deliveryType) && !"[]".equals(this.deliveryType)){
                List<DeliveryConfigVo> list = SpringContext.bean(ICoreDeliveryConfigService.class).getDeliveryConfig(this.siteId);
                JSONArray jsonArray = JSONUtil.parseArray(this.deliveryType);
                //排序
                JSONArray jsonArrayNew =  new JSONArray();
                Map<String, DeliveryConfigVo> orderedMap = list.stream()
                        .collect(Collectors.toMap(DeliveryConfigVo::getKey,
                                vo -> vo,
                                (existing, replacement) -> existing,
                                LinkedHashMap::new));
                for (String key : orderedMap.keySet()){
                    if (jsonArray.contains(key)){
                        jsonArrayNew.add( key);
                    }
                }
                return JSONUtil.toJsonStr(jsonArrayNew);
            }
            return "";

        }
        public String getDeliveryTypeList() {
            Map<String, DeliveryConfigVo> listMap = new LinkedHashMap<>();
            if(StringUtils.isNotEmpty(this.deliveryType)){
                List<DeliveryConfigVo> deliveryTypeList = new ArrayList<>();
                List<DeliveryConfigVo> list = SpringContext.bean(ICoreDeliveryConfigService.class).getDeliveryConfig(this.siteId);
                JSONArray jsonArray = JSONUtil.parseArray(this.deliveryType);
                for (int i = 0; i < jsonArray.size(); i++){
                    String key = jsonArray.get(i).toString();
                    DeliveryConfigVo deliveryConfigVo = list.stream().filter(item -> item.getKey().equals(key)).findFirst().orElse(null);
                    if (ObjectUtil.isNotEmpty(deliveryConfigVo) && ObjectUtil.isNotEmpty(deliveryConfigVo.getStatus()) && deliveryConfigVo.getStatus() == 1) {
                        deliveryTypeList.add(deliveryConfigVo);
                    }
                }
                Map<String, DeliveryConfigVo> orderedMap = list.stream()
                        .collect(Collectors.toMap(DeliveryConfigVo::getKey,
                                vo -> vo,
                                (existing, replacement) -> existing,
                                LinkedHashMap::new));
                List<DeliveryConfigVo> deliveryTypeListAll = new ArrayList<>();
                for (String key : orderedMap.keySet()){
                    List<DeliveryConfigVo> listThis = deliveryTypeList.stream().filter(item -> item.getKey().equals(key)).toList();
                    if (!listThis.isEmpty()){
                        deliveryTypeListAll.addAll(listThis);
                    }
                }

                for (DeliveryConfigVo deliveryConfigVo : deliveryTypeListAll){
                    listMap.put(deliveryConfigVo.getKey(), deliveryConfigVo);
                }
            }
            return JSONUtil.toJsonStr(listMap);
        }

    }

    @Data
    public static class Sku {
        private Integer skuId; // 商品sku_id
        private Integer siteId; // 站点id
        private String skuName; // 商品sku名称
        private String skuImage; // sku主图
        private String skuNo; // 商品sku编码
        private Integer goodsId; // 商品id
        private String skuSpecFormat; // sku规格格式
        private BigDecimal price; // sku单价
        private BigDecimal marketPrice; // 划线价
        private BigDecimal salePrice; // 实际卖价（有活动显示活动价，默认原价）
        private BigDecimal costPrice; // sku成本价
        private Integer stock; // 商品sku库存
        private Integer limitNum; // 商品sku库存
        private Integer point; // 商品sku库存
        private BigDecimal weight; // 重量（单位kg）
        private BigDecimal volume; // 体积（单位立方米）
        private Integer saleNum; // 销量
        private Integer isDefault; // 是否默认
        private BigDecimal memberPrice;
        private BigDecimal newcomerPrice;
        private int isNewcomer;
        private String showType;
        private BigDecimal showPrice;
        private Integer isSell;

    }

    @Data
    public static class Spec extends MallGoodsSpec {

    }

    @Data
    public static class Service {
        private Integer serviceId; //
        private String serviceName; // 服务名称
        private String image; // 图片
        private String desc; // 描述
    }

    @Data
    public static class Brand {
        private Integer brandId; // 品牌ID
        private String brandName; // 品牌名称
        private String logo; // 品牌logo
        private String desc; // 品牌介绍
    }

    @Data
    public static class ShopInfo {

        /** 商家分类id */
        private Integer categoryId;

        private String categoryName;

        /** 店铺关注数 */
        private Integer followNumber;

        /** 是否自营 */
        private Integer isSelf;

        /** 站点id */
        private Integer siteId;

        /** 站点名称 */
        private String siteName;
        /** 分组ID(0:不限制) */
        private Integer groupId;
        /** 站点套餐名称 */
        private String groupName;
        /** 关键字 */
        private String keywords;
        /** 站点类型 */
        private String appType;
        /** 站点logo */
        private String logo;
        /** 简介 */
        private String desc;
        /** 状态 1-正常 0-体验期 2-已到期 */
        private Integer status;
        /** 纬度 */
        private String latitude;
        /** 经度 */
        private String longitude;
        /** 省 */
        private Integer provinceId;
        /** 市 */
        private Integer cityId;
        /** 区 */
        private Integer districtId;
        /** 详细地址 */
        private String address;
        /** 完整地址 */
        private String fullAddress;
        /** 客服电话 */
        private String phone;
        /** 营业时间 */
        private String businessHours;

        private Integer businessStatus;
        /** 创建时间 */
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long createTime;
        /** 前台名称*/
        private String frontEndName;
        /** 前台logo */
        private String frontEndLogo;
        /** 前台icon */
        private String frontEndIcon;
        /** 网站图标 */
        private String icon;

        private SiteShopInfo shop;

        private String deliveryConfig;


        private BigDecimal qualityScore; // 商品质量
        private BigDecimal serviceScore; // 商家服务
        private BigDecimal logisticsScore; // 发货物流
    }
    @Data
    public static class SiteShopInfo {
        /** 商家分类id */
        private Integer categoryId;

        private String categoryName;

        /** 店铺关注数 */
        private Integer followNumber;

        /** 是否自营 */
        private Integer isSelf;
        /** 是否推荐 */
        private Integer isRecommend;

        /** 站点id */
        private Integer siteId;

        private Integer businessStatus;

    }

    @Data
    public static class MemberInfo {
        private Integer isFollow = 0;
    }

    @Data
    public static class ImageSize {
        private String width = "";
        private String height = "";
    }
}
