package com.niu.core.service.core.shop.param;

import cn.hutool.json.JSONArray;
import lombok.Data;

import java.io.Serializable;

/**
 * 店铺转账参数
 */

@Data
public class ShopTransferParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String transferType;

    private String transferRealName;

    private String transferMobile;

    private String transferBank;

    private String transferAccount;

    private String transferPaymentCode;

    private String openId = "";

    private String channel = "";



}