package com.niu.core.service.core.delivery_weapp.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class WeappShippingVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    /**
     * 站点id集合
     */
    private String siteIds;

    /**
     * 交易id（group_id）
     */
    private Integer tradeId;

    /**
     * 交易类型（mall_group或points_mall）
     */
    private String tradeType;

    /**
     * 交易单号（group_id）
     */
    private String tradeNo;

    /**
     * 订单单号类型：1-使用下单商户号和商户侧单号；2-使用微信支付单号
     */
    private Integer orderNumberType;

    /**
     * 原支付交易对应的微信订单号
     */
    private String transactionId;

    /**
     * 物流模式：1-实体物流配送；2-同城配送；3-虚拟商品；4-用户自提
     */
    private Integer logisticsType;

    /**
     * 发货模式：1-UNIFIED_DELIVERY（统一发货）；2-SPLIT_DELIVERY（分拆发货）
     */
    private Integer deliveryMode;

    /**
     * 小程序用户标识
     */
    private String openid;

    /**
     * 创建时间（时间戳）
     */
    private Integer createTime;

    /**
     * 录入状态：1-成功；0-失败
     */
    private Integer status;

    /**
     * 小程序APPID
     */
    private String appid;

    private List<WeappShippingSplitVo> shippingSplit;
}
