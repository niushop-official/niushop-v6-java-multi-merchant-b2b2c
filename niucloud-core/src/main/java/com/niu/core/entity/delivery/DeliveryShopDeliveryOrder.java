package com.niu.core.entity.delivery;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 门店配送订单实体
 */
@Data
public class DeliveryShopDeliveryOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 站点ID */
    private Integer siteId;

    /** 配送单号 */
    private String deliveryNo;

    /** 三方配送单号 */
    private String outDeliveryNo;

    /** 交易类型，mall商城 supply供货商 */
    private String tradeType;

    /** 交易单号 */
    private String tradeNo;

    /** 交易ID */
    private Integer tradeId;

    /** 配送费用 */
    private BigDecimal deliveryMoney;

    /** 配送起点（发货点） */
    private String deliveryStart;

    /** 配送起点省 */
    private Integer deliveryStartProvinceId;

    /** 配送起点市 */
    private Integer deliveryStartCityId;

    /** 配送起点区 */
    private Integer deliveryStartDistrictId;

    /** 配送起点经度 */
    private String deliveryStartLng;

    /** 配送起点纬度 */
    private String deliveryStartLat;

    /** 配送终点（收货点） */
    private String deliveryEnd;

    /** 配送终点省 */
    private Integer deliveryEndProvinceId;

    /** 配送终点市 */
    private Integer deliveryEndCityId;

    /** 配送终点区 */
    private Integer deliveryEndDistrictId;

    /** 配送终点经度 */
    private String deliveryEndLng;

    /** 配送终点纬度 */
    private String deliveryEndLat;

    /** 配送距离 */
    private BigDecimal deliveryDistance;

    /** 配送员ID */
    private Integer deliverId;

    /** 配送员姓名 */
    private String deliverName;

    /** 配送员手机号 */
    private String deliverMobile;

    /** 配送状态 */
    private String status;

    /** 配送时间 */
    private Long createTime;

    /** 送达时间 */
    private Long finishTime;

    /** 取消时间 */
    private Long cancelTime;

    /** 备注 */
    private String remark;

    /** 商品信息 */
    private String body;
}