package com.niu.mall.service.admin.marketing.param;

import lombok.Data;
import java.io.Serializable;
import jakarta.validation.constraints.NotNull;

@Data
public class FullGiftModifyParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "title参数缺失")
    private String title;

    @NotNull(message = "type参数缺失")
    private String type;

    @NotNull(message = "status参数缺失")
    private String status;

    @NotNull(message = "startTime参数缺失")
    private Long startTime;

    @NotNull(message = "endTime参数缺失")
    private Long endTime;

    @NotNull(message = "goodsType参数缺失")
    private String goodsType;

    @NotNull(message = "ruleType参数缺失")
    private String ruleType;

    @NotNull(message = "memberType参数缺失")
    private String memberType;

    private String memberTypeJson;

    @NotNull(message = "ruleJson参数缺失")
    private String ruleJson;

    private String remark;

    private Integer sort;

}