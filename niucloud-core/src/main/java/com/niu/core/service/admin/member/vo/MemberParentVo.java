package com.niu.core.service.admin.member.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 上级推荐人视图
 */
@Data
public class MemberParentVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer memberId;
    private String nickname;
    private String headimg;
}
