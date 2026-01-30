package com.niu.core.service.admin.sms.param;

import lombok.Data;

import java.util.List;

@Data
public class SignDeleteParam {
    private List<String> signatures;

    private String password;

    private String remark;
}
