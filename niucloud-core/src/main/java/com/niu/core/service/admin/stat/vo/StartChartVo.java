package com.niu.core.service.admin.stat.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class StartChartVo  implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Long> orderNum;
    private List<String> time;
    private List<String> saleMoney;
    private List<String> refundMoney;
    private List<Long> accessSum;
    private List<String> orderReceivableMoney;
    private List<String> orderActualMoney;
    private List<String> mallCouponMoney;
    private List<String> mallRefundCouponMoney;
    private List<String> orderRefundMoney;
    private List<String> orderActualRefundMoney;
    private List<String> shopSeparateMoney;
    private List<String> shopRefundSeparateMoney;
    private List<String> shopCommission;
    private List<String> shopRefundCommission;
    private List<String> deliveryMoney;
    private List<String> refundDeliveryMoney;
    private List<String> mallServiceMoney;
    private List<String> mallRefundServiceMoney;
    private List<String> rechargeMoney;
    private List<String> rechargePayMoney;
    private List<String> supplyMoney;
    private List<String> supplyRefundMoney;

}
