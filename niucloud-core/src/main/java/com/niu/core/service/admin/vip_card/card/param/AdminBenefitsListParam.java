package com.niu.core.service.admin.vip_card.card.param;

import lombok.Data;

@Data
public class AdminBenefitsListParam {
    private String name;  // 权益名称（模糊搜索）
    private String type;  // 权益类型（精确匹配）
}