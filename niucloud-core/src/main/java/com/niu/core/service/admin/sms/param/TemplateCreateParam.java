package com.niu.core.service.admin.sms.param;

import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class TemplateCreateParam {
    private String templateKey;
    private String templateType;
    private JSONObject paramsJson;
    private String templateId;
}
