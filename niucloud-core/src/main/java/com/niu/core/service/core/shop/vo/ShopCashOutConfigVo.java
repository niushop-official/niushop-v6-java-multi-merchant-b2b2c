package com.niu.core.service.core.shop.vo;

import cn.hutool.json.JSONArray;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * 微信配置视图
 */

@Data
public class ShopCashOutConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String isOpen = "1"; // 是否启用

    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String transferType; // 原始ID
    private String min = "0.01"; // 最低提现金额
    private String rate = "0"; // 提现手续费比率
    private String isAutoVerify = "0"; // 是否自动审核
    private String isAutoTransfer = "0"; // 是否自动转账

}