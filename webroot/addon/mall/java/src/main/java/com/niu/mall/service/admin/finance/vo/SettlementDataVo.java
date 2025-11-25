package com.niu.mall.service.admin.finance.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.mall.enums.order.OrderFromTypeEnum;
import com.niu.mall.enums.trade.SettlementEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ShopOrder视图
 */
@Data
public class SettlementDataVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal shopWaitSettlementMoney;  // 订单金额
    private BigDecimal mallWaitSettlementMoney;
    private BigDecimal shopSettlementMoney;
    private BigDecimal mallSettlementMoney;


}
