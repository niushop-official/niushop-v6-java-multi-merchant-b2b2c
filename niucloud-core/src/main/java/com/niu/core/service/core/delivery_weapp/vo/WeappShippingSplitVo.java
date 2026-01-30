package com.niu.core.service.core.delivery_weapp.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class WeappShippingSplitVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 发货id（关联ns_delivery_weapp_shipping表id）
     */
    private Integer shippingId;

    /**
     * 交易id
     */
    private Integer tradeId;

    /**
     * 交易类型
     */
    private String tradeType;

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 订单交易id
     */
    private Integer splitTradeId;

    /**
     * 订单交易类型
     */
    private String splitTradeType;

    /**
     * 订单交易单号
     */
    private String splitTradeNo;

    /**
     * 原支付交易对应的微信订单号
     */
    private String transactionId;

    /**
     * 分拆发货模式时必填，用于标识分拆发货模式下是否已全部发货完成
     */
    private String isAllDelivered;

    /**
     * 录入状态：1-成功；0-失败
     */
    private Integer status;

    /**
     * 错误码
     */
    private String errcode;

    /**
     * 错误原因
     */
    private String errmsg;

    private List<WeappShippingSplitItemVo> shippingItem;
}
