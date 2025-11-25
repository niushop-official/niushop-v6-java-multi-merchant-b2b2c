package com.niu.mall.service.api.marketing.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.mall.enums.coupon.CouponReceiveTypeEnum;
import com.niu.mall.enums.coupon.CouponStatusEnum;
import com.niu.mall.enums.coupon.CouponTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

/**
 * ShopCoupon列表视图
 */
@Data
public class MallCouponListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 自增ID
    private Integer siteId;  // 站点id
    private String title;  // 名称
    @JsonIgnore
    private Long startTimeValue; // 活动开启时间（内部字段）
    @JsonIgnore
    private Long endTimeValue; // 活动结束时间（内部字段）
    private Integer remainCount;  // 剩余数量
    private Integer receiveCount;  // 已领取数量
    private Integer limitCount;  // 单个会员限制领取数量
    @JsonIgnore
    private Integer receiveUseCount;  // 获取使用数量（PHP没有此字段）
    private Integer status;  //  状态 1 正常 2 未开启 3 已无效
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 添加时间
    @JsonIgnore
    private BigDecimal price;  // 面值（内部字段，使用getCouponPrice()返回）
    @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
    private BigDecimal minConditionMoney;  // 商品最低多少金额可用优惠券
    private Integer type;  // 优惠券类型 1通用优惠券 2商品品类优惠券 3商品优惠券
    private Integer receiveType;  // 领取方式
    private Integer validType;  // 有效时间
    private Integer length;  // 有效期时长(天)
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long validStartTime;  // 有效期开始时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long validEndTime;  // 有效期结束时间
    private Integer sort;  // 排序
    @JsonIgnore
    private Integer receiveStatus;  //  状态 1 正常 2 关闭（PHP没有此字段）
    private Integer isReceive;
    private Long memberReceiveCount;  // 会员领取数量
    @JsonProperty("join_site_ids")
    private List<Integer> joinSiteIds = new LinkedList<>(); // 优惠券可使用站点id
    private Integer isAllSiteJoin; // 是否所有站点可用
    private String siteName;
    private Integer needReceive;
    @JsonIgnore
    private String image; // 优惠券图片（PHP没有此字段）
    private String sumCount;
    private CouponGoods goods;

    @Data
    public static class CouponGoods{
        private Integer siteId;  // 站点id
        private Integer couponId;  // 优惠券id
        private Integer goodsId;
        @JsonProperty("goods_cover")
        private String goodsCover;
        private Integer shopSiteId;
        @JsonProperty("goodsSku")
        private goodsSkuInfo goodsSku;
        private ShopInfo shop;

    }

    @Data
    public static class goodsSkuInfo {
        private Integer goodsId;
    }

    @Data
    public static class ShopInfo {
        private Integer siteId;
    }

    /**
     * 获取活动开启时间（与PHP一致：0或时间戳）
     * @return
     */
    @JsonProperty("start_time")
    public Long getStartTime() {
        if (startTimeValue == null || startTimeValue == 0) {
            return 0L;
        }
        return startTimeValue;
    }

    /**
     * 设置活动开启时间
     * @param startTime
     */
    public void setStartTime(Long startTime) {
        this.startTimeValue = startTime;
    }

    /**
     * 获取活动结束时间（与PHP一致：0或时间戳）
     * @return
     */
    @JsonProperty("end_time")
    public Long getEndTime() {
        if (endTimeValue == null || endTimeValue == 0) {
            return 0L;
        }
        return endTimeValue;
    }

    /**
     * 设置活动结束时间
     * @param endTime
     */
    public void setEndTime(Long endTime) {
        this.endTimeValue = endTime;
    }

    /**
     * 获取券类型
     * @return
     */
    public String getTypeName() {
        return CouponTypeEnum.getNameByType(this.type);
    }

    /**
     * 获取券领取类型
     * @return
     */
    public String getReceiveTypeName() {
        return CouponReceiveTypeEnum.getNameByType(this.receiveType);
    }

    /**
     * 获取状态名称
     * @return
     */
    public String getStatusName() {
        return CouponStatusEnum.getNameByStatus(this.status);
    }

    /**
     * 获取全部数量
     * @return
     */
    public String getSumCount(){
        if(this.remainCount != -1)
        {
            return String.valueOf(this.remainCount + this.receiveCount);
        }else{
            return "-1";
        }
    }


    /**
     * 优惠券最低使用价格
     * @return
     */
    @JsonProperty("coupon_min_price")
    public String getCouponMinPrice() {
        if (minConditionMoney != null) {
            return minConditionMoney.stripTrailingZeros().toPlainString();
        }
        return "0";
    }

    /**
     * 优惠券价格（字符串格式，与PHP一致）
     * @return
     */
    @JsonProperty("coupon_price")
    public String getCouponPrice() {
        if (price != null) {
            return price.stripTrailingZeros().toPlainString();
        }
        return "0";
    }

    /**
     * 获取价格（字符串格式，保留两位小数）
     * @return
     */
    @JsonProperty("price")
    public String getPrice() {
        if (price != null) {
            return String.format("%.2f", price);
        }
        return "0.00";
    }

}
