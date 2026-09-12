package com.niu.core.service.admin.marketing.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.fullactive.*;
import com.niu.core.enums.fullactive.FullReductionGoodsTypeEnum;
import com.niu.core.enums.fullactive.FullReductionMemberTypeEnum;
import com.niu.core.enums.fullactive.FullReductionPromotionTypeEnum;
import com.niu.core.enums.fullactive.FullReductionRuleTypeEnum;
import com.niu.core.enums.fullactive.FullReductionStatusEnum;
import com.niu.core.enums.fullactive.FullReductionTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class FullReductionInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer siteId;

    private String title;

    private String addon;

    private String type;

    private String status;

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long startTime;

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long endTime;

    private String goodsType;

    private String ruleType;

    private String memberType;

    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String memberTypeJson;

    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String ruleJson;

    private String remark;

    private Integer sort;

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime;

    public String getStatusName() {
        return FullReductionStatusEnum.getNameByStatus(this.status);
    }

    public String getGoodsTypeName() {
        return FullReductionGoodsTypeEnum.getNameByType(this.goodsType);
    }

    public String getRuleTypeName() {
        return FullReductionRuleTypeEnum.getNameByType(this.ruleType);
    }

    public String getMemberTypeName() {
        return FullReductionMemberTypeEnum.getNameByType(this.memberType);
    }

    public String getTypeName() {
        return FullReductionTypeEnum.getNameByType(this.type);
    }

    public String getPromotionTypeName() {
        return FullReductionPromotionTypeEnum.getNameByType(this.promotionType);
    }

    private String promotionType;
    private String creator;
    private Map memberTypeInfo = new HashMap<>();
    private List<Map<String, Object>> goods;
    private ArrayList<Map<String, Object>> goodsData;

    private BigDecimal minThreshold;
}
