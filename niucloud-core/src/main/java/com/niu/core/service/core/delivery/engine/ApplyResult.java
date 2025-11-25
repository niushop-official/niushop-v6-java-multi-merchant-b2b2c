package com.niu.core.service.core.delivery.engine;

import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class ApplyResult {
    private boolean success;
    private String reason;
    private Integer resultCode;
    private String printTemplate;
    private JSONObject orderInfo;
    private String orderNo;
}
