package com.niu.core.enums.member;


import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;

/**
 * 会员权益
 */
public class BenefitsEnum {

    private static final JsonModuleLoader JSON_MODULE_LOADER = new JsonModuleLoader();

    public static JSONObject getType() {
        return JSON_MODULE_LOADER.mergeResultElement(RequestUtils.siteId(), "member/benefits.json");
    }
}
