package com.niu.core.service.core.vip_card.card.benefits;

import cn.hutool.core.util.StrUtil;

import java.util.Map;

/**
 * 阶段返利权益文案（对齐 PHP stage_reward）
 */
public class StageRewardHandler implements BenefitsContentHandler {
    @Override
    public String handle(Integer siteId, Map<String, Object> config) {
        if (config == null) {
            return null;
        }
        Object giftsContent = config.get("gifts_content");
        Object timeObj = config.get("time");
        if (!(giftsContent instanceof Map) || timeObj == null) {
            return null;
        }

        Map<?, ?> giftsMap = (Map<?, ?>) giftsContent;
        if (giftsMap.isEmpty()) {
            return null;
        }

        String giftsText = joinGiftContents(giftsMap);
        if (StrUtil.isBlank(giftsText)) {
            return null;
        }
        return "购卡" + timeObj + "天后赠送：" + giftsText;
    }

    private static String joinGiftContents(Map<?, ?> giftsMap) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Object value : giftsMap.values()) {
            if (!(value instanceof Map<?, ?> gift)) {
                continue;
            }
            Object content = gift.get("content");
            if (content == null) {
                continue;
            }
            if (i++ > 0) {
                sb.append("；");
            }
            sb.append(content);
        }
        return sb.toString();
    }
}
