package com.niu.core.service.core.delivery.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ElectronicSheetConfigValueVo {

    @JsonProperty("interface_type")
    @JSONField(name = "interface_type")
    private String interfaceType = "kdbird";
    @JsonProperty("kdniao_id")
    @JSONField(name = "kdniao_id")
    private String kdniaoId = "";
    @JsonProperty("kdniao_api_key")
    @JSONField(name = "kdniao_api_key")
    private String kdniaoApiKey = "";
    @JsonProperty("server_port1")
    @JSONField(name = "server_port1")
    private String serverPort1 = "8000";
    @JsonProperty("server_port2")
    @JSONField(name = "server_port2")
    private String serverPort2 = "18000";
    @JsonProperty("https_port")
    @JSONField(name = "https_port")
    private String httpsPort = "8443";

    public static ElectronicSheetConfigValueVo newInstance() {
        return new ElectronicSheetConfigValueVo();
    }
}
