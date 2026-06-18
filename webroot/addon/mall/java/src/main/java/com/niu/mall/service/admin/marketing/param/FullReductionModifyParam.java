package com.niu.mall.service.admin.marketing.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

@Data
public class FullReductionModifyParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "title参数缺失")
    private String title;

    @NotNull(message = "type参数缺失")
    private String type;

    @NotNull(message = "status参数缺失")
    private String status;

    @NotNull(message = "startTime参数缺失")
    private Instant startTime;

    @NotNull(message = "endTime参数缺失")
    private Instant endTime;

    @NotNull(message = "goodsType参数缺失")
    private String goodsType;

    @NotNull(message = "ruleType参数缺失")
    private String ruleType;

    private String promotionType;

    @NotNull(message = "memberType参数缺失")
    private String memberType;

    private String memberTypeJson;

    @NotNull(message = "ruleJson参数缺失")
    private String ruleJson;

    private String remark;

    private Integer sort;

    private String goodsData;
}