package com.niu.core.service.admin.verify.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 核销记录参数
 */
@Data
public class VerifyCodeParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer tradeId;

    private String tradeType;
}
