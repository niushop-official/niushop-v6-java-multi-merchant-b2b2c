package com.niu.mall.service.admin.marketing.param;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class FullReductionParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String title;

    private String type;

    private String status;

    @JsonDeserialize(using = BeanJsonSerializer.UnixMillisDeserializer.class)
    private Long startTime;

    @JsonDeserialize(using = BeanJsonSerializer.UnixMillisDeserializer.class)
    private Long endTime;

    private String goodsType;

    private List<GoodsData> goodsData;

    private String ruleType;

    private List<Rule> ruleJson;

    private String promotionType;

    private String memberType;

    private List<Integer> memberTypeJson;

    private String remark;

    private Integer sort;

    @Data
    public static class GoodsData {
        private Integer goodsId;
        private List<Integer> skuIds;
    }

    @Data
    public static class Rule {
        private BigDecimal amount;
        private Integer level;
        private BigDecimal reduce;
        private List<GoodsData> goodsData;
        private List<GiftSkuData> giftSkuData;
    }

    @Data
    public static class GiftSkuData {
        private Integer goodsId;
        private String goodsName;
        private String goodsTypeName;
        private Integer num;
        private BigDecimal price;
        private Integer skuId;
        private String skuImage;
        private String skuName;
        private Integer stock;
    }
}
