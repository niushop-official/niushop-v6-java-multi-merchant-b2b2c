package com.niu.core.service.admin.verify.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 核销记录实体
 */
@Data
public class VerifyCodeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 核销码 */
    private String code; 
    
    /** 核销参数 */
    private String qrcode;
}