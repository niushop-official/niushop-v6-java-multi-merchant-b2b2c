package com.niu.core.service.core.vip_card.card.benefits;

import lombok.Data;

import java.util.Map;

@Data
public class BenefitsConfig {
    private String type;
    private String name;
    private String icon;
    private String desc;
    private String addon;
    private String component;
    // 内容处理器，key 为场景（admin/wap），value 为类名或表达式
    private Map<String, String> contentStr;
    private Map<String, BenefitsContentHandler> content;
}