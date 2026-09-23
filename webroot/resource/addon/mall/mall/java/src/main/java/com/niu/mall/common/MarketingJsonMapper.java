package com.niu.mall.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import java.util.Map;

/**
 * 营销 API 返回 Map 的 JSON 转换，与 PHP toArray / 全局 Jackson SNAKE_CASE 一致。
 */
public final class MarketingJsonMapper {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private MarketingJsonMapper() {
    }

    public static Map<String, Object> toSnakeCaseMap(Object vo) {
        return MAPPER.convertValue(vo, new TypeReference<>() {
        });
    }
}
