package com.niu.mall.service.core.order.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单配置参数
 */
@Data
public class OrderConfigParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单关闭配置
     */
    private String isClose; //自动取消订单 2:否 1:是
    private Integer closeLength = 60; // 提交订单时长
    /**
     * 订单评价配置
     */
    private Integer isEvaluate = 0; // 评价开关

    private Integer evaluateIsToExamine = 0; // 评价审核
    private Integer evaluateIsShow = 0; // 是否显示评价
    private Integer isSelfEvaluate =0; // 是否允许自己评价

    /**
     * 订单完成配置
     */
    private String isFinish; // 自动确认收货 2:否 1:是

    private Integer finishLength = 7; // 自动确认收货时长

    /**
     * 订单发票配置
     */
    private String isInvoice = "2"; // 是否需要发票
    private String[] invoiceType = {}; // 发票类型
    private String[] invoiceContent = {}; // 发票内容

    /**
     * 订单退款配置
     */
    private String noAllowRefund; // 退款允许
    private Integer refundLength = 30; // 退款时长

    private Integer formId;


}
