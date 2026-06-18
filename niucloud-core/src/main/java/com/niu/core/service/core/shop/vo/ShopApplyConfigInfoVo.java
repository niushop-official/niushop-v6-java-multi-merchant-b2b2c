package com.niu.core.service.core.shop.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ShopApplyConfigInfoVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String applyBanner;
    private String applyTitle;
    private Integer formId;
}
