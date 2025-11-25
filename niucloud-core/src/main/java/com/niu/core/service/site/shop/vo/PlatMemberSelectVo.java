package com.niu.core.service.site.shop.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 平台会员选择VO
 */
@Data
public class PlatMemberSelectVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer memberId;

    private String nickname;

    private String memberNo;
}

