package com.niu.mall.service.admin.marketing.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class FullGiftInitVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Map<String, Object> statusList;

    private Map<String, Object> goodsTypeList;

    private Map<String, Object> ruleTypeList;

    private Map<String, Object> memberTypeList;

    public FullGiftInitVo() {
        this.statusList = new HashMap<>();
        this.goodsTypeList = new HashMap<>();
        this.ruleTypeList = new HashMap<>();
        this.memberTypeList = new HashMap<>();
    }

    private FullGiftInfoVo info;

}