package com.niu.core.service.admin.marketing.param;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.io.Serializable;

/**
 * 新人礼配置参数
 */
@Data
public class NewcomerGiftConfigParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer isOpen = 0;

    private JSONObject gifts = new JSONObject();
}
