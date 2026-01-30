package com.niu.mall.service.core.order.vo;

import com.niu.mall.entity.order.config.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单配置数据
 */
@Data
public class OrderConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;
    private MallOrderClose closeOrderInfo; //订单关闭配置
    private MallOrderRefund refund;  //订单退款配置
    private MallOrderEvaluate evaluate; //订单评价配置
    private MallOrderInvoice invoice;  //订单发票配置
    private MallOrderFinish confirm;  //订单完成配置
    private MallOrderDelaySpear delaySpear; //延迟分账配置
    private Integer isClose; //自动取消订单 2:否 1:是
    private Integer isFinish; // 自动确认收货 2:否 1:是
    private Integer formId;



}
