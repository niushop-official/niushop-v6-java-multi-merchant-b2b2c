package com.niu.mall.service.admin.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单结算数据VO
 */
@Data
public class SettlementInfoListVo {
    
    /**
     * 店铺待结算总金额
     */
    private BigDecimal shopWaitSettlementMoney;
    
    /**
     * 平台待结算总金额
     */
    private BigDecimal mallWaitSettlementMoney;
    
    /**
     * 店铺已结算总金额
     */
    private BigDecimal shopSettlementMoney;
    
    /**
     * 平台已结算总金额
     */
    private BigDecimal mallSettlementMoney;
}
