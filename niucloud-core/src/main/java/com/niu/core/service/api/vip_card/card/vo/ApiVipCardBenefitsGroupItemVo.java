package com.niu.core.service.api.vip_card.card.vo;

import lombok.Data;

import java.util.List;

/**
 * 权益分组 item（对齐 PHP vip_card_item 关联结构）
 */
@Data
public class ApiVipCardBenefitsGroupItemVo {
    private Long benefitsItemId;
    private Long benefitsId;
    private String name;
    private ApiVipCardBenefitsConfigVo benefitsConfig;
    private List<ApiVipCardGoodsVo> goodsList;
}
