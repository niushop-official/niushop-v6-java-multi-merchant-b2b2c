package com.niu.core.service.admin.vip_card.card.vo;


import lombok.Data;

@Data
public class AdminVipCardListVo {
    private Integer cardId;
    private Integer siteId;
    private String cardName;
    private Integer status;
    private Long createTime;
    private Integer sort;
}