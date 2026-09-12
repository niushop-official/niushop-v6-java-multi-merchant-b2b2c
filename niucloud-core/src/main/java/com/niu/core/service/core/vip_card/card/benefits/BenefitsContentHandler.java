package com.niu.core.service.core.vip_card.card.benefits;

import java.util.Map;

@FunctionalInterface
public interface BenefitsContentHandler {
    String handle(Integer siteId, Map<String, Object> config);
}