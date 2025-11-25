package com.niu.core.service.admin.verify.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 核销记录参数
 */
@Data
public class VerifySearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String code;

    private String data;

    private String type;

    private Integer verifierMemberId;

    private String value;

    private String body;

    private Integer relateTag =0;

    private String verifierType;
    private String[] createTime;
}
