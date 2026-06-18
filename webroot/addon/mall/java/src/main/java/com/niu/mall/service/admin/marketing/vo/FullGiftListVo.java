package com.niu.mall.service.admin.marketing.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.mall.enums.fullactive.FullGiftGoodsTypeEnum;
import com.niu.mall.enums.fullactive.FullGiftMemberTypeEnum;
import com.niu.mall.enums.fullactive.FullGiftRuleTypeEnum;
import com.niu.mall.enums.fullactive.FullGiftStatusEnum;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class FullGiftListVo implements Serializable {

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

    private String memberTypeJson;

    private String ruleJson;

    private String remark;

    private Integer sort;

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime;

    public String getStatusName() {
        return FullGiftStatusEnum.getNameByStatus(this.status);
    }

    public String getGoodsTypeName() {
        return FullGiftGoodsTypeEnum.getNameByType(this.goodsType);
    }

    public String getRuleTypeName() {
        return FullGiftRuleTypeEnum.getNameByType(this.ruleType);
    }

    public String getMemberTypeName() {
        return FullGiftMemberTypeEnum.getNameByType(this.memberType);
    }

    private String creator;
    private Map memberTypeInfo = new HashMap<>();
}