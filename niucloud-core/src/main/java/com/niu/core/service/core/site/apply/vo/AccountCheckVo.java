package com.niu.core.service.core.site.apply.vo;

import lombok.Data;

@Data
public class AccountCheckVo {
    private Integer uid;
    private String username;
    private String headImg;
    private String mobile;  // 脱敏后的手机号
}
