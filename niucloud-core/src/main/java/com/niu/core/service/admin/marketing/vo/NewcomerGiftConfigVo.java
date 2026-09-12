package com.niu.core.service.admin.marketing.vo;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.io.Serializable;

/**
 * 新人礼配置
 */
@Data
public class NewcomerGiftConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer isOpen = 0;

    private JSONObject gifts = new JSONObject();
}
