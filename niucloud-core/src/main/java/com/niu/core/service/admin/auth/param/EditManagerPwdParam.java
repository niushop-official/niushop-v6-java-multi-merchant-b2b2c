package com.niu.core.service.admin.auth.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class EditManagerPwdParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private  Integer id;
    private  String password;
    private String confirmPassword;
}
