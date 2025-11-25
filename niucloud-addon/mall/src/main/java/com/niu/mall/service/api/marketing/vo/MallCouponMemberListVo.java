package com.niu.mall.service.api.marketing.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.mall.enums.coupon.CouponMemberReceiveTypeEnum;
import com.niu.mall.enums.coupon.CouponStatusEnum;
import com.niu.mall.enums.coupon.CouponTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ShopCouponMember列表视图
 */
@Data
public class MallCouponMemberListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 优惠券发放记录id
    private Integer siteId;  // 站点id
    private String siteName;  // 站点id
    private Integer couponId;  // 优惠券id
    private Integer memberId;  // 会员id
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 领取时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long expireTime;  // 过期时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long useTime;  // 使用时间
    private String type;  // 优惠券类型
    private String typeName;
    private Integer status;  // 状态
    private String statusName;
    private String title;  // 优惠券名称
    private BigDecimal price;  // 面值
    @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
    private BigDecimal minConditionMoney;  // 最低使用门槛
    private String receiveType;  // 领取方式
    private String receiveTypeName;
    private Integer tradeId;  // 关联业务id

    /**
     * 获取券领取类型
     * @return
     */
    public String getReceiveTypeName() {
        if (this.receiveType == null) {
            return "";
        }
        return CouponMemberReceiveTypeEnum.getReceiveTypeName(this.receiveType);
    }

    /**
     * 获取状态名称
     * @return
     */
    public String getStatusName() {
        if (this.status == null) {
            return "";
        }
        return CouponStatusEnum.getNameByStatus(this.status);
    }

    /**
     * 获取优惠券价格，去除多余的0
     * @return
     */
    public BigDecimal getCouponPrice(){
        if (this.price == null) {
            return BigDecimal.ZERO;
        }
        return this.price.stripTrailingZeros();
    }

    /**
     * 获取优惠券最低使用价格
     * @return
     */
    public BigDecimal getCouponMinPrice(){
        if (this.minConditionMoney == null) {
            return BigDecimal.ZERO;
        }
        return this.minConditionMoney.stripTrailingZeros();
    }

    /**
     * 获取类型名称
     * @return
     */
    public String getTypeName(){
        if (this.type == null || this.type.trim().isEmpty()) {
            return "";
        }
        try {
            return CouponTypeEnum.getNameByType(Integer.parseInt(this.type));
        } catch (NumberFormatException e) {
            return "";
        }
    }


}
