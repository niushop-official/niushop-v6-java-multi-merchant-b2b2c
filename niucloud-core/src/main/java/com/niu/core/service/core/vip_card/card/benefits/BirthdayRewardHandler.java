package com.niu.core.service.core.vip_card.card.benefits;

import java.util.Map;

public class BirthdayRewardHandler implements BenefitsContentHandler {
    @Override
    public String handle(Integer siteId, Map<String, Object> config) {
        Object giftsContent = config.get("gifts_content");
        if (giftsContent instanceof Map) {
            Map<?, ?> giftsMap = (Map<?, ?>) giftsContent;
            if (giftsMap.isEmpty()) return null;
            StringBuilder sb = new StringBuilder("购卡后生日当天赠送：");
            int i = 0;
            for (Map.Entry<?, ?> entry : giftsMap.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof Map) {
                    Map<?, ?> gift = (Map<?, ?>) value;
                    Object content = gift.get("content");
                    if (content != null) {
                        if (i++ > 0) sb.append("；");
                        sb.append(content);
                    }
                }
            }
            return sb.toString();
        }
        return null;
    }
}
