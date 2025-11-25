package com.niu.core.service.admin.site.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class SiteFinanceLogVo {
    private Integer id;
    private Integer mainId;
    private String mainName;
    private Integer siteId;
    private String type;
    private BigDecimal value;
    private String tradeType;
    private String tradeNo;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;
    private String memo;
    private String raeType;
    
    // 计算字段
    private String typeName;
    private String tradeTypeName;
    private String raeTypeName;
} 