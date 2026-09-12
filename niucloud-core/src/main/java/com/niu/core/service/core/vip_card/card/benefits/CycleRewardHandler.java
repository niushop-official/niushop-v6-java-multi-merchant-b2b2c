package com.niu.core.service.core.vip_card.card.benefits;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.niu.core.enums.vip_card.VipCardTimeTypeEnum;

import java.util.Map;

/**
 * 周期返利权益文案（对齐 PHP vip_card_benefits.php cycle_reward content.admin）
 */
public class CycleRewardHandler implements BenefitsContentHandler {
    @Override
    public String handle(Integer siteId, Map<String, Object> config) {
        if (config == null) {
            return null;
        }
        Object giftsContent = config.get("gifts_content");
        // 对齐 PHP：isset($config['time_type']) 才继续
        Object timeTypeObj = config.get("time_type");
        if (!(giftsContent instanceof Map) || timeTypeObj == null) {
            return null;
        }

        VipCardTimeTypeEnum timeType = VipCardTimeTypeEnum.getEnumByCode(String.valueOf(timeTypeObj));
        if (timeType == null) {
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

        return switch (timeType) {
            case TIME_TYPE_MONTH -> "购卡后每月" + ObjectUtil.defaultIfNull(config.get("time_month"), "") + "号赠送：" + giftsText;
            case TIME_TYPE_WEEK -> "购卡后每周" + ObjectUtil.defaultIfNull(config.get("time_week"), "") + "赠送：" + giftsText;
            case TIME_TYPE_DAY -> "购卡后每隔" + ObjectUtil.defaultIfNull(config.get("time_day"), "") + "天赠送：" + giftsText;
            default -> null;
        };
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
