package com.niu.mall.service.admin.marketing.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.mall.enums.fullactive.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class FullReductionListVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer siteId;

    private String title;

    private String type;

    private String status;

    private String statusName;

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long startTime;

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long endTime;

    private String goodsType;

    private String goodsTypeName;

    private String ruleType;

    private String ruleTypeName;

    private String memberType;

    private String memberTypeName;

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

    private String promotionType;

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

    private String creator;
    private Map memberTypeInfo = new HashMap<>();
}