package com.niu.mall.entity.order.config;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单评价设置
 */
@Data
public class MallOrderEvaluate implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer isEvaluate; // 是否开启订单评价
    private Integer evaluateIsToExamine; // 完成订单是否需要审核
    private Integer evaluateIsShow; // 是否显示评价
    private Integer isSelfEvaluate; // 自我评价
    private Integer isAutoGoodEvaluate; // 是否自动好评
    private Integer goodEvaluateDays; // 自动好评天数
}