package com.niu.core.service.api.marketing.vo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.coupon.CouponReceiveTypeEnum;
import com.niu.core.enums.coupon.CouponStatusEnum;
import com.niu.core.enums.coupon.CouponTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ShopCoupon视图
 */
@Data
public class MallCouponInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 自增ID
    private Integer siteId;  // 站点id
    private String title;  // 名称
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long startTime; // 活动开启时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long endTime; // 活动结束时间
    private Integer remainCount;  // 剩余数量
    private Integer receiveCount;  // 已领取数量
    private Integer limitCount;  // 单个会员限制领取数量
    private String sumCount;  // 总数量
    private Integer receiveUseCount;  // 获取使用数量
    private Integer receiveExpireCount;  // 获取过期数量
    private Integer status;  //  状态 1 正常 2 未开启 3 已无效
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 添加时间
    @JsonIgnore
    private BigDecimal price = new BigDecimal(0);  // 面值
    @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
    private BigDecimal minConditionMoney = new BigDecimal(0);  // 商品最低多少金额可用优惠券
    private Integer type;  // 优惠券类型 1通用优惠券 2商品品类优惠券 3商品优惠券
    private Integer receiveType;  // 领取方式
    private Integer validType;  // 有效时间
    private Integer length;  // 有效期时长(天)
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long validStartTime;  // 有效期开始时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long validEndTime;  // 有效期结束时间
    private Integer sort;  // 排序
    private Integer receiveStatus;  //  状态 1 正常 2 关闭
    private Integer isReceive = 0;  // 是否可以领取 1可以 2不可以
    private Integer memberReceiveCount = 0;  // 会员领取数量
    private Integer needReceive;  // 是否需要领取 0否 1是
    private String joinSiteIds; // 优惠券可使用站点id
    private Integer isAllSiteJoin; // 是否所有站点可用
    private String siteName;




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
     * 优惠券价格
     * @return
     */
    @JsonProperty("coupon_price")
    public String getCouponPrice() {
        if (price != null) {
            return price.stripTrailingZeros().toPlainString();
        }
        return "0";
    }
}
