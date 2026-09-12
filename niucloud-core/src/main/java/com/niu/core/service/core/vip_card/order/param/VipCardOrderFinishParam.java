package com.niu.core.service.core.vip_card.order.param;

import com.niu.core.entity.vip_card.MemberVipCardOrderItem;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class VipCardOrderFinishParam {
    private Integer orderId;
    private Integer siteId;
    private Integer memberId;
    private MemberVipCardOrderItem orderItem;   // 包含 item_config, card_name, is_renew, card_id, benefits_config
    private BigDecimal orderMoney;
    private Long createTime;
}