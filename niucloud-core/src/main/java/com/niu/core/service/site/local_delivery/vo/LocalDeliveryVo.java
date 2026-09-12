package com.niu.core.service.site.local_delivery.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * LocaleDeliveryServiceListVo列表视图
 */
@Data
public class LocalDeliveryVo {

    private Integer localId; //
    private Integer siteId; // 站点id
    private String feeType; // 费用类型
    private BigDecimal baseDist; // 多少km内
    private BigDecimal basePrice; // 配送费用
    private BigDecimal gradDist; // 每超出多少km内
    private BigDecimal gradPrice; // 配送费用
    private BigDecimal weightStart; // 重量多少内不额外收费
    private BigDecimal weightUnit; // 每超出多少kg
    private BigDecimal weightPrice; // 价格
    private String deliveryType; // 配送类型
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String area; // 配送区域
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String center; // 发货地址中心点

    /** 配送时间设置 0 关闭 1 开启 */
    private Integer timeIsOpen;

    /** 时间选取类型 0 每天  1 自定义 */
    private Integer timeType;

    /** 营业时间  周一 周二....... */
    private String timeWeek;

    /** 时段设置单位分钟 */
    private Integer timeInterval;

    /** 时间选择需提前多少天 */
    private Integer advanceDay;

    /** 最多可预约多少天 */
    private Integer mostDay;

    /** 当日的起始时间 */
    private Long startTime;

    /** 当日的营业结束时间 */
    private Long endTime;

    /** 配送时间段 */
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String deliveryTime;
    
    private BigDecimal freeGoodsMoney;
}
