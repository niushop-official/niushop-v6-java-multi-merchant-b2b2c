package com.niu.mall.service.core.stat.vo;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TrendDataVo {
    private List<String> dateList;             // 日期列表
    private List<Integer> visitNum;            // 访客数
    private List<BigDecimal> orderMoney;       // 付款金额
    private List<Integer> payMemberCount;      // 付款人数
    private List<Integer> payNum;              // 付款件数
    private List<BigDecimal> visitOrderRate;   // 访问->下单转化率
    private List<BigDecimal> orderPayRate;     // 下单->支付转化率
    private List<BigDecimal> visitPayRate;     // 访问->支付转化率
}