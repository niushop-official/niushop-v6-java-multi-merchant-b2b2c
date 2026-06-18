package com.niu.core.service.admin.member.param;

import lombok.Data;

@Data
public class MemberResetPasswordParam {
    private String password;
    private String confirmPassword;
    private Integer memberId;

}
