package com.niu.mall.service.core.marketing.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class FullReductionCalculateVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String title;

    private String goodsType;
    private String memberType;
    private String memberTypeJson;
    private String ruleType;
    private String ruleJson; // 实际为 List<Map<String, Object>>

    private String type;

    private String typeName;

    private String promotionType;

    private Integer sort;

    private BigDecimal minThreshold;

    private BigDecimal matchedTotal;

    private BigDecimal discountAmount;

    private BigDecimal discountRate;

    private List<MatchedGoods> matchedGoods;

    private Map<String, Object> reduceInfo; // 最优规则

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long startTime;

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long endTime;

    @Data
    public static class MatchedGoods implements Serializable {
        private static final long serialVersionUID = 1L;

        private Integer goodsId;

        private Integer skuId;

        private BigDecimal payMoney;

        private Integer num;

        private BigDecimal discountAmount;

        private String skuKey;
    }

}