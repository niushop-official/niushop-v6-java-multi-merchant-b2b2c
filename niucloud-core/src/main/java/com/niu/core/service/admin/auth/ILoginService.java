package com.niu.core.service.admin.auth;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.auth.param.AdminForgetPasswordParam;
import com.niu.core.service.admin.auth.param.SendMobileParam;
import com.niu.core.service.admin.auth.param.UserLoginParam;
import com.niu.core.service.admin.auth.param.UserMobileLoginParam;
import com.niu.core.service.admin.auth.vo.LoginResultVo;

/**
 * 用户登录服务层
 */
public interface ILoginService {

    /**
     * 账号密码登录
     * @param userLoginParam
     * @return
     */
    LoginResultVo account(UserLoginParam userLoginParam);

    /**
     * 手机号登录
     * @return
     */
    LoginResultVo mobile(UserMobileLoginParam param);

    /**
     * 手机发送验证码
     * @return
     */
    JSONObject sendMobileCode(SendMobileParam param);

    /**
     * 忘记密码重置
     * @param param 手机号、新密码、短信验证码
     */
    void resetPassword(AdminForgetPasswordParam param);

    /**
     * 用戶登出
     *
     */
    void logout();

    /**
     * 清理token
     * @param uid
     * @param appType
     * @param token
     */
    void clearToken(Integer uid, String appType, String token);
}
