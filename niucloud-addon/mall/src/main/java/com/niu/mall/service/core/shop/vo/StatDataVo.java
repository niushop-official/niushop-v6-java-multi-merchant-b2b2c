package com.niu.mall.service.core.shop.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

@Data
public class StatDataVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Long> orderNum = new LinkedList<>(); // 订单总数

    private List<String> saleMoney = new LinkedList<>(); // 销售总额

    private List<String> refundMoney = new LinkedList<>(); // 退款总额

    private List<Long> accessSum = new LinkedList<>(); // 访问数

    private List<String> time = new LinkedList<>();
}
