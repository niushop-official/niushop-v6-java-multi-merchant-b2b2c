package com.niu.core.service.admin.sys.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserResetPasswordParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String password;
}
