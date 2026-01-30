package com.niu.core.service.admin.sms.param;

import lombok.Data;

@Data
public class SendListParam {
    private String content;

    private String mobile;

    private String smsStatus;
}
