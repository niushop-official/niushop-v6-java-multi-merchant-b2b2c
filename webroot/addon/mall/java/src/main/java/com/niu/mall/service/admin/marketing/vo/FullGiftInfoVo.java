package com.niu.mall.service.admin.marketing.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.mall.enums.fullactive.FullGiftGoodsTypeEnum;
import com.niu.mall.enums.fullactive.FullGiftMemberTypeEnum;
import com.niu.mall.enums.fullactive.FullGiftRuleTypeEnum;
import com.niu.mall.enums.fullactive.FullGiftStatusEnum;
import com.niu.mall.service.admin.marketing.param.FullGiftParam;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class FullGiftInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer siteId;

    private String title;

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

    private List<FullGiftParam.Rule> ruleJson;

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

    @JsonIgnore
    public String getRuleTypeName() {
        return FullGiftRuleTypeEnum.getNameByType(this.ruleType);
    }

    public String getMemberTypeName() {
        return FullGiftMemberTypeEnum.getNameByType(this.memberType);
    }

    private String creator;
    /** 与 PHP append member_type_info 一致：全部会员时为 [] */
    private List<Map<String, Object>> memberTypeInfo = new ArrayList<>();
    private List<Map<String, Object>> goods;
    private List<Map<String, Object>> goodsData;

    private BigDecimal minThreshold;

}