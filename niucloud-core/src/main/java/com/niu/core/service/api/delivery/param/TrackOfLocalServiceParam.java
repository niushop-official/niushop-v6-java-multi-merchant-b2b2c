package com.niu.core.service.api.delivery.param;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 同城配送轨迹 / 骑手位置查询参数
 * 对应 PHP trade_type / trade_no
 */
@Data
public class TrackOfLocalServiceParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("trade_type")
    @JsonAlias({"tradeType", "trade_type"})
    private String tradeType;

    @JsonProperty("trade_no")
    @JsonAlias({"tradeNo", "trade_no"})
    private String tradeNo;

    private Integer memberId;

    /** 兼容 GET 查询参数 trade_type */
    public void setTrade_type(String tradeType) {
        this.tradeType = tradeType;
    }

    /** 兼容 GET 查询参数 trade_no */
    public void setTrade_no(String tradeNo) {
        this.tradeNo = tradeNo;
    }
}
