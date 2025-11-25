package com.niu.core.service.site.shop.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 平台会员选择参数
 */
@Data
public class PlatMemberSelectParam implements Serializable {

    private static final long serialVersionUID = 1L;
    private String keyword;
}

