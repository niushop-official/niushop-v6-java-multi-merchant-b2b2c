package com.niu.core.service.admin.marketing.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class FullReductionInitVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, Object> typeList;

    private Map<String, Object> statusList;

    private Map<String, Object> goodsTypeList;

    private Map<String, Object> ruleTypeList;

    private Map<String, Object> memberTypeList;

    private Map<String, Object> promotionTypeList;

    private FullReductionInfoVo info;

    public FullReductionInitVo() {
        this.statusList = new HashMap<>();
        this.typeList = new HashMap<>();
        this.goodsTypeList = new HashMap<>();
        this.ruleTypeList = new HashMap<>();
        this.memberTypeList = new HashMap<>();
        this.promotionTypeList = new HashMap<>();
    }

}