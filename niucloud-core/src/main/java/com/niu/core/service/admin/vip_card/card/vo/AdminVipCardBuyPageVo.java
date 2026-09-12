package com.niu.core.service.admin.vip_card.card.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.vip_card.MemberVipCard;
import com.niu.core.entity.vip_card.MemberVipCardItem;
import lombok.Data;

@Data
public class AdminVipCardBuyPageVo {
    private Integer id;
    private Integer siteId;
    private Integer orderId;
    private Integer memberId;
    private Integer cardId;
    private Integer itemId;
    private Integer isExpire;
    private String isExpireName;
    private Integer isTrial;
    private String isTrialName;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long expireTime;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    private Member member;
    private MemberVipCard vipCard;
    private MemberVipCardItem vipCardItem;
}
