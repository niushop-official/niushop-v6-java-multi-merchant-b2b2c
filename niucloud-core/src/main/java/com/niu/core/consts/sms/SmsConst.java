package com.niu.core.consts.sms;

import java.util.List;

public class SmsConst {

    public static final String LOGIN = "login";
    public static final String REGISTER = "register";
    public static final String BIND_MOBILE = "bind_mobile";
    public static final String FIND_PASS = "find_pass";
    public static final String MERCHANT_SETTLED = "merchant_settled";

    public static final List<String> SCENE_TYPE = List.of(LOGIN, REGISTER, BIND_MOBILE, FIND_PASS,MERCHANT_SETTLED);

}